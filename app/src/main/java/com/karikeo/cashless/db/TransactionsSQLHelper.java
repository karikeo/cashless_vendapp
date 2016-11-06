package com.karikeo.cashless.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TransactionsSQLHelper extends SQLiteOpenHelper {

    private static final String TAG = "com.karikeo.cashless.db.TransactionsSQLHelper";

    public static final String TABLE_TRANSACTION = "transactions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TRANSACTION_TYPE = "transaction";
    public static final String COLUMN_MACADDR = "mac_addr";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_BALANCE_DELTA = "balance_delta";

    private static final String DATABASE_NAME = "transactions.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TRANSACTION + "( " +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_TRANSACTION_TYPE + " text not null, " +
            COLUMN_MACADDR + " text not null, " +
            COLUMN_DATE + " text not null, " +
            COLUMN_BALANCE_DELTA + " text not null);";

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