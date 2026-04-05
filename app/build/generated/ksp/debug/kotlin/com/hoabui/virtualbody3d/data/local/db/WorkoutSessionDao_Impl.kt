package com.hoabui.virtualbody3d.`data`.local.db

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
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
public class WorkoutSessionDao_Impl(
  __db: RoomDatabase,
) : WorkoutSessionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWorkoutSessionEntity: EntityInsertAdapter<WorkoutSessionEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWorkoutSessionEntity = object :
        EntityInsertAdapter<WorkoutSessionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `workout_sessions` (`id`,`locationId`,`startEpochMillis`,`endEpochMillis`,`dayKey`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WorkoutSessionEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.locationId)
        statement.bindLong(3, entity.startEpochMillis)
        statement.bindLong(4, entity.endEpochMillis)
        statement.bindLong(5, entity.dayKey)
      }
    }
  }

  public override suspend fun insertSession(entity: WorkoutSessionEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfWorkoutSessionEntity.insert(_connection, entity)
  }

  public override fun observeAllSessions(): Flow<List<WorkoutSessionEntity>> {
    val _sql: String = "SELECT * FROM workout_sessions ORDER BY startEpochMillis ASC"
    return createFlow(__db, false, arrayOf("workout_sessions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLocationId: Int = getColumnIndexOrThrow(_stmt, "locationId")
        val _columnIndexOfStartEpochMillis: Int = getColumnIndexOrThrow(_stmt, "startEpochMillis")
        val _columnIndexOfEndEpochMillis: Int = getColumnIndexOrThrow(_stmt, "endEpochMillis")
        val _columnIndexOfDayKey: Int = getColumnIndexOrThrow(_stmt, "dayKey")
        val _result: MutableList<WorkoutSessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutSessionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpLocationId: String
          _tmpLocationId = _stmt.getText(_columnIndexOfLocationId)
          val _tmpStartEpochMillis: Long
          _tmpStartEpochMillis = _stmt.getLong(_columnIndexOfStartEpochMillis)
          val _tmpEndEpochMillis: Long
          _tmpEndEpochMillis = _stmt.getLong(_columnIndexOfEndEpochMillis)
          val _tmpDayKey: Long
          _tmpDayKey = _stmt.getLong(_columnIndexOfDayKey)
          _item =
              WorkoutSessionEntity(_tmpId,_tmpLocationId,_tmpStartEpochMillis,_tmpEndEpochMillis,_tmpDayKey)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllSessions(): List<WorkoutSessionEntity> {
    val _sql: String = "SELECT * FROM workout_sessions ORDER BY startEpochMillis ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLocationId: Int = getColumnIndexOrThrow(_stmt, "locationId")
        val _columnIndexOfStartEpochMillis: Int = getColumnIndexOrThrow(_stmt, "startEpochMillis")
        val _columnIndexOfEndEpochMillis: Int = getColumnIndexOrThrow(_stmt, "endEpochMillis")
        val _columnIndexOfDayKey: Int = getColumnIndexOrThrow(_stmt, "dayKey")
        val _result: MutableList<WorkoutSessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkoutSessionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpLocationId: String
          _tmpLocationId = _stmt.getText(_columnIndexOfLocationId)
          val _tmpStartEpochMillis: Long
          _tmpStartEpochMillis = _stmt.getLong(_columnIndexOfStartEpochMillis)
          val _tmpEndEpochMillis: Long
          _tmpEndEpochMillis = _stmt.getLong(_columnIndexOfEndEpochMillis)
          val _tmpDayKey: Long
          _tmpDayKey = _stmt.getLong(_columnIndexOfDayKey)
          _item =
              WorkoutSessionEntity(_tmpId,_tmpLocationId,_tmpStartEpochMillis,_tmpEndEpochMillis,_tmpDayKey)
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
