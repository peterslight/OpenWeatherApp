package com.peterstev.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Provider

fun printLog(message: Any) {
    println("hhhh $message")
}

fun ImageView.loadImage(url: String) {
    Glide.with(this.context).load(url).into(this)
}

fun <T> Single<T>.thread(schedulers: RxSchedulers): Single<T> {
    return this
        .subscribeOn(schedulers.IO)
        .observeOn(schedulers.MAIN)
}

fun <T> Observable<T>.thread(schedulers: RxSchedulers): Observable<T> {
    return this
        .subscribeOn(schedulers.IO)
        .observeOn(schedulers.MAIN)
}

fun <T> Flowable<T>.thread(schedulers: RxSchedulers): Flowable<T> {
    return this
        .subscribeOn(schedulers.IO)
        .observeOn(schedulers.MAIN)
}

fun View?.hide() {
    this?.visibility = View.GONE
}

fun View?.show() {
    this?.visibility = View.VISIBLE
}

fun Fragment.hideKeyboard() = ViewCompat
    .getWindowInsetsController(requireView())
    ?.hide(WindowInsetsCompat.Type.ime())

fun Fragment.networkIsAvailable(): Boolean {
    val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val netCapability = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        netCapability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        netCapability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        netCapability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        netCapability.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}

inline fun <reified VM : ViewModel> Fragment.daggerParentFragmentViewModel(
    crossinline getProvider: () -> Provider<VM>,
) = lazy {
    ViewModelProviders.of(requireActivity(), getProvider().createFactory()).get(VM::class.java)
}

inline fun <reified VM : ViewModel> Provider<VM>.createFactory() = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return this@createFactory.get() as T
    }
}
