package com.chatguru.ai.utils

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object RequestThrottler {
    private val mutex = Mutex()
    private var lastRequestTime = 0L
    private val minDelayBetweenRequests = 6000L // 6 seconds = max 10 req/min

    suspend fun <T> throttle(block: suspend () -> T): T {
        return mutex.withLock {
            val now = System.currentTimeMillis()
            val timeSinceLastRequest = now - lastRequestTime

            Log.d("RequestThrottler", "⏱️ Time since last request: ${timeSinceLastRequest}ms")

            if (timeSinceLastRequest < minDelayBetweenRequests) {
                val delayNeeded = minDelayBetweenRequests - timeSinceLastRequest
                Log.d("RequestThrottler", "⏸️ Delaying for ${delayNeeded}ms (${delayNeeded/1000}s)")
                delay(delayNeeded)
            }

            lastRequestTime = System.currentTimeMillis()
            Log.d("RequestThrottler", "✅ Executing request at ${lastRequestTime}")
            block()
        }
    }
}
