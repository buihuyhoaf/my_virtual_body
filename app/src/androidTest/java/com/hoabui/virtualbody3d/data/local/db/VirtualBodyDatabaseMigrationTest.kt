package com.hoabui.virtualbody3d.data.local.db

import com.google.gson.Gson
import com.hoabui.virtualbody3d.data.local.db.seed.DatabaseSeeder
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VirtualBodyDatabaseMigrationTest {

    private val testDbName = "migration_test_virtual_body.db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        VirtualBodyDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun migrate1To2_createsWorkoutSessions() {
        helper.createDatabase(testDbName, 1).apply {
            close()
        }
        val db = helper.runMigrationsAndValidate(
            testDbName,
            2,
            true,
            MIGRATION_1_2,
        )
        db.query(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='workout_sessions'",
        ).use { c ->
            assert(c.moveToFirst())
            assertEquals("workout_sessions", c.getString(0))
        }
        db.close()
    }

    @Test
    fun migrate1To3_seedsCatalogTables() {
        helper.createDatabase(testDbName, 1).apply { close() }
        val db = helper.runMigrationsAndValidate(
            testDbName,
            3,
            true,
            MIGRATION_1_2,
            migration2To3(DatabaseSeeder(Gson())),
        )
        db.query("SELECT COUNT(*) FROM exercises").use { c ->
            assert(c.moveToFirst())
            assertEquals(14, c.getInt(0))
        }
        db.query("SELECT COUNT(*) FROM progress_snapshots").use { c ->
            assert(c.moveToFirst())
            assertEquals(5, c.getInt(0))
        }
        db.query("SELECT COUNT(*) FROM nutrition_summary").use { c ->
            assert(c.moveToFirst())
            assertEquals(1, c.getInt(0))
        }
        db.query("SELECT COUNT(*) FROM body_scan_results").use { c ->
            assert(c.moveToFirst())
            assertEquals(1, c.getInt(0))
        }
        db.close()
    }
}
