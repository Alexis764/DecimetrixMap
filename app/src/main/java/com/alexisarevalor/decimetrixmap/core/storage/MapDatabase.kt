package com.alexisarevalor.decimetrixmap.core.storage

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MapDatabase @Inject constructor(
    @ApplicationContext val context: Context
) : SQLiteOpenHelper(context, "MapDatabase", null, 1) {

    //Constants
    companion object {
        //Database tables
        const val POINT = "point"

        //Point table columns
        const val POINT_ID = "point_id"
        const val POINT_NAME = "point_name"
        const val POINT_LATITUDE = "point_latitude"
        const val POINT_LONGITUDE = "point_longitude"
        const val POINT_ALERT = "point_alert"
    }

    //Function to create database tables
    override fun onCreate(db: SQLiteDatabase?) {
        val sqlPointTable = "CREATE TABLE $POINT (" +
                "$POINT_ID INTEGER PRIMARY KEY, " +
                "$POINT_NAME TEXT, " +
                "$POINT_LATITUDE REAL, " +
                "$POINT_LONGITUDE REAL, " +
                "$POINT_ALERT INTEGER)"

        db!!.execSQL(sqlPointTable)
    }

    //Function to upgrade the database
    override fun onUpgrade(
        db: SQLiteDatabase?,
        p1: Int,
        p2: Int
    ) {
        val sqlDropPointTable = "DROP TABLE IF EXISTS $POINT"
        db!!.execSQL(sqlDropPointTable)
        onCreate(db)
    }


    //FUNCTIONS TO POINT TABLE
    //Function to insert a new point
    fun insertPoint(
        pointName: String,
        pointLatitude: Double,
        pointLongitude: Double,
        pointAlert: Int
    ) {
        val data = ContentValues()
        data.put(POINT_NAME, pointName)
        data.put(POINT_LATITUDE, pointLatitude)
        data.put(POINT_LONGITUDE, pointLongitude)
        data.put(POINT_ALERT, pointAlert)

        val db = this.writableDatabase
        db.insert(POINT, null, data)
    }

    //Function to get all points
    @SuppressLint("Range")
    fun getAllPoints(): List<PointModel> {
        val sql = "SELECT * FROM $POINT"
        val db = this.readableDatabase
        val cursor = db.rawQuery(sql, null)
        val points = mutableListOf<PointModel>()

        if (cursor.moveToFirst()) {
            do {
                val pointId = cursor.getInt(cursor.getColumnIndex(POINT_ID))
                val pointName = cursor.getString(cursor.getColumnIndex(POINT_NAME))
                val pointLatitude = cursor.getDouble(cursor.getColumnIndex(POINT_LATITUDE))
                val pointLongitude = cursor.getDouble(cursor.getColumnIndex(POINT_LONGITUDE))
                val pointAlert = cursor.getInt(cursor.getColumnIndex(POINT_ALERT))

                points.add(
                    PointModel(
                        pointId = pointId,
                        pointName = pointName,
                        pointLatitude = pointLatitude,
                        pointLongitude = pointLongitude,
                        pointAlert = pointAlert
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return points
    }

    //Function to get a point by id
    @SuppressLint("Range")
    fun getPointById(pointId: Int): PointModel? {
        val sql = "SELECT * FROM $POINT WHERE $POINT_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(sql, arrayOf(pointId.toString()))

        if (cursor.moveToFirst()) {
            val pointName = cursor.getString(cursor.getColumnIndex(POINT_NAME))
            val pointLatitude = cursor.getDouble(cursor.getColumnIndex(POINT_LATITUDE))
            val pointLongitude = cursor.getDouble(cursor.getColumnIndex(POINT_LONGITUDE))
            val pointAlert = cursor.getInt(cursor.getColumnIndex(POINT_ALERT))

            cursor.close()
            return PointModel(
                pointId = pointId,
                pointName = pointName,
                pointLatitude = pointLatitude,
                pointLongitude = pointLongitude,
                pointAlert = pointAlert
            )
        }

        cursor.close()
        return null
    }

}