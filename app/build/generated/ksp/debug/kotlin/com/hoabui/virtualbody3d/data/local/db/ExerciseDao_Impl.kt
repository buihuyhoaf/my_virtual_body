package com.hoabui.virtualbody3d.`data`.local.db

import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ExerciseDao_Impl(
  __db: RoomDatabase,
) : ExerciseDao {
  private val __db: RoomDatabase
  init {
    this.__db = __db
  }

  public override fun observeAll(): Flow<List<ExerciseEntity>> {
    val _sql: String = "SELECT * FROM exercises ORDER BY id ASC"
    return createFlow(__db, false, arrayOf("exercises")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLocalImageName: Int = getColumnIndexOrThrow(_stmt, "local_image_name")
        val _columnIndexOfImageResUrl: Int = getColumnIndexOrThrow(_stmt, "image_res_url")
        val _columnIndexOfBodyRegion: Int = getColumnIndexOrThrow(_stmt, "body_region")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfEquipment: Int = getColumnIndexOrThrow(_stmt, "equipment")
        val _columnIndexOfSafetyNotes: Int = getColumnIndexOrThrow(_stmt, "safety_notes")
        val _columnIndexOfLastWeightKg: Int = getColumnIndexOrThrow(_stmt, "last_weight_kg")
        val _columnIndexOfSets: Int = getColumnIndexOrThrow(_stmt, "sets")
        val _columnIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _columnIndexOfMeasurementMode: Int = getColumnIndexOrThrow(_stmt, "measurement_mode")
        val _result: MutableList<ExerciseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExerciseEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLocalImageName: String?
          if (_stmt.isNull(_columnIndexOfLocalImageName)) {
            _tmpLocalImageName = null
          } else {
            _tmpLocalImageName = _stmt.getText(_columnIndexOfLocalImageName)
          }
          val _tmpImageResUrl: String?
          if (_stmt.isNull(_columnIndexOfImageResUrl)) {
            _tmpImageResUrl = null
          } else {
            _tmpImageResUrl = _stmt.getText(_columnIndexOfImageResUrl)
          }
          val _tmpBodyRegion: String?
          if (_stmt.isNull(_columnIndexOfBodyRegion)) {
            _tmpBodyRegion = null
          } else {
            _tmpBodyRegion = _stmt.getText(_columnIndexOfBodyRegion)
          }
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpEquipment: String?
          if (_stmt.isNull(_columnIndexOfEquipment)) {
            _tmpEquipment = null
          } else {
            _tmpEquipment = _stmt.getText(_columnIndexOfEquipment)
          }
          val _tmpSafetyNotes: String?
          if (_stmt.isNull(_columnIndexOfSafetyNotes)) {
            _tmpSafetyNotes = null
          } else {
            _tmpSafetyNotes = _stmt.getText(_columnIndexOfSafetyNotes)
          }
          val _tmpLastWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfLastWeightKg)) {
            _tmpLastWeightKg = null
          } else {
            _tmpLastWeightKg = _stmt.getDouble(_columnIndexOfLastWeightKg)
          }
          val _tmpSets: Int?
          if (_stmt.isNull(_columnIndexOfSets)) {
            _tmpSets = null
          } else {
            _tmpSets = _stmt.getLong(_columnIndexOfSets).toInt()
          }
          val _tmpReps: Int?
          if (_stmt.isNull(_columnIndexOfReps)) {
            _tmpReps = null
          } else {
            _tmpReps = _stmt.getLong(_columnIndexOfReps).toInt()
          }
          val _tmpMeasurementMode: String?
          if (_stmt.isNull(_columnIndexOfMeasurementMode)) {
            _tmpMeasurementMode = null
          } else {
            _tmpMeasurementMode = _stmt.getText(_columnIndexOfMeasurementMode)
          }
          _item =
              ExerciseEntity(_tmpId,_tmpName,_tmpLocalImageName,_tmpImageResUrl,_tmpBodyRegion,_tmpCategory,_tmpDescription,_tmpEquipment,_tmpSafetyNotes,_tmpLastWeightKg,_tmpSets,_tmpReps,_tmpMeasurementMode)
          _result.add(_item)
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
