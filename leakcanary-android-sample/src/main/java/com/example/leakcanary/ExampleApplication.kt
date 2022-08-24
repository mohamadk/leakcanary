/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.leakcanary

import android.app.Application
import android.app.Dialog
import android.app.Service
import android.os.StrictMode
import android.view.View
import leakcanary.EventListener
import leakcanary.EventListener.Event.HeapAnalysisDone
import leakcanary.LeakCanary
import org.leakcanary.internal.LeakUiAppClient

open class ExampleApplication : Application() {
  val leakedViews = mutableListOf<View>()
  val leakedDialogs = mutableListOf<Dialog>()
  val leakedServices = mutableListOf<Service>()

  override fun onCreate() {
    super.onCreate()
    enabledStrictMode()

    // TODO This doesn't compile in release mode where there's no LeakCanary.
    LeakCanary.config = LeakCanary.config.run {
      copy(eventListeners = eventListeners + EventListener {
        // TODO Move this into an EventListener class, maybe the standard one
        //  TODO Detect if app installed or not and delegate to std leakcanary if not.
        if (it is HeapAnalysisDone<*>) {
          LeakUiAppClient(this@ExampleApplication).sendHeapAnalysis(it.heapAnalysis)
        }
      })
    }
  }

  private fun enabledStrictMode() {
    StrictMode.setThreadPolicy(
      StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .penaltyDeath()
        .build()
    )
  }
}
