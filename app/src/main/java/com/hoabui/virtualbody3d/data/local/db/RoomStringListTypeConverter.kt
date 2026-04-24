package com.hoabui.virtualbody3d.data.local.db

import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONException

/**
 * Room persistence for [List] of [String] as JSON text (e.g. `["A","B"]`).
 */
class RoomStringListTypeConverter {
    @TypeConverter
    fun fromJson(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(value)
            buildList {
                for (i in 0 until arr.length()) {
                    add(arr.getString(i))
                }
            }
        } catch (_: JSONException) {
            emptyList()
        }
    }

    @TypeConverter
    fun toJson(list: List<String>?): String {
        if (list.isNullOrEmpty()) return "[]"
        return JSONArray(list).toString()
    }
}
