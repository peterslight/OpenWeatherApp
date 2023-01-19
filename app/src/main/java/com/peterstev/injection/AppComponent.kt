package com.peterstev.injection

import android.app.Activity
import androidx.fragment.app.Fragment
import com.peterstev.data.injection.ApiModule
import com.peterstev.data.injection.DataModule
import com.peterstev.database.injection.DatabaseModule
import com.peterstev.database.injection.DatabaseRepoModule
import com.peterstev.domain.injection.DomainModule
import com.peterstev.fragment.LaterFragment
import com.peterstev.fragment.MainFragment
import com.peterstev.fragment.TodayFragment
import com.peterstev.fragment.TomorrowFragment
import com.peterstev.viewmodel.TomorrowViewModel
import com.peterstev.viewmodel.WeatherViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DomainModule::class,
        DatabaseModule::class,
        DatabaseRepoModule::class,
        DataModule::class,
        ApiModule::class,
        AppModule::class,
    ]
)
interface AppComponent {

    val sharedViewModel: Provider<WeatherViewModel>
    val tomorrowViewModel: Provider<TomorrowViewModel>

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance activity: Activity): AppComponent
    }

    fun inject(fragment: TodayFragment)
    fun inject(fragment: TomorrowFragment)
    fun inject(fragment: LaterFragment)
    fun inject(mainFragment: MainFragment)

    companion object {
        fun create(fragment: Fragment): AppComponent {
            return DaggerAppComponent.factory().create(fragment.requireActivity())
        }
    }
}
