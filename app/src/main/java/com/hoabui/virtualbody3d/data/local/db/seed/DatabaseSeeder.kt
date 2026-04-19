package com.hoabui.virtualbody3d.data.local.db.seed

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteStatement
import com.google.gson.Gson
import com.hoabui.virtualbody3d.data.model.ExerciseDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single place for catalog / dashboard seed data. Used from [androidx.room.RoomDatabase.Callback.onCreate]
 * after Room creates tables, and from [androidx.room.RoomDatabase.Callback.onOpen] to refresh exercise images.
 *
 * All inserts are idempotent (`INSERT OR IGNORE` / `INSERT OR REPLACE`) inside a transaction.
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val gson: Gson,
) {
    private val refreshLock = Any()
    @Volatile
    private var hasRefreshedLocalImageNamesThisSession = false

    fun roomCallback(): RoomDatabase.Callback =
        object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                seedCatalogFreshDatabase(db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                synchronized(refreshLock) {
                    if (hasRefreshedLocalImageNamesThisSession) return
                    refreshExerciseLocalImageNames(db)
                    hasRefreshedLocalImageNamesThisSession = true
                }
            }
        }

    /**
     * Room has already created all entity tables. Only populate rows.
     */
    fun seedCatalogFreshDatabase(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            insertCatalogSeedDataIdempotent(db)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun insertCatalogSeedDataIdempotent(db: SupportSQLiteDatabase) {
        val insertExercise = db.compileStatement(
            """
            INSERT OR IGNORE INTO exercises (
              id, name, local_image_name, image_res_url, body_region, category,
              description, equipment, safety_notes, last_weight_kg, sets, reps, measurement_mode
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
            """.trimIndent(),
        )
        CatalogSeedData.exerciseRowsForSeed().forEach { e ->
            insertExercise.bindAllArgsAsStrings(e)
            insertExercise.executeInsert()
            insertExercise.clearBindings()
        }
        insertExercise.close()

        val insertProgress = db.compileStatement(
            """
            INSERT OR IGNORE INTO progress_snapshots (date_iso, image_url, weight_kg, body_fat_percent, muscle_mass_kg)
            VALUES (?,?,?,?,?)
            """.trimIndent(),
        )
        CatalogSeedData.progressSnapshotsForSeed().forEach { p ->
            bindStringOrNull(insertProgress, 1, p.dateIso)
            bindStringOrNull(insertProgress, 2, p.imageUrl)
            bindFloatOrNull(insertProgress, 3, p.weightKg)
            bindFloatOrNull(insertProgress, 4, p.bodyFatPercent)
            bindFloatOrNull(insertProgress, 5, p.muscleMassKg)
            insertProgress.executeInsert()
            insertProgress.clearBindings()
        }
        insertProgress.close()

        val n = CatalogSeedData.nutritionSummaryForSeed()
        db.compileStatement(
            """
            INSERT OR REPLACE INTO nutrition_summary (id, intake, burned, goal)
            VALUES (1,?,?,?)
            """.trimIndent(),
        ).apply {
            bindLong(1, n.intake.toLong())
            bindLong(2, n.burned.toLong())
            bindLong(3, n.goal.toLong())
            executeInsert()
            close()
        }

        val json = gson.toJson(CatalogSeedData.bodyScanResultForSeed())
        db.compileStatement(
            """
            INSERT OR REPLACE INTO body_scan_results (id, payload_json)
            VALUES (1,?)
            """.trimIndent(),
        ).apply {
            bindString(1, json)
            executeInsert()
            close()
        }
    }

    private fun refreshExerciseLocalImageNames(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            val updateExerciseImage = db.compileStatement(
                """
                UPDATE exercises
                SET local_image_name = ?
                WHERE id = ? AND (local_image_name IS NULL OR local_image_name != ?)
                """.trimIndent(),
            )
            val clearExerciseImage = db.compileStatement(
                """
                UPDATE exercises
                SET local_image_name = NULL
                WHERE id = ? AND local_image_name IS NOT NULL
                """.trimIndent(),
            )
            CatalogSeedData.exerciseRowsForSeed().forEach { e ->
                val id = requireNotNull(e.id) { "Seed exercise id is required" }
                val localImageName = e.localImageName
                if (localImageName == null) {
                    clearExerciseImage.bindString(1, id)
                    clearExerciseImage.executeUpdateDelete()
                    clearExerciseImage.clearBindings()
                } else {
                    updateExerciseImage.bindString(1, localImageName)
                    updateExerciseImage.bindString(2, id)
                    updateExerciseImage.bindString(3, localImageName)
                    updateExerciseImage.executeUpdateDelete()
                    updateExerciseImage.clearBindings()
                }
            }
            updateExerciseImage.close()
            clearExerciseImage.close()
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun SupportSQLiteStatement.bindAllArgsAsStrings(e: ExerciseDto) {
        bindString(1, e.id.orEmpty())
        bindString(2, e.name.orEmpty())
        bindStringOrNull(this, 3, e.localImageName)
        bindStringOrNull(this, 4, e.imageResUrl)
        bindStringOrNull(this, 5, e.bodyRegion)
        bindStringOrNull(this, 6, e.category)
        bindStringOrNull(this, 7, e.description)
        bindStringOrNull(this, 8, e.equipment)
        bindStringOrNull(this, 9, e.safetyNotes)
        if (e.lastWeightKg != null) bindDouble(10, e.lastWeightKg!!) else bindNull(10)
        if (e.sets != null) bindLong(11, e.sets!!.toLong()) else bindNull(11)
        if (e.reps != null) bindLong(12, e.reps!!.toLong()) else bindNull(12)
        bindStringOrNull(this, 13, e.measurementMode)
    }

    private fun bindStringOrNull(stmt: SupportSQLiteStatement, index: Int, value: String?) {
        if (value != null) stmt.bindString(index, value) else stmt.bindNull(index)
    }

    private fun bindFloatOrNull(stmt: SupportSQLiteStatement, index: Int, value: Float?) {
        if (value != null) stmt.bindDouble(index, value.toDouble()) else stmt.bindNull(index)
    }
}
