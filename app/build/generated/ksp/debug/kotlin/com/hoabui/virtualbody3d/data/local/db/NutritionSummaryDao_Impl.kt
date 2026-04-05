package com.hoabui.virtualbody3d.`data`.local.db

import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class NutritionSummaryDao_Impl(
  __db: RoomDatabase,
) : NutritionSummaryDao {
  private val __db: RoomDatabase
  init {
    this.__db = __db
  }

  public override fun observeActive(): Flow<NutritionSummaryEntity?> {
    val _sql: String = "SELECT * FROM nutrition_summary WHERE id = 1"
    return createFlow(__db, false, arrayOf("nutrition_summary")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfIntake: Int = getColumnIndexOrThrow(_stmt, "intake")
        val _columnIndexOfBurned: Int = getColumnIndexOrThrow(_stmt, "burned")
        val _columnIndexOfGoal: Int = getColumnIndexOrThrow(_stmt, "goal")
        val _result: NutritionSummaryEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpIntake: Int
          _tmpIntake = _stmt.getLong(_columnIndexOfIntake).toInt()
          val _tmpBurned: Int
          _tmpBurned = _stmt.getLong(_columnIndexOfBurned).toInt()
          val _tmpGoal: Int
          _tmpGoal = _stmt.getLong(_columnIndexOfGoal).toInt()
          _result = NutritionSummaryEntity(_tmpId,_tmpIntake,_tmpBurned,_tmpGoal)
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
