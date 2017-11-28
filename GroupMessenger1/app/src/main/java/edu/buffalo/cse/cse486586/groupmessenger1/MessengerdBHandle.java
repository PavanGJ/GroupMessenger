package edu.buffalo.cse.cse486586.groupmessenger1;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
/**
 * Created by pavanjoshi on 2/15/17.
 */

public class MessengerdBHandle extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = MessengerdBContract.DATABASE_NAME;
    public static final int DATABASE_NUMBER = 1;

    public MessengerdBHandle(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_NUMBER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MessengerdBContract.DROP_TABLE_MESSAGE);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MessengerdBContract.CREATE_TABLE_MESSAGE);
    }

}
