package com.example.playlistmarker.ui.search.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmarker.R
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.data.search.sharedpreferences.SearchStateData
import com.example.playlistmarker.databinding.FragmentSearchBinding
import com.example.playlistmarker.domain.search.model.Track
import com.example.playlistmarker.domain.search.use_cases.HistoryInteractor
import com.example.playlistmarker.domain.search.use_cases.SearchStateInteractor
import com.example.playlistmarker.ui.mapper.TrackInfoDetailsMapper
import com.example.playlistmarker.ui.search.model.TrackInfoDetails
import com.example.playlistmarker.ui.search.recyclerview.TrackAdapter
import com.example.playlistmarker.ui.search.ui_state.UiHistoryHandler
import com.example.playlistmarker.ui.search.ui_state.UiHistoryHandlerImpl
import com.example.playlistmarker.ui.search.ui_state.UiStateHandler
import com.example.playlistmarker.ui.search.ui_state.UiStateHandlerImpl
import com.example.playlistmarker.ui.search.utills.debounce.DebounceHandler
import com.example.playlistmarker.ui.search.utills.hidekeyboard.HideKeyboardHelper
import com.example.playlistmarker.ui.search.utills.sharing.NavigationContract
import com.example.playlistmarker.ui.search.utills.sharing.NavigationContractImpl
import com.example.playlistmarker.ui.search.viewmodel.historyviewmodel.HistoryViewModel
import com.example.playlistmarker.ui.search.viewmodel.searchviewmodel.SearchViewModel
import com.example.playlistmarker.ui.search.viewmodel.searchviewmodel.UiState
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel: SearchViewModel by viewModel()
    private val historyViewModel: HistoryViewModel by viewModel()
    private lateinit var uiHistoryHandler: UiHistoryHandler
    private lateinit var uiStateHandler: UiStateHandler
    private lateinit var navigationContract: NavigationContract
    private lateinit var onTrackClickDebounce: (TrackInfoDetails) -> Unit

    private val historyInteractor: HistoryInteractor by inject()
    private val debounceHandler: DebounceHandler by lazy { Creator.provideDebounceHandler() }
    private val hideKeyboardHelper: HideKeyboardHelper by lazy { Creator.provideHideKeyboardHelper() }

    private val searchList = mutableListOf<TrackInfoDetails>()
    private val historyTrack = mutableListOf<TrackInfoDetails>()
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationContract = NavigationContractImpl(requireContext())

        onTrackClickDebounce = debounceHandler.debounce(CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            useLastParam = true
        ) { track ->
            Log.d("Debounce", "clickDebounce")
            val domainTrack = TrackInfoDetailsMapper.mapToDomain(track)
            historyInteractor.addTrackHistory(domainTrack)
            navigationContract.openAudioPlayer(track)
        }

        searchAdapter = TrackAdapter(searchList) { track -> onTrackClickDebounce(track) }
        historyAdapter = TrackAdapter(historyTrack) { track -> onTrackClickDebounce(track) }

        uiHistoryHandler = UiHistoryHandlerImpl(binding, this, historyAdapter, searchAdapter)
        uiStateHandler = UiStateHandlerImpl(binding, this)

        setupListeners()

        binding.clearButton.isVisible = !binding.searchEditText.text.isNullOrEmpty()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = searchAdapter

        observeViewModels()
    }

    override fun onResume() {
        super.onResume()
        binding.searchEditText.clearFocus()
        binding.searchEditText.setText("")
        searchList.clear()
        searchAdapter.notifyDataSetChanged()
    }

    private fun setupListeners() {
        binding.historySearchButtonView.setOnClickListener {
            historyViewModel.clearHistory()
            historyTrack.clear()
            historyAdapter.notifyDataSetChanged()
            uiHistoryHandler.historySetVisibility(false)
        }

        binding.clearButton.setOnClickListener {
            binding.searchEditText.setText("")
            searchList.clear()
            binding.searchEditText.clearFocus()
            searchAdapter.notifyDataSetChanged()
            hideKeyboardHelper.hideKeyboard(binding.clearButton)
            uiHistoryHandler.historySetVisibility(false)
        }

        binding.placeholderErrorButton.setOnClickListener {
            uiStateHandler.placeholderSetVisibility(isHidden = true)
            searchViewModel.searchTrack(binding.searchEditText.text.toString())
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            if (hasFocus && binding.searchEditText.text.isEmpty()) {

                historyViewModel.loadHistory()

                bottomNavigationView.visibility = View.GONE

                historyViewModel.historyState.observe(viewLifecycleOwner) { history ->

                    historyTrack.clear()
                    historyTrack.addAll(history)
                    historyAdapter.notifyDataSetChanged()

                    val shouldShowHistory = history.isNotEmpty()
                    uiHistoryHandler.historySetVisibility(shouldShowHistory)
                }
            } else {
                bottomNavigationView.visibility = View.VISIBLE
                uiHistoryHandler.historySetVisibility(false)
            }
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("UiHistoryHandler", "textChanged")
                handleSearchTextChange(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun handleSearchTextChange(s: CharSequence?) {
        binding.clearButton.isVisible = !s.isNullOrEmpty()


        if(binding.searchEditText.hasFocus() && s?.isEmpty() == true) {
            if (historyTrack.isNotEmpty()) {
                uiHistoryHandler.historySetVisibility(true)
            } else {
                searchList.clear()
                searchAdapter.notifyDataSetChanged()
            }
        } else {
            searchViewModel.searchDebounce(s.toString())
            uiHistoryHandler.historySetVisibility(false)
        }

        if (s.isNullOrEmpty()) {
            uiStateHandler.placeholderSetVisibility(true)
        }
    }

    private fun observeViewModels() {
        searchViewModel.combinedLiveData.observe(viewLifecycleOwner) { (uiState, searchState) ->
            uiState?.let { handleUiState(it) }
            searchState?.let { handleSearchState(it) }
        }
    }

    private fun showTracks(track: List<TrackInfoDetails>) {
        requireActivity().runOnUiThread {
            searchList.clear()
            searchList.addAll(track)
            searchAdapter.notifyDataSetChanged()
            uiStateHandler.placeholderSetVisibility(isHidden = true)
        }
    }

    private fun handleUiState(uiState: UiState) {
        when (uiState) {
            is UiState.NotFound -> {
                uiStateHandler.showLoading(false)
                uiStateHandler.showNotFound()
            }
            is UiState.ErrorInternet -> {
                uiStateHandler.showLoading(false)
                uiStateHandler.showErrorInternet(uiState.message)
            }
            is UiState.Loading -> uiStateHandler.showLoading(uiState.isLoading)
            is UiState.Content -> {
                uiStateHandler.showLoading(false)
                showTracks(uiState.tracks)
            }
        }
    }

    private fun handleSearchState(searchState: SearchStateData) {
        binding.searchEditText.setText(searchState.searchText)
        searchList.clear()
        searchList.addAll(searchState.searchList)
        searchAdapter.notifyDataSetChanged()
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}