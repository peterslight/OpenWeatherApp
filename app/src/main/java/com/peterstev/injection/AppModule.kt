package com.peterstev.injection

import android.app.Activity
import android.app.Application
import com.peterstev.util.BaseSchedulerProvider
import com.peterstev.util.RxSchedulers
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun providesRxScheduler(): RxSchedulers {
        return BaseSchedulerProvider()
    }

    @Provides
    fun providesApplication(activity: Activity): Application {
        return activity.application
    }
}
