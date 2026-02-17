# MyVirtualBody – 3D GLB Viewer (SceneView + Jetpack Compose)

## Overview

The app displays a GLB 3D model using **SceneView** (`io.github.sceneview:sceneview`) inside **Jetpack Compose** via **AndroidView**. It is set up so you can later drive **morph targets** (e.g. body fat) from a ViewModel or UI.

---

## 1. Project-level `build.gradle.kts`

- Declares AGP and Kotlin Compose plugins (applied in the app module).
- No extra dependencies here; the app module pulls SceneView and Compose.

---

## 2. App-level `build.gradle.kts`

- **compileSdk 34**, **targetSdk 34**, **minSdk 26** as requested.
- **Java 17** and **jvmTarget = "17"** for Compose and modern toolchain.
- **SceneView** dependency: `io.github.sceneview:sceneview:2.3.3`.
- **packaging** excludes some META-INF entries to avoid conflicts with native/Filament libs.

---

## 3. AndroidManifest.xml

- **OpenGL ES 3.0** (`uses-feature android:glEsVersion="0x00030000"`) for 3D rendering; no ARCore.
- Single **MainActivity** as launcher; theme and app name come from resources.

---

## 4. MainActivity.kt (Compose)

- **Edge-to-edge** and **setContent** with `MyVirtualBodyTheme`.
- Content is a single full-screen composable: **`ModelViewerScreen(Modifier.fillMaxSize())`**.
- No XML layouts; everything is Compose.

---

## 5. 3D viewer: `ModelViewerScreen.kt`

### SceneView via AndroidView

- The 3D view is the **SceneView** (View) from the SceneView library, embedded in Compose with **`AndroidView`**.
- All Filament/SceneView pieces (engine, loaders, scene, view, renderer, camera, light, environment, manipulator) are created with the library’s **remember*** Composables and then passed into **SceneView** as **shared*** and **cameraManipulator** so that:
  - One engine/render path is used.
  - Lighting and camera are under your control.

### GLB loading and centering

- **Asset path:** `models/female_gym_outfit_sports_bra_and_leggins.glb`  
  File must be at:  
  `app/src/main/assets/models/female_gym_outfit_sports_bra_and_leggins.glb`
- Loading is done with **`modelLoader.createModelInstance(MODEL_ASSET)`** on the main thread (Filament/gltfio requirement), inside a **LaunchedEffect** when **sceneViewRef** is set.
- The model is added as a **ModelNode** with:
  - **scaleToUnits = 1.2f** so it fits a consistent size.
  - **centerOrigin = Position(0, 0, 0)** so it is centered in the scene.

### Touch rotation and pinch-to-zoom

- **`rememberCameraManipulator(orbitHomePosition, targetPosition)`** is passed into **SceneView** as **cameraManipulator**.
- SceneView uses this to provide:
  - **Orbit / rotation** with touch.
  - **Pinch-to-zoom** (and typical camera gestures).

No extra gesture code is needed in the composable.

### Lighting

- **`rememberMainLightNode(engine) { intensity = 100_000f }`** provides a strong direct light (needed for shadows and clear shading).
- **`rememberEnvironment(environmentLoader) { SceneView.createEnvironment(environmentLoader, isOpaque = true) }`** provides the Filament environment (ambient/skybox). You can later swap in an HDR environment here if you add an HDR asset.

### Mobile-friendly behavior

- Single shared engine, loaders, and scene (no duplicate heavy resources).
- Model loaded once and reused; loading runs on main thread but only once when the viewer is shown.
- Lifecycle is wired via **sharedLifecycle** / **sceneView.lifecycle** so the view stops rendering when the screen is not active.

### Morph targets (body fat) – architecture

- **`ModelViewerState`** holds **`modelNode: ModelNode?`** (and `isLoading`, `error`).
- **`ModelViewerScreen`** updates this state and notifies the parent via **`onStateUpdated(ModelViewerState)`**.
- When you add a ViewModel or parent state:
  - Keep a **`ModelViewerState`** (or equivalent) that receives **modelNode** from the viewer.
  - When the user changes “body fat” (or any morph), call:
    - **`modelNode?.setMorphWeights(weights, offset)`**
- The GLB must define **morph targets** (blend shapes) for those weights to have an effect. You can inspect **`modelNode?.nodes`** and related APIs to discover morph target names/count and drive them from sliders or other UI.

---

## 6. `ModelViewerState.kt`

- **modelNode**: The loaded **ModelNode**; use it later for **setMorphWeights** and other per-model controls.
- **isLoading** / **error**: For loading and error UI (e.g. the progress and error text in **ModelViewerScreen**).

---

## Summary

| Requirement              | How it’s done                                                                 |
|--------------------------|-------------------------------------------------------------------------------|
| Jetpack Compose only     | No XML layouts; Compose setContent and ModelViewerScreen.                    |
| SceneView via AndroidView| AndroidView(factory = { SceneView(...) }, update = { ... }).                  |
| GLB from assets          | `models/female_gym_outfit_sports_bra_and_leggins.glb` + createModelInstance. |
| Touch rotation           | rememberCameraManipulator passed into SceneView.                             |
| Pinch to zoom            | Same cameraManipulator.                                                       |
| Lighting                 | rememberMainLightNode + rememberEnvironment.                                  |
| Center model             | ModelNode with centerOrigin = Position(0,0,0), scaleToUnits = 1.2f.          |
| Mobile-friendly          | Shared engine/loaders, lifecycle, single load.                               |
| Target SDK 34, min 26     | Set in app build.gradle.kts.                                                  |
| No ARCore                | Only OpenGL ES 3.0 in manifest; no AR dependencies.                          |
| Morph targets later      | ModelViewerState.modelNode + onStateUpdated; use setMorphWeights when ready.|

Place your GLB at **`app/src/main/assets/models/female_gym_outfit_sports_bra_and_leggins.glb`** and run the app to see the 3D model with rotation and pinch-to-zoom.
