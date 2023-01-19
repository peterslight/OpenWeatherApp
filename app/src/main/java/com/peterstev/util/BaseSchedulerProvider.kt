package com.peterstev.util

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class BaseSchedulerProvider @Inject constructor() : RxSchedulers {
    override val IO: Scheduler
        get() = Schedulers.io()
    override val MAIN: Scheduler
        get() = AndroidSchedulers.mainThread()
}
