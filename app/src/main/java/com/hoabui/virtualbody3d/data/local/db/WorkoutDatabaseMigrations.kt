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
