package com.karikeo.cashless.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Calendar;

public class TransactionDataSource {
    private static final String TAG = "TransactionDataSource";

    private SQLiteDatabase database;
    private TransactionsSQLHelper dbHelper;
    private String[] allColumns = { TransactionsSQLHelper.COLUMN_ID,
            TransactionsSQLHelper.COLUMN_TRANSACTION_TYPE,
            TransactionsSQLHelper.COLUMN_MACADDR,
            TransactionsSQLHelper.COLUMN_DATE,
            TransactionsSQLHelper.COLUMN_BALANCE_DELTA,
            TransactionsSQLHelper.COLUMN_EMAIL,
            TransactionsSQLHelper.COLUMN_SENT};

    private final int FALSE = 0;
    private final int TRUE = 1;


    public TransactionDataSource(Context context){
        dbHelper = new TransactionsSQLHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Transaction createTransaction(final String tType, final String balanceDelta, final String macAddr, String email){
        if ( database == null || !database.isOpen() )
            open();

        ContentValues values = new ContentValues();

        values.put(TransactionsSQLHelper.COLUMN_TRANSACTION_TYPE, tType);
        values.put(TransactionsSQLHelper.COLUMN_BALANCE_DELTA, Integer.valueOf(balanceDelta));
        values.put(TransactionsSQLHelper.COLUMN_MACADDR, macAddr);
        values.put(TransactionsSQLHelper.COLUMN_DATE, Integer.toString(Calendar.getInstance().get(Calendar.SECOND)));
        values.put(TransactionsSQLHelper.COLUMN_EMAIL, email);
        values.put(TransactionsSQLHelper.COLUMN_SENT, FALSE);

        long insertId = database.insert(TransactionsSQLHelper.TABLE_TRANSACTION, null, values);

        if (insertId == -1){
            Log.e(TAG, "Can't insert Transaction.");
            return null;
        }

        Log.w(TAG, "Create with id=" + Long.toString(insertId));

        Cursor cursor = database.query(TransactionsSQLHelper.TABLE_TRANSACTION, allColumns,
                TransactionsSQLHelper.COLUMN_ID + " = " + insertId, null, null, null, null);

        cursor.moveToFirst();
        Transaction newTransaction = cursorToTransaction(cursor);
        cursor.close();
        return  newTransaction;
    }

    public Float getBalanceDeltaFromAllTransactions(){
        if (database == null || !database.isOpen())
            open();

        Cursor cursor = null;
        String sum = null;
        try {
            //cursor = database.rawQuery("SELECT SUM(?) FROM ?", new String[]{TransactionsSQLHelper.COLUMN_BALANCE_DELTA, TransactionsSQLHelper.TABLE_TRANSACTION});
            //cursor = database.rawQuery("SELECT SUM(" + TransactionsSQLHelper.COLUMN_BALANCE_DELTA + ") FROM " + TransactionsSQLHelper.TABLE_TRANSACTION + , new String[]{});
            cursor = database.rawQuery(String.format("SELECT SUM(%s) FROM %s WHERE %s = %d", TransactionsSQLHelper.COLUMN_BALANCE_DELTA, TransactionsSQLHelper.TABLE_TRANSACTION,
                    TransactionsSQLHelper.COLUMN_SENT, FALSE), new String[]{});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                sum = cursor.getString(0);
            }
        }
        finally {
            closeCursor(cursor);
        }
        if (sum == null){
            sum = "0";
        }

        return Float.valueOf(sum);
    }

    public Transaction[] getTransactions(){
        if ( database == null || !database.isOpen() ){
            open();
        }

        Transaction[] transactions = null;
        Cursor cursor = null;
        try{
            cursor = database.rawQuery(String.format("SELECT * FROM %s WHERE %s = %d",
                    TransactionsSQLHelper.TABLE_TRANSACTION,
                    TransactionsSQLHelper.COLUMN_SENT,
                    0), new String[]{});
            //cursor = database.rawQuery("SELECT * FROM "+ TransactionsSQLHelper.TABLE_TRANSACTION, new String[]{});

            final int num = cursor.getCount();
            Log.d(TAG, String.format("getTransactions: num of transactions:%s", String.valueOf(num)));

            if ( num == 0){
                closeCursor(cursor);
                return transactions;
            }

            transactions = new Transaction[num];

            for (int i = 0; i<num; i++){
                cursor.moveToPosition(i);
                transactions[i] = cursorToTransaction(cursor);
            }

        }finally {
            closeCursor(cursor);
        }


        return transactions;
    }

    private void closeCursor(Cursor cursor) {
        if (cursor!=null){
            cursor.close();
        }
    }

    public boolean updateSent(Transaction transaction, int sent){
        if (database == null || !database.isOpen())
            open();

        boolean flg = false;

        Cursor cursor = null;
        try {
            //cursor = database.rawQuery("SELECT SUM(?) FROM ?", new String[]{TransactionsSQLHelper.COLUMN_BALANCE_DELTA, TransactionsSQLHelper.TABLE_TRANSACTION});
            //cursor = database.rawQuery("SELECT SUM(" + TransactionsSQLHelper.COLUMN_BALANCE_DELTA + ") FROM " + TransactionsSQLHelper.TABLE_TRANSACTION + , new String[]{});
            cursor = database.rawQuery(String.format("UPDATE %s SET %s = %d WHERE %s = %d",
                    TransactionsSQLHelper.TABLE_TRANSACTION,
                    TransactionsSQLHelper.COLUMN_SENT,
                    sent,
                    TransactionsSQLHelper.COLUMN_ID,
                    transaction.getId()), new String[]{});
            if (cursor.getCount() > 0) {
                flg = true;
            }
        }
        finally {
            closeCursor(cursor);
        }

        return flg;
    }

    public void deleteTransaction(Transaction transaction){
        long id = transaction.getId();
        Log.w(TAG, "delete with id=" + Long.toString(id));

        database.delete(TransactionsSQLHelper.TABLE_TRANSACTION, TransactionsSQLHelper.COLUMN_ID + " = " + id, null);
    }

    private Transaction cursorToTransaction(Cursor cursor){
        Transaction t = new Transaction();
        t.setId(cursor.getLong(0));
        t.setType(Transaction.TYPE.fromString(cursor.getString(1)));
        t.setMacAddress(cursor.getString(2));
        t.setDate(cursor.getString(3));
        t.setBalanceDelta(cursor.getString(4));
        t.setEmail(cursor.getString(5));
        t.setSent(cursor.getInt(6));

        return t;
    }
}

