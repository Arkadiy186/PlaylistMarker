package com.example.playlistmarker.ui.search.utills.debounce

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DebounceHandlerImpl : DebounceHandler {
    override fun <T> debounce(
        delayMillis: Long,
        coroutineScope: CoroutineScope,
        useLastParam: Boolean,
        action: (T) -> Unit
    ): (T) -> Unit {
        var debounceJob: Job? = null
        return { param: T ->
            Log.d("DebounceHandler", "here")
            if (useLastParam) {
                Log.d("DebounceHandler", "job.cancel")
                debounceJob?.cancel()
            }
            if (debounceJob?.isCompleted == true || useLastParam) {
                Log.d("DebounceHandler", "job.launch")
                debounceJob = coroutineScope.launch {
                    delay(delayMillis)
                    action(param)
                }
            }
        }
    }
}