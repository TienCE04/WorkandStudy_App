package com.example.workandstudy_app.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.workandstudy_app.document.DAO.DocumentDao
import com.example.workandstudy_app.document.DAO.SubjectDao
import com.example.workandstudy_app.document.Entity.Documents
import com.example.workandstudy_app.document.Entity.Subjects
import com.example.workandstudy_app.todolist.DAO.TasksDao
import com.example.workandstudy_app.todolist.Entity.TasksData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import androidx.core.content.edit

@Database(
    entities = [Subjects::class, Documents::class, TasksData::class],
    version = 2, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun monHocDao(): SubjectDao
    abstract fun taiLieuDao(): DocumentDao
    abstract fun tasksDao(): TasksDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tasks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        dueDate TEXT,
                        isCompleted INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                if (!prefs.getBoolean("is_data_populated", false)) {
                                    loadDataFromDefaultJson(
                                        getDatabase(context),
                                        context,
                                        "default_data.json"
                                    )
                                    prefs.edit { putBoolean("is_data_populated", true) }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun loadDataFromDefaultJson(
            database: AppDatabase,
            context: Context,
            fileName: String
        ) {
            try {
                val jsonString = context.assets.open(fileName).use { inputStream ->
                    String(inputStream.readBytes(), Charsets.UTF_8)
                }
                val jsonObject = JSONObject(jsonString)

                // Chèn Subjects
                val monHocArray = jsonObject.getJSONArray("mon_hoc")
                for (i in 0 until monHocArray.length()) {
                    val item = monHocArray.getJSONObject(i)
                    database.monHocDao().insert(
                        Subjects(
                            tenMonHoc = item.getString("tenMonHoc"),
                            ngayThem = item.getString("ngayThem"),
                            isDefault = 1
                        )
                    )
                }

                // Chèn Documents
                val taiLieuArray = jsonObject.getJSONArray("tai_lieu")
                for (i in 0 until taiLieuArray.length()) {
                    val item = taiLieuArray.getJSONObject(i)
                    database.taiLieuDao().insert(
                        Documents(
                            monHocId = item.getInt("monHocId"),
                            danhMuc = item.getString("danhMuc"),
                            phanLoai = item.getString("phanLoai"),
                            tenFile = item.getString("tenFile"),
                            urlFile = item.getString("urlFile"),
                            ngayTao = item.getString("ngayTao"),
                            isDefault = 1
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}