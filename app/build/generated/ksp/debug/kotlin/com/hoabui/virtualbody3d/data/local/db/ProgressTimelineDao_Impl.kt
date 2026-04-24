package com.hoabui.virtualbody3d.`data`.local.db

import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ProgressTimelineDao_Impl(
  __db: RoomDatabase,
) : ProgressTimelineDao {
  private val __db: RoomDatabase
  init {
    this.__db = __db
  }

  public override fun observeAllByDate(): Flow<List<ProgressSnapshotEntity>> {
    val _sql: String = "SELECT * FROM progress_snapshots ORDER BY date_iso ASC"
    return createFlow(__db, false, arrayOf("progress_snapshots")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfRowId: Int = getColumnIndexOrThrow(_stmt, "row_id")
        val _columnIndexOfDateIso: Int = getColumnIndexOrThrow(_stmt, "date_iso")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "image_url")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weight_kg")
        val _columnIndexOfBodyFatPercent: Int = getColumnIndexOrThrow(_stmt, "body_fat_percent")
        val _columnIndexOfMuscleMassKg: Int = getColumnIndexOrThrow(_stmt, "muscle_mass_kg")
        val _result: MutableList<ProgressSnapshotEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProgressSnapshotEntity
          val _tmpRowId: Long
          _tmpRowId = _stmt.getLong(_columnIndexOfRowId)
          val _tmpDateIso: String
          _tmpDateIso = _stmt.getText(_columnIndexOfDateIso)
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpWeightKg: Float?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg).toFloat()
          }
          val _tmpBodyFatPercent: Float?
          if (_stmt.isNull(_columnIndexOfBodyFatPercent)) {
            _tmpBodyFatPercent = null
          } else {
            _tmpBodyFatPercent = _stmt.getDouble(_columnIndexOfBodyFatPercent).toFloat()
          }
          val _tmpMuscleMassKg: Float?
          if (_stmt.isNull(_columnIndexOfMuscleMassKg)) {
            _tmpMuscleMassKg = null
          } else {
            _tmpMuscleMassKg = _stmt.getDouble(_columnIndexOfMuscleMassKg).toFloat()
          }
          _item =
              ProgressSnapshotEntity(_tmpRowId,_tmpDateIso,_tmpImageUrl,_tmpWeightKg,_tmpBodyFatPercent,_tmpMuscleMassKg)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestSnapshotOnOrBefore(dateIso: String):
      ProgressSnapshotEntity? {
    val _sql: String =
        "SELECT * FROM progress_snapshots WHERE date_iso <= ? ORDER BY date_iso DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, dateIso)
        val _columnIndexOfRowId: Int = getColumnIndexOrThrow(_stmt, "row_id")
        val _columnIndexOfDateIso: Int = getColumnIndexOrThrow(_stmt, "date_iso")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "image_url")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weight_kg")
        val _columnIndexOfBodyFatPercent: Int = getColumnIndexOrThrow(_stmt, "body_fat_percent")
        val _columnIndexOfMuscleMassKg: Int = getColumnIndexOrThrow(_stmt, "muscle_mass_kg")
        val _result: ProgressSnapshotEntity?
        if (_stmt.step()) {
          val _tmpRowId: Long
          _tmpRowId = _stmt.getLong(_columnIndexOfRowId)
          val _tmpDateIso: String
          _tmpDateIso = _stmt.getText(_columnIndexOfDateIso)
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpWeightKg: Float?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg).toFloat()
          }
          val _tmpBodyFatPercent: Float?
          if (_stmt.isNull(_columnIndexOfBodyFatPercent)) {
            _tmpBodyFatPercent = null
          } else {
            _tmpBodyFatPercent = _stmt.getDouble(_columnIndexOfBodyFatPercent).toFloat()
          }
          val _tmpMuscleMassKg: Float?
          if (_stmt.isNull(_columnIndexOfMuscleMassKg)) {
            _tmpMuscleMassKg = null
          } else {
            _tmpMuscleMassKg = _stmt.getDouble(_columnIndexOfMuscleMassKg).toFloat()
          }
          _result =
              ProgressSnapshotEntity(_tmpRowId,_tmpDateIso,_tmpImageUrl,_tmpWeightKg,_tmpBodyFatPercent,_tmpMuscleMassKg)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
