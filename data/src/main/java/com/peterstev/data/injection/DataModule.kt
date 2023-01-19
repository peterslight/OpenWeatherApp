package com.peterstev.data.injection

import com.peterstev.data.inversion.SearchRepositoryImpl
import com.peterstev.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module

@Module
abstract class DataModule {

    @Binds
    internal abstract fun bindSearchRepository(
        baseRepositoryImpl: SearchRepositoryImpl,
    ): SearchRepository
}
