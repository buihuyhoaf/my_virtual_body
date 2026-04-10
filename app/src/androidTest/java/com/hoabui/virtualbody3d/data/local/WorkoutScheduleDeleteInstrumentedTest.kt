package com.hoabui.virtualbody3d.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hoabui.virtualbody3d.data.local.db.VirtualBodyDatabase
import com.hoabui.virtualbody3d.data.local.db.WorkoutScheduleEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutSessionEntity
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class WorkoutScheduleDeleteInstrumentedTest {

    private lateinit var db: VirtualBodyDatabase
    private lateinit var dataSource: WorkoutScheduleLocalDataSource

    @Before
    fun setup() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(ctx, VirtualBodyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dataSource = WorkoutScheduleLocalDataSource(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun deleteLastScheduleForSession_removesSessionRow() = runBlocking {
        val sessionDao = db.workoutSessionDao()
        val scheduleDao = db.workoutScheduleDao()
        val now = 1_700_000_000_000L
        sessionDao.insertSession(
            WorkoutSessionEntity(
                id = "sess-a",
                locationId = "loc1",
                startEpochMillis = now,
                endEpochMillis = now + 3_600_000L,
                dayKey = 19_000L,
            ),
        )
        val row1 = scheduleEntity("c1", sessionId = "sess-a", now = now)
        val row2 = scheduleEntity("c2", sessionId = "sess-a", now = now)
        val id1 = scheduleDao.insert(row1)
        val id2 = scheduleDao.insert(row2)

        dataSource.deleteScheduleByRowIdWithSessionCleanup(id1)
        assertNotNull(sessionDao.getById("sess-a"))

        dataSource.deleteScheduleByRowIdWithSessionCleanup(id2)
        assertNull(sessionDao.getById("sess-a"))
    }

    @Test
    fun deleteScheduleWithoutSession_doesNotTouchSessions() = runBlocking {
        val sessionDao = db.workoutSessionDao()
        val scheduleDao = db.workoutScheduleDao()
        val now = 1_700_000_000_000L
        sessionDao.insertSession(
            WorkoutSessionEntity(
                id = "sess-b",
                locationId = "loc1",
                startEpochMillis = now,
                endEpochMillis = now + 3_600_000L,
                dayKey = 19_000L,
            ),
        )
        val id = scheduleDao.insert(scheduleEntity("c-solo", sessionId = null, now = now))

        dataSource.deleteScheduleByRowIdWithSessionCleanup(id)

        assertNotNull(sessionDao.getById("sess-b"))
    }

    private fun scheduleEntity(clientId: String, sessionId: String?, now: Long) = WorkoutScheduleEntity(
        clientId = clientId,
        dayKey = 19_000L,
        exerciseId = "ex-1",
        sessionId = sessionId,
        scheduledAtEpochMillis = now,
        sets = 3,
        reps = 10,
        weightKg = 40.0,
        restSeconds = 90,
        notes = null,
        measurementMode = "strength",
        durationSeconds = null,
        locationId = "loc1",
        executionStatus = "scheduled",
        createdAtEpochMillis = now,
        updatedAtEpochMillis = now,
    )
}
