package com.hoabui.virtualbody3d.di

import com.hoabui.virtualbody3d.data.local.dictionary.MuscleDictionaryFromAssets
import com.hoabui.virtualbody3d.domain.model.exercise.MuscleDictionary
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MuscleDictionaryModule {
    @Binds
    @Singleton
    abstract fun bindMuscleDictionary(
        impl: MuscleDictionaryFromAssets,
    ): MuscleDictionary
}
