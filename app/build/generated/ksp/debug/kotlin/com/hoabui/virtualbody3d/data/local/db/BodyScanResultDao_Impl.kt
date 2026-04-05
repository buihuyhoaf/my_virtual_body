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
public class BodyScanResultDao_Impl(
  __db: RoomDatabase,
) : BodyScanResultDao {
  private val __db: RoomDatabase
  init {
    this.__db = __db
  }

  public override fun observeActive(): Flow<BodyScanResultEntity?> {
    val _sql: String = "SELECT * FROM body_scan_results WHERE id = 1"
    return createFlow(__db, false, arrayOf("body_scan_results")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPayloadJson: Int = getColumnIndexOrThrow(_stmt, "payload_json")
        val _result: BodyScanResultEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpPayloadJson: String
          _tmpPayloadJson = _stmt.getText(_columnIndexOfPayloadJson)
          _result = BodyScanResultEntity(_tmpId,_tmpPayloadJson)
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
