package com.hoabui.virtualbody3d.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_scan_results")
data class BodyScanResultEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 1,
    @ColumnInfo(name = "payload_json")
    val payloadJson: String,
)
