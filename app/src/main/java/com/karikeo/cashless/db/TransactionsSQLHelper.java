package com.karikeo.cashless.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TransactionsSQLHelper extends SQLiteOpenHelper {

    private static final String TAG = "com.karikeo.cashless.db.TransactionsSQLHelper";

    public static final String TABLE_TRANSACTION = "transactions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TRANSACTION_TYPE = "trans";
    public static final String COLUMN_MACADDR = "mac_addr";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_BALANCE_DELTA = "balance_delta";

    private static final String DATABASE_NAME = "transactions.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_TRANSACTION + "( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TRANSACTION_TYPE + " TEXT NOT NULL , " +
            COLUMN_MACADDR + " TEXT NOT NULL , " +
            COLUMN_DATE + " TEXT NOT NULL , " +
            COLUMN_BALANCE_DELTA + " INTEGER NOT NULL" +
            ");";

    public TransactionsSQLHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading from version " + oldVersion + " to " + newVersion + ", destroy old data");

        db.execSQL("DROP TABLE IF EXISTS" + TABLE_TRANSACTION);
        onCreate(db);
    }
}
