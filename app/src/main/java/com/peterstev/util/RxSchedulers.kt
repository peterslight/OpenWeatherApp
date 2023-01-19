package com.peterstev.util

import io.reactivex.rxjava3.core.Scheduler

interface RxSchedulers {
    val IO: Scheduler
    val MAIN: Scheduler
}


