package com.hoabui.virtualbody3d.ui.body

import android.content.Context
import com.hoabui.virtualbody3d.ui.body.state.GlbSceneBounds
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap

data class GlbMetadata(
    val bounds: GlbSceneBounds?,
    val maxJointCount: Int?
)

/** Cache for GLB metadata (bounds + maxJointCount) by asset path. */
object GlbMetadataCache {
    private val cache = ConcurrentHashMap<String, GlbMetadata>()
    fun getOrPut(
        context: Context,
        path: String,
        parse: (Context, String) -> GlbMetadata
    ): GlbMetadata = cache.getOrPut(path) { parse(context, path) }
}

/**
 * Reads the GLB asset once and parses both scene bounds and max joint count from the JSON chunk.
 */
fun parseGlbMetadata(context: Context, assetPath: String): GlbMetadata {
    val data = runCatching { context.assets.open(assetPath).use { it.readBytes() } }.getOrNull()
        ?: return GlbMetadata(bounds = null, maxJointCount = null)
    if (data.size < 20) return GlbMetadata(bounds = null, maxJointCount = null)
    val header = ByteBuffer.wrap(data, 0, 12).order(ByteOrder.LITTLE_ENDIAN)
    if (header.int != 0x46546C67) return GlbMetadata(bounds = null, maxJointCount = null)
    var offset = 12
    while (offset + 8 <= data.size) {
        val chunkHeader = ByteBuffer.wrap(data, offset, 8).order(ByteOrder.LITTLE_ENDIAN)
        val chunkLength = chunkHeader.int
        val chunkType = chunkHeader.int
        offset += 8
        if (chunkLength < 0 || offset + chunkLength > data.size) break
        if (chunkType != 0x4E4F534A) {
            offset += chunkLength
            continue
        }
        val jsonText = String(data, offset, chunkLength, Charset.forName("UTF-8"))
        val root = runCatching { JSONObject(jsonText) }.getOrNull() ?: break
        val accessors = root.optJSONArray("accessors") ?: break
        val meshes = root.optJSONArray("meshes") ?: break
        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var minZ = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        var maxZ = Float.NEGATIVE_INFINITY
        fun mergeAccessor(accIndex: Int) {
            val acc = accessors.optJSONObject(accIndex) ?: return
            val minArr = acc.optJSONArray("min") ?: return
            val maxArr = acc.optJSONArray("max") ?: return
            if (minArr.length() < 3 || maxArr.length() < 3) return
            val mx = minArr.optDouble(0).toFloat()
            val my = minArr.optDouble(1).toFloat()
            val mz = minArr.optDouble(2).toFloat()
            val bigX = maxArr.optDouble(0).toFloat()
            val bigY = maxArr.optDouble(1).toFloat()
            val bigZ = maxArr.optDouble(2).toFloat()
            if (mx < minX) minX = mx
            if (my < minY) minY = my
            if (mz < minZ) minZ = mz
            if (bigX > maxX) maxX = bigX
            if (bigY > maxY) maxY = bigY
            if (bigZ > maxZ) maxZ = bigZ
        }
        for (i in 0 until meshes.length()) {
            val mesh = meshes.optJSONObject(i) ?: continue
            val primitives = mesh.optJSONArray("primitives") ?: continue
            for (j in 0 until primitives.length()) {
                val prim = primitives.optJSONObject(j) ?: continue
                val attrs = prim.optJSONObject("attributes") ?: continue
                val posIndex = attrs.optInt("POSITION", -1)
                if (posIndex in 0 until accessors.length()) mergeAccessor(posIndex)
            }
        }
        val bounds = if (minX == Float.POSITIVE_INFINITY) null
        else GlbSceneBounds(minX, minY, minZ, maxX, maxY, maxZ)
        var maxJointCount = 0
        val skins = root.optJSONArray("skins")
        if (skins != null) {
            for (i in 0 until skins.length()) {
                val skin = skins.optJSONObject(i) ?: continue
                val joints = skin.optJSONArray("joints") ?: continue
                if (joints.length() > maxJointCount) maxJointCount = joints.length()
            }
        }
        return GlbMetadata(bounds = bounds, maxJointCount = if (maxJointCount > 0) maxJointCount else null)
    }
    return GlbMetadata(bounds = null, maxJointCount = null)
}
