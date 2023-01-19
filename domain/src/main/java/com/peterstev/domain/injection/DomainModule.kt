package com.peterstev.domain.injection

import com.peterstev.domain.inversion.LocalUseCaseImpl
import com.peterstev.domain.inversion.SearchUseCaseImpl
import com.peterstev.domain.usecase.LocalUseCase
import com.peterstev.domain.usecase.SearchUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class DomainModule {

    @Binds
    internal abstract fun bindSearchUseCase(
        useCaseImpl: SearchUseCaseImpl,
    ): SearchUseCase

    @Binds
    internal abstract fun bindWeatherUseCase(
        favouritesUseCaseImpl: LocalUseCaseImpl,
    ): LocalUseCase

}
