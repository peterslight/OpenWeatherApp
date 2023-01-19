package com.peterstev.database.injection

import com.peterstev.database.inversion.LocalRepositoryImpl
import com.peterstev.domain.repository.LocalRepository
import dagger.Binds
import dagger.Module

@Module
abstract class DatabaseRepoModule {

    @Binds
    internal abstract fun bindFavouriteRepository(
        repositoryImpl: LocalRepositoryImpl,
    ): LocalRepository

}
