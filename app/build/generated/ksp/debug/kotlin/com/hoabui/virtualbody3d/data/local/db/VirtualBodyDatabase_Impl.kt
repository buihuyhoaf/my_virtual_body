package com.hoabui.virtualbody3d.`data`.local.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class VirtualBodyDatabase_Impl : VirtualBodyDatabase() {
  private val _workoutScheduleDao: Lazy<WorkoutScheduleDao> = lazy {
    WorkoutScheduleDao_Impl(this)
  }

  private val _workoutSessionDao: Lazy<WorkoutSessionDao> = lazy {
    WorkoutSessionDao_Impl(this)
  }

  private val _exerciseDao: Lazy<ExerciseDao> = lazy {
    ExerciseDao_Impl(this)
  }

  private val _progressTimelineDao: Lazy<ProgressTimelineDao> = lazy {
    ProgressTimelineDao_Impl(this)
  }

  private val _nutritionSummaryDao: Lazy<NutritionSummaryDao> = lazy {
    NutritionSummaryDao_Impl(this)
  }

  private val _bodyScanResultDao: Lazy<BodyScanResultDao> = lazy {
    BodyScanResultDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(5,
        "8a5a007576f104e6b3080483e8f39bbd", "1356ead0b3169b38871f8497fc56cb19") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `workout_schedules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clientId` TEXT NOT NULL, `dayKey` INTEGER NOT NULL, `exerciseId` TEXT NOT NULL, `sessionId` TEXT, `scheduledAtEpochMillis` INTEGER NOT NULL, `sets` INTEGER NOT NULL, `reps` INTEGER NOT NULL, `weightKg` REAL NOT NULL, `restSeconds` INTEGER NOT NULL, `notes` TEXT, `measurementMode` TEXT NOT NULL, `durationSeconds` INTEGER, `locationId` TEXT NOT NULL, `executionStatus` TEXT NOT NULL, `createdAtEpochMillis` INTEGER NOT NULL, `updatedAtEpochMillis` INTEGER NOT NULL, `exercise_image_res_url` TEXT, `exercise_local_image_name` TEXT)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_schedules_dayKey` ON `workout_schedules` (`dayKey`)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_workout_schedules_clientId` ON `workout_schedules` (`clientId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `workout_sessions` (`id` TEXT NOT NULL, `locationId` TEXT NOT NULL, `startEpochMillis` INTEGER NOT NULL, `endEpochMillis` INTEGER NOT NULL, `dayKey` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_sessions_dayKey` ON `workout_sessions` (`dayKey`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_sessions_locationId_dayKey` ON `workout_sessions` (`locationId`, `dayKey`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_sessions_location_day_start_end` ON `workout_sessions` (`locationId`, `dayKey`, `startEpochMillis`, `endEpochMillis`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `exercises` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `local_image_name` TEXT, `image_res_url` TEXT, `body_region` TEXT, `category` TEXT, `description` TEXT, `equipment` TEXT, `safety_notes` TEXT, `last_weight_kg` REAL, `sets` INTEGER, `reps` INTEGER, `measurement_mode` TEXT, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `progress_snapshots` (`row_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date_iso` TEXT NOT NULL, `image_url` TEXT, `weight_kg` REAL, `body_fat_percent` REAL, `muscle_mass_kg` REAL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_progress_snapshots_date_iso` ON `progress_snapshots` (`date_iso`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `nutrition_summary` (`id` INTEGER NOT NULL, `intake` INTEGER NOT NULL, `burned` INTEGER NOT NULL, `goal` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `body_scan_results` (`id` INTEGER NOT NULL, `payload_json` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8a5a007576f104e6b3080483e8f39bbd')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `workout_schedules`")
        connection.execSQL("DROP TABLE IF EXISTS `workout_sessions`")
        connection.execSQL("DROP TABLE IF EXISTS `exercises`")
        connection.execSQL("DROP TABLE IF EXISTS `progress_snapshots`")
        connection.execSQL("DROP TABLE IF EXISTS `nutrition_summary`")
        connection.execSQL("DROP TABLE IF EXISTS `body_scan_results`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsWorkoutSchedules: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWorkoutSchedules.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("clientId", TableInfo.Column("clientId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("dayKey", TableInfo.Column("dayKey", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("exerciseId", TableInfo.Column("exerciseId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("sessionId", TableInfo.Column("sessionId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("scheduledAtEpochMillis",
            TableInfo.Column("scheduledAtEpochMillis", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("sets", TableInfo.Column("sets", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("reps", TableInfo.Column("reps", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("weightKg", TableInfo.Column("weightKg", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("restSeconds", TableInfo.Column("restSeconds", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("measurementMode", TableInfo.Column("measurementMode", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("durationSeconds", TableInfo.Column("durationSeconds",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("locationId", TableInfo.Column("locationId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("executionStatus", TableInfo.Column("executionStatus", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("createdAtEpochMillis",
            TableInfo.Column("createdAtEpochMillis", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("updatedAtEpochMillis",
            TableInfo.Column("updatedAtEpochMillis", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("exercise_image_res_url",
            TableInfo.Column("exercise_image_res_url", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSchedules.put("exercise_local_image_name",
            TableInfo.Column("exercise_local_image_name", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWorkoutSchedules: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWorkoutSchedules: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesWorkoutSchedules.add(TableInfo.Index("index_workout_schedules_dayKey", false,
            listOf("dayKey"), listOf("ASC")))
        _indicesWorkoutSchedules.add(TableInfo.Index("index_workout_schedules_clientId", true,
            listOf("clientId"), listOf("ASC")))
        val _infoWorkoutSchedules: TableInfo = TableInfo("workout_schedules",
            _columnsWorkoutSchedules, _foreignKeysWorkoutSchedules, _indicesWorkoutSchedules)
        val _existingWorkoutSchedules: TableInfo = read(connection, "workout_schedules")
        if (!_infoWorkoutSchedules.equals(_existingWorkoutSchedules)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |workout_schedules(com.hoabui.virtualbody3d.data.local.db.WorkoutScheduleEntity).
              | Expected:
              |""".trimMargin() + _infoWorkoutSchedules + """
              |
              | Found:
              |""".trimMargin() + _existingWorkoutSchedules)
        }
        val _columnsWorkoutSessions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWorkoutSessions.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSessions.put("locationId", TableInfo.Column("locationId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSessions.put("startEpochMillis", TableInfo.Column("startEpochMillis",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSessions.put("endEpochMillis", TableInfo.Column("endEpochMillis", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkoutSessions.put("dayKey", TableInfo.Column("dayKey", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWorkoutSessions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWorkoutSessions: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesWorkoutSessions.add(TableInfo.Index("index_workout_sessions_dayKey", false,
            listOf("dayKey"), listOf("ASC")))
        _indicesWorkoutSessions.add(TableInfo.Index("index_workout_sessions_locationId_dayKey",
            false, listOf("locationId", "dayKey"), listOf("ASC", "ASC")))
        _indicesWorkoutSessions.add(TableInfo.Index("index_workout_sessions_location_day_start_end",
            false, listOf("locationId", "dayKey", "startEpochMillis", "endEpochMillis"),
            listOf("ASC", "ASC", "ASC", "ASC")))
        val _infoWorkoutSessions: TableInfo = TableInfo("workout_sessions", _columnsWorkoutSessions,
            _foreignKeysWorkoutSessions, _indicesWorkoutSessions)
        val _existingWorkoutSessions: TableInfo = read(connection, "workout_sessions")
        if (!_infoWorkoutSessions.equals(_existingWorkoutSessions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |workout_sessions(com.hoabui.virtualbody3d.data.local.db.WorkoutSessionEntity).
              | Expected:
              |""".trimMargin() + _infoWorkoutSessions + """
              |
              | Found:
              |""".trimMargin() + _existingWorkoutSessions)
        }
        val _columnsExercises: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsExercises.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("local_image_name", TableInfo.Column("local_image_name", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("image_res_url", TableInfo.Column("image_res_url", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("body_region", TableInfo.Column("body_region", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("category", TableInfo.Column("category", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("description", TableInfo.Column("description", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("equipment", TableInfo.Column("equipment", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("safety_notes", TableInfo.Column("safety_notes", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("last_weight_kg", TableInfo.Column("last_weight_kg", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("sets", TableInfo.Column("sets", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("reps", TableInfo.Column("reps", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExercises.put("measurement_mode", TableInfo.Column("measurement_mode", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExercises: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesExercises: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoExercises: TableInfo = TableInfo("exercises", _columnsExercises,
            _foreignKeysExercises, _indicesExercises)
        val _existingExercises: TableInfo = read(connection, "exercises")
        if (!_infoExercises.equals(_existingExercises)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |exercises(com.hoabui.virtualbody3d.data.local.db.ExerciseEntity).
              | Expected:
              |""".trimMargin() + _infoExercises + """
              |
              | Found:
              |""".trimMargin() + _existingExercises)
        }
        val _columnsProgressSnapshots: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProgressSnapshots.put("row_id", TableInfo.Column("row_id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProgressSnapshots.put("date_iso", TableInfo.Column("date_iso", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProgressSnapshots.put("image_url", TableInfo.Column("image_url", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProgressSnapshots.put("weight_kg", TableInfo.Column("weight_kg", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProgressSnapshots.put("body_fat_percent", TableInfo.Column("body_fat_percent",
            "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProgressSnapshots.put("muscle_mass_kg", TableInfo.Column("muscle_mass_kg", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProgressSnapshots: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesProgressSnapshots: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesProgressSnapshots.add(TableInfo.Index("index_progress_snapshots_date_iso", true,
            listOf("date_iso"), listOf("ASC")))
        val _infoProgressSnapshots: TableInfo = TableInfo("progress_snapshots",
            _columnsProgressSnapshots, _foreignKeysProgressSnapshots, _indicesProgressSnapshots)
        val _existingProgressSnapshots: TableInfo = read(connection, "progress_snapshots")
        if (!_infoProgressSnapshots.equals(_existingProgressSnapshots)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |progress_snapshots(com.hoabui.virtualbody3d.data.local.db.ProgressSnapshotEntity).
              | Expected:
              |""".trimMargin() + _infoProgressSnapshots + """
              |
              | Found:
              |""".trimMargin() + _existingProgressSnapshots)
        }
        val _columnsNutritionSummary: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsNutritionSummary.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNutritionSummary.put("intake", TableInfo.Column("intake", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNutritionSummary.put("burned", TableInfo.Column("burned", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNutritionSummary.put("goal", TableInfo.Column("goal", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysNutritionSummary: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesNutritionSummary: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoNutritionSummary: TableInfo = TableInfo("nutrition_summary",
            _columnsNutritionSummary, _foreignKeysNutritionSummary, _indicesNutritionSummary)
        val _existingNutritionSummary: TableInfo = read(connection, "nutrition_summary")
        if (!_infoNutritionSummary.equals(_existingNutritionSummary)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |nutrition_summary(com.hoabui.virtualbody3d.data.local.db.NutritionSummaryEntity).
              | Expected:
              |""".trimMargin() + _infoNutritionSummary + """
              |
              | Found:
              |""".trimMargin() + _existingNutritionSummary)
        }
        val _columnsBodyScanResults: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBodyScanResults.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBodyScanResults.put("payload_json", TableInfo.Column("payload_json", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBodyScanResults: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBodyScanResults: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBodyScanResults: TableInfo = TableInfo("body_scan_results",
            _columnsBodyScanResults, _foreignKeysBodyScanResults, _indicesBodyScanResults)
        val _existingBodyScanResults: TableInfo = read(connection, "body_scan_results")
        if (!_infoBodyScanResults.equals(_existingBodyScanResults)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |body_scan_results(com.hoabui.virtualbody3d.data.local.db.BodyScanResultEntity).
              | Expected:
              |""".trimMargin() + _infoBodyScanResults + """
              |
              | Found:
              |""".trimMargin() + _existingBodyScanResults)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "workout_schedules",
        "workout_sessions", "exercises", "progress_snapshots", "nutrition_summary",
        "body_scan_results")
  }

  public override fun clearAllTables() {
    super.performClear(false, "workout_schedules", "workout_sessions", "exercises",
        "progress_snapshots", "nutrition_summary", "body_scan_results")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(WorkoutScheduleDao::class,
        WorkoutScheduleDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WorkoutSessionDao::class, WorkoutSessionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ExerciseDao::class, ExerciseDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ProgressTimelineDao::class,
        ProgressTimelineDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(NutritionSummaryDao::class,
        NutritionSummaryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BodyScanResultDao::class, BodyScanResultDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun workoutScheduleDao(): WorkoutScheduleDao = _workoutScheduleDao.value

  public override fun workoutSessionDao(): WorkoutSessionDao = _workoutSessionDao.value

  public override fun exerciseDao(): ExerciseDao = _exerciseDao.value

  public override fun progressTimelineDao(): ProgressTimelineDao = _progressTimelineDao.value

  public override fun nutritionSummaryDao(): NutritionSummaryDao = _nutritionSummaryDao.value

  public override fun bodyScanResultDao(): BodyScanResultDao = _bodyScanResultDao.value
}
