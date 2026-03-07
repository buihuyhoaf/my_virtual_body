package com.hoabui.virtualbody3d.ui.body.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.google.android.filament.Engine
import com.google.android.filament.gltfio.FilamentInstance
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.EnvironmentLoader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton holder for shared Filament engine, loaders, and preloaded body model instance.
 * Populated by [BodyModelPreload]; consumed by [BodyModelPreview] to avoid loading the model again on Home.
 */
@Singleton
class BodyModelProvider @Inject constructor() {

    @Volatile
    private var engine: Engine? = null

    @Volatile
    private var modelLoader: ModelLoader? = null

    @Volatile
    private var materialLoader: MaterialLoader? = null

    @Volatile
    private var environmentLoader: EnvironmentLoader? = null

    @Volatile
    private var preloadedInstance: FilamentInstance? = null

    fun getEngine(): Engine? = engine
    fun getModelLoader(): ModelLoader? = modelLoader
    fun getMaterialLoader(): MaterialLoader? = materialLoader
    fun getEnvironmentLoader(): EnvironmentLoader? = environmentLoader
    fun getPreloadedInstance(): FilamentInstance? = preloadedInstance

    fun isReady(): Boolean = engine != null && preloadedInstance != null

    /**
     * Called by [BodyModelPreload] when engine and model are ready.
     * Must be invoked on the main thread.
     */
    fun setShared(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        environmentLoader: EnvironmentLoader,
        instance: FilamentInstance
    ) {
        this.engine = engine
        this.modelLoader = modelLoader
        this.materialLoader = materialLoader
        this.environmentLoader = environmentLoader
        this.preloadedInstance = instance
    }
}

val LocalBodyModelProvider = staticCompositionLocalOf<BodyModelProvider> {
    error("No BodyModelProvider provided. Wrap with CompositionLocalProvider(LocalBodyModelProvider provides bodyModelProvider)")
}
