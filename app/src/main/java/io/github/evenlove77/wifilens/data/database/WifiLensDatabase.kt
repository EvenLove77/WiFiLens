package io.github.evenlove77.wifilens.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.github.evenlove77.wifilens.data.model.VaultItem

/**
 * SQLite 数据库（零依赖，代替 Room）
 */
class WifiLensDatabase(context: Context) : SQLiteOpenHelper(
    context.applicationContext, DB_NAME, null, DB_VERSION
) {

    companion object {
        private const val DB_NAME = "wifilens.db"
        private const val DB_VERSION = 1

        // Vault 表
        const val TABLE_VAULT = "wifi_vault"
        const val COL_VAULT_ID = "id"
        const val COL_VAULT_SSID = "ssid"
        const val COL_VAULT_PASSWORD = "password"
        const val COL_VAULT_REMARK = "remark"
        const val COL_VAULT_CATEGORY = "category"
        const val COL_VAULT_CREATED = "created_at"
        const val COL_VAULT_UPDATED = "updated_at"

    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_VAULT (
                $COL_VAULT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_VAULT_SSID TEXT NOT NULL,
                $COL_VAULT_PASSWORD TEXT NOT NULL DEFAULT '',
                $COL_VAULT_REMARK TEXT NOT NULL DEFAULT '',
                $COL_VAULT_CATEGORY TEXT NOT NULL DEFAULT '我的WiFi',
                $COL_VAULT_CREATED INTEGER NOT NULL,
                $COL_VAULT_UPDATED INTEGER NOT NULL
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VAULT")
        onCreate(db)
    }

    // ========== Vault CRUD ==========

    fun getAllVault(): List<VaultItem> {
        val db = readableDatabase
        val cursor = db.query(TABLE_VAULT, null, null, null, null, null, "$COL_VAULT_UPDATED DESC")
        val items = mutableListOf<VaultItem>()
        cursor.use {
            while (it.moveToNext()) {
                items.add(VaultItem(
                    id = it.getLong(it.getColumnIndexOrThrow(COL_VAULT_ID)),
                    ssid = it.getString(it.getColumnIndexOrThrow(COL_VAULT_SSID)),
                    password = it.getString(it.getColumnIndexOrThrow(COL_VAULT_PASSWORD)),
                    remark = it.getString(it.getColumnIndexOrThrow(COL_VAULT_REMARK)),
                    category = it.getString(it.getColumnIndexOrThrow(COL_VAULT_CATEGORY)),
                    createdAt = it.getLong(it.getColumnIndexOrThrow(COL_VAULT_CREATED)),
                    updatedAt = it.getLong(it.getColumnIndexOrThrow(COL_VAULT_UPDATED))
                ))
            }
        }
        return items
    }

    fun getVaultByCategory(category: String): List<VaultItem> {
        val db = readableDatabase
        val cursor = db.query(TABLE_VAULT, null, "$COL_VAULT_CATEGORY = ?", arrayOf(category), null, null, "$COL_VAULT_UPDATED DESC")
        val items = mutableListOf<VaultItem>()
        cursor.use {
            while (it.moveToNext()) {
                items.add(VaultItem(
                    id = it.getLong(it.getColumnIndexOrThrow(COL_VAULT_ID)),
                    ssid = it.getString(it.getColumnIndexOrThrow(COL_VAULT_SSID)),
                    password = it.getString(it.getColumnIndexOrThrow(COL_VAULT_PASSWORD)),
                    remark = it.getString(it.getColumnIndexOrThrow(COL_VAULT_REMARK)),
                    category = it.getString(it.getColumnIndexOrThrow(COL_VAULT_CATEGORY)),
                    createdAt = it.getLong(it.getColumnIndexOrThrow(COL_VAULT_CREATED)),
                    updatedAt = it.getLong(it.getColumnIndexOrThrow(COL_VAULT_UPDATED))
                ))
            }
        }
        return items
    }

    fun insertVault(item: VaultItem): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_VAULT_SSID, item.ssid)
            put(COL_VAULT_PASSWORD, item.password)
            put(COL_VAULT_REMARK, item.remark)
            put(COL_VAULT_CATEGORY, item.category)
            put(COL_VAULT_CREATED, item.createdAt)
            put(COL_VAULT_UPDATED, item.updatedAt)
        }
        return db.insert(TABLE_VAULT, null, values)
    }

    fun updateVault(item: VaultItem) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_VAULT_SSID, item.ssid)
            put(COL_VAULT_PASSWORD, item.password)
            put(COL_VAULT_REMARK, item.remark)
            put(COL_VAULT_CATEGORY, item.category)
            put(COL_VAULT_UPDATED, item.updatedAt)
        }
        db.update(TABLE_VAULT, values, "$COL_VAULT_ID = ?", arrayOf(item.id.toString()))
    }

    fun deleteVault(id: Long) {
        writableDatabase.delete(TABLE_VAULT, "$COL_VAULT_ID = ?", arrayOf(id.toString()))
    }

    fun insertAllVault(items: List<VaultItem>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            items.forEach { item ->
                val values = ContentValues().apply {
                    put(COL_VAULT_SSID, item.ssid)
                    put(COL_VAULT_PASSWORD, item.password)
                    put(COL_VAULT_REMARK, item.remark)
                    put(COL_VAULT_CATEGORY, item.category)
                    put(COL_VAULT_CREATED, item.createdAt)
                    put(COL_VAULT_UPDATED, item.updatedAt)
                }
                db.insert(TABLE_VAULT, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun deleteAllVault() {
        writableDatabase.delete(TABLE_VAULT, null, null)
    }

    fun hasVaultData(): Boolean {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_VAULT", null)
        return cursor.use {
            it.moveToFirst() && it.getInt(0) > 0
        }
    }

}
