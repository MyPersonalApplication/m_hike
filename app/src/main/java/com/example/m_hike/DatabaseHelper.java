package com.example.m_hike;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import com.example.m_hike.model.Hike;
import com.example.m_hike.model.Observation;
import com.example.m_hike.model.Photo;
import com.example.m_hike.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper implements Serializable {
    private static final String DATABASE_NAME = "hiker_management";
    private static final int DATABASE_VERSION = 13;

    // Table User
    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_NAME = "username";
    private static final String COLUMN_USER_FULL_NAME = "full_name";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_AVATAR = "avatar";
    private static final String COLUMN_USER_CREATED = "created";

    // Table Hike
    private static final String TABLE_HIKE = "hike";
    private static final String COLUMN_HIKE_ID = "id";
    private static final String COLUMN_HIKE_USER_NAME = "username";
    private static final String COLUMN_HIKE_NAME = "name";
    private static final String COLUMN_HIKE_LOCATION = "location";
    private static final String COLUMN_HIKE_LATITUDE = "latitude";
    private static final String COLUMN_HIKE_LONGITUDE = "longitude";
    private static final String COLUMN_HIKE_DATE = "date";
    private static final String COLUMN_HIKE_PARKING_AVAILABLE = "parking_available";
    private static final String COLUMN_HIKE_LENGTH = "length";
    private static final String COLUMN_HIKE_DIFFICULTY_LEVEL = "difficulty_level";
    private static final String COLUMN_HIKE_DESCRIPTION = "description";

    // Table Observation
    private static final String TABLE_OBSERVATION = "observation";
    private static final String COLUMN_OBSERVATION_ID = "id";
    private static final String COLUMN_OBSERVATION_HIKE_ID = "hike_id";
    private static final String COLUMN_OBSERVATION_NAME = "name";
    private static final String COLUMN_OBSERVATION_TIME = "time";
    private static final String COLUMN_OBSERVATION_ADDITIONAL_COMMENT = "additional_comment";

    // Table Photo
    private static final String TABLE_PHOTO = "photo";
    private static final String COLUMN_PHOTO_ID = "id";
    private static final String COLUMN_PHOTO_OBSERVATION_ID = "observation_id";
    private static final String COLUMN_PHOTO_TITLE = "title";
    private static final String COLUMN_PHOTO_DESCRIPTION = "description";
    private static final String COLUMN_PHOTO_URL = "url";
    private static final String COLUMN_PHOTO_TIMESTAMP = "timestamp";

    // Create table User
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + " (" +
            COLUMN_USER_NAME + " VARCHAR(100) PRIMARY KEY, " +
            COLUMN_USER_FULL_NAME + " TEXT NOT NULL, " +
            COLUMN_USER_PASSWORD + " VARCHAR(60) NOT NULL, " +
            COLUMN_USER_AVATAR + " BLOB NOT NULL, " +
            COLUMN_USER_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    // Create Table Hike
    private static final String CREATE_TABLE_HIKE = "CREATE TABLE " + TABLE_HIKE + " (" +
            COLUMN_HIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_HIKE_USER_NAME + " VARCHAR(100) NOT NULL, " +
            COLUMN_HIKE_NAME + " TEXT NOT NULL, " +
            COLUMN_HIKE_LOCATION + " TEXT NOT NULL, " +
            COLUMN_HIKE_LATITUDE + " FLOAT, " +
            COLUMN_HIKE_LONGITUDE + " FLOAT, " +
            COLUMN_HIKE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            COLUMN_HIKE_PARKING_AVAILABLE + " VARCHAR(10) NOT NULL, " +
            COLUMN_HIKE_LENGTH + " FLOAT NOT NULL, " +
            COLUMN_HIKE_DIFFICULTY_LEVEL + " VARCHAR(20) NOT NULL, " +
            COLUMN_HIKE_DESCRIPTION + " TEXT, " +
            "FOREIGN KEY (" + COLUMN_HIKE_USER_NAME + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_NAME + ") ON DELETE CASCADE)";


    // Create Table Observation
    private static final String CREATE_TABLE_OBSERVATION = "CREATE TABLE " + TABLE_OBSERVATION + " (" +
            COLUMN_OBSERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_OBSERVATION_HIKE_ID + " INTEGER NOT NULL, " +
            COLUMN_OBSERVATION_NAME + " TEXT NOT NULL, " +
            COLUMN_OBSERVATION_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            COLUMN_OBSERVATION_ADDITIONAL_COMMENT + " TEXT, " +
            "FOREIGN KEY (" + COLUMN_OBSERVATION_HIKE_ID + ") REFERENCES " + TABLE_HIKE + "(" + COLUMN_HIKE_ID + ") ON DELETE CASCADE)";

    // Create Table Photo
    private static final String CREATE_TABLE_PHOTO = "CREATE TABLE " + TABLE_PHOTO + " (" +
            COLUMN_PHOTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PHOTO_OBSERVATION_ID + " INTEGER NOT NULL, " +
            COLUMN_PHOTO_TITLE + " TEXT NOT NULL, " +
            COLUMN_PHOTO_DESCRIPTION + " TEXT, " +
            COLUMN_PHOTO_URL + " BLOB NOT NULL, " +
            COLUMN_PHOTO_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (" + COLUMN_PHOTO_OBSERVATION_ID + ") REFERENCES " + TABLE_OBSERVATION + "(" + COLUMN_OBSERVATION_ID + ") ON DELETE CASCADE)";

    // Create trigger to delete all hikes when a user is deleted
    private static final String CREATE_TRIGGER_DELETE_USER = "CREATE TRIGGER delete_user " +
            "AFTER DELETE ON " + TABLE_USER + " " +
            "FOR EACH ROW " +
            "BEGIN " +
            "DELETE FROM " + TABLE_HIKE + " WHERE " + COLUMN_HIKE_USER_NAME + " = OLD." + COLUMN_USER_NAME + "; " +
            "END";

    // Create trigger to delete all observations when a hike is deleted
    private static final String CREATE_TRIGGER_DELETE_HIKE = "CREATE TRIGGER delete_hike " +
            "AFTER DELETE ON " + TABLE_HIKE + " " +
            "FOR EACH ROW " +
            "BEGIN " +
            "DELETE FROM " + TABLE_OBSERVATION + " WHERE " + COLUMN_OBSERVATION_HIKE_ID + " = OLD." + COLUMN_HIKE_ID + "; " +
            "END";

    // Create trigger to delete all photos when an observation is deleted
    private static final String CREATE_TRIGGER_DELETE_OBSERVATION = "CREATE TRIGGER delete_observation " +
            "AFTER DELETE ON " + TABLE_OBSERVATION + " " +
            "FOR EACH ROW " +
            "BEGIN " +
            "DELETE FROM " + TABLE_PHOTO + " WHERE " + COLUMN_PHOTO_OBSERVATION_ID + " = OLD." + COLUMN_OBSERVATION_ID + "; " +
            "END";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_HIKE);
        db.execSQL(CREATE_TABLE_OBSERVATION);
        db.execSQL(CREATE_TABLE_PHOTO);
        db.execSQL(CREATE_TRIGGER_DELETE_USER);
        db.execSQL(CREATE_TRIGGER_DELETE_HIKE);
        db.execSQL(CREATE_TRIGGER_DELETE_OBSERVATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop Table Photo
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        // Drop Table Observation
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATION);
        // Drop Table Hike
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKE);
        // Drop Table User
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // Recreate
        onCreate(db);
    }

    public List<User> getAllUsers() {
        SQLiteDatabase database = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USER + " ORDER BY " + COLUMN_USER_CREATED + " DESC";
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(query, null);

        List<User> userList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_FULL_NAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD));
            byte[] avatar = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_USER_AVATAR));
            String created = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_CREATED));

            userList.add(new User(username, fullName, password, avatar, created));
        }

        return userList;
    }

    public void updateUser(User user) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "UPDATE " + TABLE_USER + " SET " +
                COLUMN_USER_FULL_NAME + " = ?, " +
                COLUMN_USER_PASSWORD + " = ?, " +
                COLUMN_USER_AVATAR + " = ? " +
                "WHERE " + COLUMN_USER_NAME + " = ?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, user.getFullName());
        statement.bindString(2, user.getPassword());
        statement.bindBlob(3, user.getAvatar());
        statement.bindString(4, user.getUsername());
        statement.executeUpdateDelete();
    }

    public void DeleteUser(String username) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_USER + " WHERE " + COLUMN_USER_NAME + " = ?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, username);
        statement.executeUpdateDelete();
    }

    public void addNewUser(User addUser) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "INSERT INTO " + TABLE_USER + " (" +
                COLUMN_USER_NAME + ", " +
                COLUMN_USER_FULL_NAME + ", " +
                COLUMN_USER_PASSWORD + ", " +
                COLUMN_USER_AVATAR + ") " +
                "VALUES (?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, addUser.getUsername());
        statement.bindString(2, addUser.getFullName());
        statement.bindString(3, addUser.getPassword());
        statement.bindBlob(4, addUser.getAvatar());
        statement.executeInsert();
    }

    public List<Hike> getAllHikes(String username) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_HIKE + " WHERE " + COLUMN_HIKE_USER_NAME + " = ? ORDER BY " + COLUMN_HIKE_DATE + " DESC";
        String[] selectionArgs = new String[]{username};
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(query, selectionArgs);

        List<Hike> hikeList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HIKE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_NAME));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_LOCATION));
            Float latitude = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HIKE_LATITUDE));
            Float longitude = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HIKE_LONGITUDE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_DATE));
            String parkingAvailable = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_PARKING_AVAILABLE));
            Float length = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HIKE_LENGTH));
            String difficultyLevel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_DIFFICULTY_LEVEL));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_DESCRIPTION));

            hikeList.add(new Hike(id, username, name, location, latitude, longitude, date, parkingAvailable, length, difficultyLevel, description));
        }

        return hikeList;
    }

    public void saveHike(Hike reply) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "INSERT INTO " + TABLE_HIKE + " (" +
                COLUMN_HIKE_USER_NAME + ", " +
                COLUMN_HIKE_NAME + ", " +
                COLUMN_HIKE_LOCATION + ", " +
                COLUMN_HIKE_LATITUDE + ", " +
                COLUMN_HIKE_LONGITUDE + ", " +
                COLUMN_HIKE_DATE + ", " +
                COLUMN_HIKE_PARKING_AVAILABLE + ", " +
                COLUMN_HIKE_LENGTH + ", " +
                COLUMN_HIKE_DIFFICULTY_LEVEL + ", " +
                COLUMN_HIKE_DESCRIPTION + ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, reply.getUsername());
        statement.bindString(2, reply.getName());
        statement.bindString(3, reply.getLocation());
        statement.bindDouble(4, reply.getLatitude());
        statement.bindDouble(5, reply.getLongitude());
        statement.bindString(6, reply.getDate());
        statement.bindString(7, reply.getParkingAvailable());
        statement.bindDouble(8, reply.getLength());
        statement.bindString(9, reply.getDifficultyLevel());
        statement.bindString(10, reply.getDescription());
        statement.executeInsert();
    }

    public void updateHike(Hike hike) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "UPDATE " + TABLE_HIKE + " SET " +
                COLUMN_HIKE_NAME + " = ?, " +
                COLUMN_HIKE_LOCATION + " = ?, " +
                COLUMN_HIKE_LATITUDE + " = ?, " +
                COLUMN_HIKE_LONGITUDE + " = ?, " +
                COLUMN_HIKE_PARKING_AVAILABLE + " = ?, " +
                COLUMN_HIKE_LENGTH + " = ?, " +
                COLUMN_HIKE_DIFFICULTY_LEVEL + " = ?, " +
                COLUMN_HIKE_DESCRIPTION + " = ? " +
                "WHERE " + COLUMN_HIKE_ID + " = ?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, hike.getName());
        statement.bindString(2, hike.getLocation());
        statement.bindDouble(3, hike.getLatitude());
        statement.bindDouble(4, hike.getLongitude());
        statement.bindString(5, hike.getParkingAvailable());
        statement.bindDouble(6, hike.getLength());
        statement.bindString(7, hike.getDifficultyLevel());
        statement.bindString(8, hike.getDescription());
        statement.bindLong(9, hike.getId());
        statement.executeUpdateDelete();
    }

    public void deleteHike(long id) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_HIKE + " WHERE " + COLUMN_HIKE_ID + " = ?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.executeUpdateDelete();
    }

    public List<Observation> getAllObservations(long hikeId) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_OBSERVATION + " WHERE " + COLUMN_OBSERVATION_HIKE_ID + " = ? ORDER BY " + COLUMN_OBSERVATION_TIME + " DESC";
        String[] selectionArgs = new String[]{String.valueOf(hikeId)};
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(query, selectionArgs);

        List<Observation> observationList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_OBSERVATION_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OBSERVATION_NAME));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OBSERVATION_TIME));
            String additionalComment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OBSERVATION_ADDITIONAL_COMMENT));

            observationList.add(new Observation(id, hikeId, name, time, additionalComment));
        }

        return observationList;
    }

    public void addObservation(Observation observation) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "INSERT INTO " + TABLE_OBSERVATION + " (" +
                COLUMN_OBSERVATION_HIKE_ID + ", " +
                COLUMN_OBSERVATION_NAME + ", " +
                COLUMN_OBSERVATION_TIME + ", " +
                COLUMN_OBSERVATION_ADDITIONAL_COMMENT + ") " +
                "VALUES (?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindLong(1, observation.getHikeId());
        statement.bindString(2, observation.getName());
        statement.bindString(3, observation.getTime());
        statement.bindString(4, observation.getAdditionalComment());
        statement.executeInsert();
    }

    public void DeleteObservation(long id) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_OBSERVATION + " WHERE " + COLUMN_OBSERVATION_ID + " = ?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.executeUpdateDelete();
    }

    public void UpdateObservation(Observation observation) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "UPDATE " + TABLE_OBSERVATION + " SET " +
                COLUMN_OBSERVATION_NAME + " = ?, " +
                COLUMN_OBSERVATION_ADDITIONAL_COMMENT + " = ? " +
                "WHERE " + COLUMN_OBSERVATION_ID + " = ?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, observation.getName());
        statement.bindString(2, observation.getAdditionalComment());
        statement.bindLong(3, observation.getId());
        statement.executeUpdateDelete();
    }

    public List<Photo> getAllPhotos(long observationId) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PHOTO + " WHERE " + COLUMN_PHOTO_OBSERVATION_ID + " = ? ORDER BY " + COLUMN_PHOTO_TIMESTAMP + " DESC";
        String[] selectionArgs = new String[]{String.valueOf(observationId)};
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(query, selectionArgs);

        List<Photo> photoList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_DESCRIPTION));
            byte[] url = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URL));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_TIMESTAMP));

            photoList.add(new Photo(id, observationId, title, description, url, timestamp));
        }

        return photoList;
    }

    public void addPhoto(Photo photo) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "INSERT INTO " + TABLE_PHOTO + " (" +
                COLUMN_PHOTO_OBSERVATION_ID + ", " +
                COLUMN_PHOTO_TITLE + ", " +
                COLUMN_PHOTO_DESCRIPTION + ", " +
                COLUMN_PHOTO_URL + ") " +
                "VALUES (?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindLong(1, photo.getObservationId());
        statement.bindString(2, photo.getTitle());
        statement.bindString(3, photo.getDescription());
        statement.bindBlob(4, photo.getImageUrl());
        statement.executeInsert();
    }

    public void updatePhoto(Photo photo) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "UPDATE " + TABLE_PHOTO + " SET " +
                COLUMN_PHOTO_TITLE + " = ?, " +
                COLUMN_PHOTO_DESCRIPTION + " = ?, " +
                COLUMN_PHOTO_URL + " = ? " +
                "WHERE " + COLUMN_PHOTO_ID + " = ?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindString(1, photo.getTitle());
        statement.bindString(2, photo.getDescription());
        statement.bindBlob(3, photo.getImageUrl());
        statement.bindLong(4, photo.getId());
        statement.executeUpdateDelete();
    }

    public void deletePhoto(long id) {
        SQLiteDatabase database = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_PHOTO + " WHERE " + COLUMN_PHOTO_ID + " = ?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.executeUpdateDelete();
    }
}
