package com.peterstev

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.spekframework.spek2.Spek

@ExperimentalCoroutinesApi
open class LiveDataSpek : Spek({

    beforeEachTest {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }
        })
    }

    afterEachTest {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
})
