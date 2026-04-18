package com.hoabui.virtualbody3d.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hoabui.virtualbody3d.data.local.db.seed.DatabaseSeeder

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `workout_sessions` (
                `id` TEXT NOT NULL,
                `locationId` TEXT NOT NULL,
                `startEpochMillis` INTEGER NOT NULL,
                `endEpochMillis` INTEGER NOT NULL,
                `dayKey` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_sessions_dayKey` ON `workout_sessions` (`dayKey`)")
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_workout_sessions_locationId_dayKey` ON `workout_sessions` (`locationId`, `dayKey`)",
        )
    }
}

fun migration2To3(databaseSeeder: DatabaseSeeder): Migration =
    object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            databaseSeeder.seedCatalogAfterVersion2Upgrade(db)
        }
    }

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE workout_schedules ADD COLUMN exercise_image_res_url TEXT")
        db.execSQL("ALTER TABLE workout_schedules ADD COLUMN exercise_local_image_name TEXT")
    }
}

val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_workout_sessions_location_day_start_end`
            ON `workout_sessions` (`locationId`, `dayKey`, `startEpochMillis`, `endEpochMillis`)
            """.trimIndent(),
        )
    }
}

val MIGRATION_5_6: Migration = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `workout_log_sessions` (
                `id` TEXT NOT NULL,
                `startEpochMillis` INTEGER NOT NULL,
                `endEpochMillis` INTEGER NOT NULL,
                `dayKey` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_log_sessions_dayKey` ON `workout_log_sessions` (`dayKey`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `workout_log_exercises` (
                `id` TEXT NOT NULL,
                `sessionId` TEXT NOT NULL,
                `exerciseId` TEXT NOT NULL,
                `displayNameSnapshot` TEXT NOT NULL,
                `measurementMode` TEXT NOT NULL,
                `startTimeMillis` INTEGER NOT NULL,
                `orderIndex` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_log_exercises_sessionId` ON `workout_log_exercises` (`sessionId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_log_exercises_exerciseId` ON `workout_log_exercises` (`exerciseId`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `workout_log_sets` (
                `id` TEXT NOT NULL,
                `exerciseLogId` TEXT NOT NULL,
                `reps` INTEGER NOT NULL,
                `weightKg` REAL NOT NULL,
                `durationSeconds` INTEGER,
                `setIndex` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_log_sets_exerciseLogId` ON `workout_log_sets` (`exerciseLogId`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `workout_log_energy` (
                `exerciseLogId` TEXT NOT NULL,
                `kcal` REAL NOT NULL,
                `bodyWeightUsed` REAL NOT NULL,
                `metUsed` REAL NOT NULL,
                `epocFactorUsed` REAL NOT NULL,
                PRIMARY KEY(`exerciseLogId`)
            )
            """.trimIndent(),
        )
    }
}

val MIGRATION_6_7: Migration = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `workout_log_sessions_new` (
                `id` TEXT NOT NULL,
                `startEpochMillis` INTEGER NOT NULL,
                `endEpochMillis` INTEGER NOT NULL,
                `dayKey` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO `workout_log_sessions_new` (`id`, `startEpochMillis`, `endEpochMillis`, `dayKey`)
            SELECT `id`, `startEpochMillis`, `endEpochMillis`, `dayKey`
            FROM `workout_log_sessions`
            """.trimIndent(),
        )
        db.execSQL("DROP TABLE `workout_log_sessions`")
        db.execSQL("ALTER TABLE `workout_log_sessions_new` RENAME TO `workout_log_sessions`")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_log_sessions_dayKey` ON `workout_log_sessions` (`dayKey`)")
    }
}
