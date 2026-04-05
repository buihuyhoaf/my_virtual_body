package com.hoabui.virtualbody3d.`data`.local.db

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WorkoutScheduleDao_Impl(
  __db: RoomDatabase,
) : WorkoutScheduleDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWorkoutScheduleEntity: EntityInsertAdapter<WorkoutScheduleEntity>

  private val __updateAdapterOfWorkoutScheduleEntity:
      EntityDeleteOrUpdateAdapter<WorkoutScheduleEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWorkoutScheduleEntity = object :
        EntityInsertAdapter<WorkoutScheduleEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `workout_schedules` (`id`,`clientId`,`dayKey`,`exerciseId`,`sessionId`,`scheduledAtEpochMillis`,`sets`,`reps`,`weightKg`,`restSeconds`,`notes`,`measurementMode`,`durationSeconds`,`locationId`,`executionStatus`,`createdAtEpochMillis`,`updatedAtEpochMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutScheduleEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.clientId)
        statement.bindLong(3, entity.dayKey)
        statement.bindText(4, entity.exerciseId)
        val _tmpSessionId: String? = entity.sessionId
        if (_tmpSessionId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpSessionId)
        }
        statement.bindLong(6, entity.scheduledAtEpochMillis)
        statement.bindLong(7, entity.sets.toLong())
        statement.bindLong(8, entity.reps.toLong())
        statement.bindDouble(9, entity.weightKg)
        statement.bindLong(10, entity.restSeconds.toLong())
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpNotes)
        }
        statement.bindText(12, entity.measurementMode)
        val _tmpDurationSeconds: Int? = entity.durationSeconds
        if (_tmpDurationSeconds == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpDurationSeconds.toLong())
        }
        statement.bindText(14, entity.locationId)
        statement.bindText(15, entity.executionStatus)
        statement.bindLong(16, entity.createdAtEpochMillis)
        statement.bindLong(17, entity.updatedAtEpochMillis)
      }
    }
    this.__updateAdapterOfWorkoutScheduleEntity = object :
        EntityDeleteOrUpdateAdapter<WorkoutScheduleEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `workout_schedules` SET `id` = ?,`clientId` = ?,`dayKey` = ?,`exerciseId` = ?,`sessionId` = ?,`scheduledAtEpochMillis` = ?,`sets` = ?,`reps` = ?,`weightKg` = ?,`restSeconds` = ?,`notes` = ?,`measurementMode` = ?,`durationSeconds` = ?,`locationId` = ?,`executionStatus` = ?,`createdAtEpochMillis` = ?,`updatedAtEpochMillis` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutScheduleEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.clientId)
        statement.bindLong(3, entity.dayKey)
        statement.bindText(4, entity.exerciseId)
        val _tmpSessionId: String? = entity.sessionId
        if (_tmpSessionId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpSessionId)
        }
        statement.bindLong(6, entity.scheduledAtEpochMillis)
        statement.bindLong(7, entity.sets.toLong())
        statement.bindLong(8, entity.reps.toLong())
        statement.bindDouble(9, entity.weightKg)
        statement.bindLong(10, entity.restSeconds.toLong())
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpNotes)
        }
        statement.bindText(12, entity.measurementMode)
        val _tmpDurationSeconds: Int? = entity.durationSeconds
        if (_tmpDurationSeconds == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpDurationSeconds.toLong())
        }
        statement.bindText(14, entity.locationId)
        statement.bindText(15, entity.executionStatus)
        statement.bindLong(16, entity.createdAtEpochMillis)
        statement.bindLong(17, entity.updatedAtEpochMillis)
        statement.bindLong(18, entity.id)
      }
    }
  }

  public override suspend fun insert(entity: WorkoutScheduleEntity): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfWorkoutScheduleEntity.insertAndReturnId(_connection,
        entity)
    _result
  }

  public override suspend fun update(entity: WorkoutScheduleEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfWorkoutScheduleEntity.handle(_connection, entity)
  }

  public override suspend fun upsert(entity: WorkoutScheduleEntity): Unit =
      performInTransactionSuspending(__db) {
    super@WorkoutScheduleDao_Impl.upsert(entity)
  }

  public override fun observeSchedulesInRange(startDay: Long, endDay: Long):
      Flow<List<WorkoutScheduleEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM workout_schedules 
        |        WHERE dayKey BETWEEN ? AND ? 
        |        ORDER BY dayKey ASC, sessionId ASC, id ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("workout_schedules")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startDay)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endDay)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfClientId: Int = getColumnIndexOrThrow(_stmt, "clientId")
        val _columnIndexOfDayKey: Int = getColumnIndexOrThrow(_stmt, "dayKey")
        val _columnIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exerciseId")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "sessionId")
        val _columnIndexOfScheduledAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "scheduledAtEpochMillis")
        val _columnIndexOfSets: Int = getColumnIndexOrThrow(_stmt, "sets")
        val _columnIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfRestSeconds: Int = getColumnIndexOrThrow(_stmt, "restSeconds")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMeasurementMode: Int = getColumnIndexOrThrow(_stmt, "measurementMode")
        val _columnIndexOfDurationSeconds: Int = getColumnIndexOrThrow(_stmt, "durationSeconds")
        val _columnIndexOfLocationId: Int = getColumnIndexOrThrow(_stmt, "locationId")
        val _columnIndexOfExecutionStatus: Int = getColumnIndexOrThrow(_stmt, "executionStatus")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "createdAtEpochMillis")
        val _columnIndexOfUpdatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "updatedAtEpochMillis")
        val _result: MutableList<WorkoutScheduleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutScheduleEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpClientId: String
          _tmpClientId = _stmt.getText(_columnIndexOfClientId)
          val _tmpDayKey: Long
          _tmpDayKey = _stmt.getLong(_columnIndexOfDayKey)
          val _tmpExerciseId: String
          _tmpExerciseId = _stmt.getText(_columnIndexOfExerciseId)
          val _tmpSessionId: String?
          if (_stmt.isNull(_columnIndexOfSessionId)) {
            _tmpSessionId = null
          } else {
            _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          }
          val _tmpScheduledAtEpochMillis: Long
          _tmpScheduledAtEpochMillis = _stmt.getLong(_columnIndexOfScheduledAtEpochMillis)
          val _tmpSets: Int
          _tmpSets = _stmt.getLong(_columnIndexOfSets).toInt()
          val _tmpReps: Int
          _tmpReps = _stmt.getLong(_columnIndexOfReps).toInt()
          val _tmpWeightKg: Double
          _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          val _tmpRestSeconds: Int
          _tmpRestSeconds = _stmt.getLong(_columnIndexOfRestSeconds).toInt()
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMeasurementMode: String
          _tmpMeasurementMode = _stmt.getText(_columnIndexOfMeasurementMode)
          val _tmpDurationSeconds: Int?
          if (_stmt.isNull(_columnIndexOfDurationSeconds)) {
            _tmpDurationSeconds = null
          } else {
            _tmpDurationSeconds = _stmt.getLong(_columnIndexOfDurationSeconds).toInt()
          }
          val _tmpLocationId: String
          _tmpLocationId = _stmt.getText(_columnIndexOfLocationId)
          val _tmpExecutionStatus: String
          _tmpExecutionStatus = _stmt.getText(_columnIndexOfExecutionStatus)
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          val _tmpUpdatedAtEpochMillis: Long
          _tmpUpdatedAtEpochMillis = _stmt.getLong(_columnIndexOfUpdatedAtEpochMillis)
          _item =
              WorkoutScheduleEntity(_tmpId,_tmpClientId,_tmpDayKey,_tmpExerciseId,_tmpSessionId,_tmpScheduledAtEpochMillis,_tmpSets,_tmpReps,_tmpWeightKg,_tmpRestSeconds,_tmpNotes,_tmpMeasurementMode,_tmpDurationSeconds,_tmpLocationId,_tmpExecutionStatus,_tmpCreatedAtEpochMillis,_tmpUpdatedAtEpochMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllSchedules(): Flow<List<WorkoutScheduleEntity>> {
    val _sql: String = "SELECT * FROM workout_schedules ORDER BY dayKey ASC, id ASC"
    return createFlow(__db, false, arrayOf("workout_schedules")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfClientId: Int = getColumnIndexOrThrow(_stmt, "clientId")
        val _columnIndexOfDayKey: Int = getColumnIndexOrThrow(_stmt, "dayKey")
        val _columnIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exerciseId")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "sessionId")
        val _columnIndexOfScheduledAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "scheduledAtEpochMillis")
        val _columnIndexOfSets: Int = getColumnIndexOrThrow(_stmt, "sets")
        val _columnIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfRestSeconds: Int = getColumnIndexOrThrow(_stmt, "restSeconds")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMeasurementMode: Int = getColumnIndexOrThrow(_stmt, "measurementMode")
        val _columnIndexOfDurationSeconds: Int = getColumnIndexOrThrow(_stmt, "durationSeconds")
        val _columnIndexOfLocationId: Int = getColumnIndexOrThrow(_stmt, "locationId")
        val _columnIndexOfExecutionStatus: Int = getColumnIndexOrThrow(_stmt, "executionStatus")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "createdAtEpochMillis")
        val _columnIndexOfUpdatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "updatedAtEpochMillis")
        val _result: MutableList<WorkoutScheduleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutScheduleEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpClientId: String
          _tmpClientId = _stmt.getText(_columnIndexOfClientId)
          val _tmpDayKey: Long
          _tmpDayKey = _stmt.getLong(_columnIndexOfDayKey)
          val _tmpExerciseId: String
          _tmpExerciseId = _stmt.getText(_columnIndexOfExerciseId)
          val _tmpSessionId: String?
          if (_stmt.isNull(_columnIndexOfSessionId)) {
            _tmpSessionId = null
          } else {
            _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          }
          val _tmpScheduledAtEpochMillis: Long
          _tmpScheduledAtEpochMillis = _stmt.getLong(_columnIndexOfScheduledAtEpochMillis)
          val _tmpSets: Int
          _tmpSets = _stmt.getLong(_columnIndexOfSets).toInt()
          val _tmpReps: Int
          _tmpReps = _stmt.getLong(_columnIndexOfReps).toInt()
          val _tmpWeightKg: Double
          _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          val _tmpRestSeconds: Int
          _tmpRestSeconds = _stmt.getLong(_columnIndexOfRestSeconds).toInt()
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMeasurementMode: String
          _tmpMeasurementMode = _stmt.getText(_columnIndexOfMeasurementMode)
          val _tmpDurationSeconds: Int?
          if (_stmt.isNull(_columnIndexOfDurationSeconds)) {
            _tmpDurationSeconds = null
          } else {
            _tmpDurationSeconds = _stmt.getLong(_columnIndexOfDurationSeconds).toInt()
          }
          val _tmpLocationId: String
          _tmpLocationId = _stmt.getText(_columnIndexOfLocationId)
          val _tmpExecutionStatus: String
          _tmpExecutionStatus = _stmt.getText(_columnIndexOfExecutionStatus)
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          val _tmpUpdatedAtEpochMillis: Long
          _tmpUpdatedAtEpochMillis = _stmt.getLong(_columnIndexOfUpdatedAtEpochMillis)
          _item =
              WorkoutScheduleEntity(_tmpId,_tmpClientId,_tmpDayKey,_tmpExerciseId,_tmpSessionId,_tmpScheduledAtEpochMillis,_tmpSets,_tmpReps,_tmpWeightKg,_tmpRestSeconds,_tmpNotes,_tmpMeasurementMode,_tmpDurationSeconds,_tmpLocationId,_tmpExecutionStatus,_tmpCreatedAtEpochMillis,_tmpUpdatedAtEpochMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllSchedules(): List<WorkoutScheduleEntity> {
    val _sql: String = "SELECT * FROM workout_schedules ORDER BY dayKey ASC, id ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfClientId: Int = getColumnIndexOrThrow(_stmt, "clientId")
        val _columnIndexOfDayKey: Int = getColumnIndexOrThrow(_stmt, "dayKey")
        val _columnIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exerciseId")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "sessionId")
        val _columnIndexOfScheduledAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "scheduledAtEpochMillis")
        val _columnIndexOfSets: Int = getColumnIndexOrThrow(_stmt, "sets")
        val _columnIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfRestSeconds: Int = getColumnIndexOrThrow(_stmt, "restSeconds")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMeasurementMode: Int = getColumnIndexOrThrow(_stmt, "measurementMode")
        val _columnIndexOfDurationSeconds: Int = getColumnIndexOrThrow(_stmt, "durationSeconds")
        val _columnIndexOfLocationId: Int = getColumnIndexOrThrow(_stmt, "locationId")
        val _columnIndexOfExecutionStatus: Int = getColumnIndexOrThrow(_stmt, "executionStatus")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "createdAtEpochMillis")
        val _columnIndexOfUpdatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "updatedAtEpochMillis")
        val _result: MutableList<WorkoutScheduleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutScheduleEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpClientId: String
          _tmpClientId = _stmt.getText(_columnIndexOfClientId)
          val _tmpDayKey: Long
          _tmpDayKey = _stmt.getLong(_columnIndexOfDayKey)
          val _tmpExerciseId: String
          _tmpExerciseId = _stmt.getText(_columnIndexOfExerciseId)
          val _tmpSessionId: String?
          if (_stmt.isNull(_columnIndexOfSessionId)) {
            _tmpSessionId = null
          } else {
            _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          }
          val _tmpScheduledAtEpochMillis: Long
          _tmpScheduledAtEpochMillis = _stmt.getLong(_columnIndexOfScheduledAtEpochMillis)
          val _tmpSets: Int
          _tmpSets = _stmt.getLong(_columnIndexOfSets).toInt()
          val _tmpReps: Int
          _tmpReps = _stmt.getLong(_columnIndexOfReps).toInt()
          val _tmpWeightKg: Double
          _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          val _tmpRestSeconds: Int
          _tmpRestSeconds = _stmt.getLong(_columnIndexOfRestSeconds).toInt()
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMeasurementMode: String
          _tmpMeasurementMode = _stmt.getText(_columnIndexOfMeasurementMode)
          val _tmpDurationSeconds: Int?
          if (_stmt.isNull(_columnIndexOfDurationSeconds)) {
            _tmpDurationSeconds = null
          } else {
            _tmpDurationSeconds = _stmt.getLong(_columnIndexOfDurationSeconds).toInt()
          }
          val _tmpLocationId: String
          _tmpLocationId = _stmt.getText(_columnIndexOfLocationId)
          val _tmpExecutionStatus: String
          _tmpExecutionStatus = _stmt.getText(_columnIndexOfExecutionStatus)
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          val _tmpUpdatedAtEpochMillis: Long
          _tmpUpdatedAtEpochMillis = _stmt.getLong(_columnIndexOfUpdatedAtEpochMillis)
          _item =
              WorkoutScheduleEntity(_tmpId,_tmpClientId,_tmpDayKey,_tmpExerciseId,_tmpSessionId,_tmpScheduledAtEpochMillis,_tmpSets,_tmpReps,_tmpWeightKg,_tmpRestSeconds,_tmpNotes,_tmpMeasurementMode,_tmpDurationSeconds,_tmpLocationId,_tmpExecutionStatus,_tmpCreatedAtEpochMillis,_tmpUpdatedAtEpochMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findByClientId(clientId: String): WorkoutScheduleEntity? {
    val _sql: String = "SELECT * FROM workout_schedules WHERE clientId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, clientId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfClientId: Int = getColumnIndexOrThrow(_stmt, "clientId")
        val _columnIndexOfDayKey: Int = getColumnIndexOrThrow(_stmt, "dayKey")
        val _columnIndexOfExerciseId: Int = getColumnIndexOrThrow(_stmt, "exerciseId")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "sessionId")
        val _columnIndexOfScheduledAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "scheduledAtEpochMillis")
        val _columnIndexOfSets: Int = getColumnIndexOrThrow(_stmt, "sets")
        val _columnIndexOfReps: Int = getColumnIndexOrThrow(_stmt, "reps")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfRestSeconds: Int = getColumnIndexOrThrow(_stmt, "restSeconds")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfMeasurementMode: Int = getColumnIndexOrThrow(_stmt, "measurementMode")
        val _columnIndexOfDurationSeconds: Int = getColumnIndexOrThrow(_stmt, "durationSeconds")
        val _columnIndexOfLocationId: Int = getColumnIndexOrThrow(_stmt, "locationId")
        val _columnIndexOfExecutionStatus: Int = getColumnIndexOrThrow(_stmt, "executionStatus")
        val _columnIndexOfCreatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "createdAtEpochMillis")
        val _columnIndexOfUpdatedAtEpochMillis: Int = getColumnIndexOrThrow(_stmt,
            "updatedAtEpochMillis")
        val _result: WorkoutScheduleEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpClientId: String
          _tmpClientId = _stmt.getText(_columnIndexOfClientId)
          val _tmpDayKey: Long
          _tmpDayKey = _stmt.getLong(_columnIndexOfDayKey)
          val _tmpExerciseId: String
          _tmpExerciseId = _stmt.getText(_columnIndexOfExerciseId)
          val _tmpSessionId: String?
          if (_stmt.isNull(_columnIndexOfSessionId)) {
            _tmpSessionId = null
          } else {
            _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          }
          val _tmpScheduledAtEpochMillis: Long
          _tmpScheduledAtEpochMillis = _stmt.getLong(_columnIndexOfScheduledAtEpochMillis)
          val _tmpSets: Int
          _tmpSets = _stmt.getLong(_columnIndexOfSets).toInt()
          val _tmpReps: Int
          _tmpReps = _stmt.getLong(_columnIndexOfReps).toInt()
          val _tmpWeightKg: Double
          _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          val _tmpRestSeconds: Int
          _tmpRestSeconds = _stmt.getLong(_columnIndexOfRestSeconds).toInt()
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpMeasurementMode: String
          _tmpMeasurementMode = _stmt.getText(_columnIndexOfMeasurementMode)
          val _tmpDurationSeconds: Int?
          if (_stmt.isNull(_columnIndexOfDurationSeconds)) {
            _tmpDurationSeconds = null
          } else {
            _tmpDurationSeconds = _stmt.getLong(_columnIndexOfDurationSeconds).toInt()
          }
          val _tmpLocationId: String
          _tmpLocationId = _stmt.getText(_columnIndexOfLocationId)
          val _tmpExecutionStatus: String
          _tmpExecutionStatus = _stmt.getText(_columnIndexOfExecutionStatus)
          val _tmpCreatedAtEpochMillis: Long
          _tmpCreatedAtEpochMillis = _stmt.getLong(_columnIndexOfCreatedAtEpochMillis)
          val _tmpUpdatedAtEpochMillis: Long
          _tmpUpdatedAtEpochMillis = _stmt.getLong(_columnIndexOfUpdatedAtEpochMillis)
          _result =
              WorkoutScheduleEntity(_tmpId,_tmpClientId,_tmpDayKey,_tmpExerciseId,_tmpSessionId,_tmpScheduledAtEpochMillis,_tmpSets,_tmpReps,_tmpWeightKg,_tmpRestSeconds,_tmpNotes,_tmpMeasurementMode,_tmpDurationSeconds,_tmpLocationId,_tmpExecutionStatus,_tmpCreatedAtEpochMillis,_tmpUpdatedAtEpochMillis)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStatus(
    id: Long,
    status: String,
    now: Long,
  ): Int {
    val _sql: String = """
        |
        |        UPDATE workout_schedules 
        |        SET executionStatus = ?, updatedAtEpochMillis = ? 
        |        WHERE id = ?
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
