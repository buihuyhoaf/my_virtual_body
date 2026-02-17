package com.example.myvirtualbody.viewer

import io.github.sceneview.node.ModelNode

/**
 * Holder for the loaded 3D model node. Used so the UI or a ViewModel can later
 * control morph targets (e.g. body fat) via [modelNode]?.setMorphWeights(weights).
 *
 * ModelNode.setMorphWeights(weights: FloatArray, offset: Int) updates vertex
 * morphing weights; the GLB must define morph targets (blend shapes) for this to have an effect.
 */
data class ModelViewerState(
    val modelNode: ModelNode? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
