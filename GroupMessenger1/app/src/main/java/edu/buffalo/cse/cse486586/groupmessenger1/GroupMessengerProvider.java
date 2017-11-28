package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Messenger;
import android.util.Log;
import android.database.sqlite.*;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {

    SQLiteDatabase MessengerdB;
    private static final String TAG=GroupMessengerProvider.class.getName();

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
        try {
            ContentValues updateValues = new ContentValues();
            updateValues.put(MessengerdBContract.dBMessages.COLUMN_NAME_2, (String) values.get(MessengerdBContract.dBMessages.COLUMN_NAME_2));
            String selection = MessengerdBContract.dBMessages.COLUMN_NAME_1 + " = ?";
            String selectionArgs[] = {(String) values.get(MessengerdBContract.dBMessages.COLUMN_NAME_1)};
            int out = this.update(uri, updateValues, selection, selectionArgs);
            if (out == 0) {
                long res = MessengerdB.insert(MessengerdBContract.dBMessages.TABLE_NAME, null, values);
            }
        }catch (Exception e){
            Log.e(TAG,"dB Insertion/Update Error");
            e.printStackTrace();
        }
        Log.v("insert", values.toString());
        return uri;
    }

    @Override
    public boolean onCreate() {
        try {
            MessengerdBHandle dbHandle = new MessengerdBHandle(this.getContext());
            MessengerdB = dbHandle.getWritableDatabase();
            return true;
        }catch (Exception e){
            Log.e(TAG,"dB Creation Error");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int ret = MessengerdB.update(
                MessengerdBContract.dBMessages.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        return ret;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
        String dBProjection[] = {
                MessengerdBContract.dBMessages.COLUMN_NAME_1,
                MessengerdBContract.dBMessages.COLUMN_NAME_2
        };
        String selection_criteria = MessengerdBContract.dBMessages.COLUMN_NAME_1+" = ?";
        String selection_args[] = {selection};
        try {
            Cursor dBCursor = MessengerdB.query(
                    MessengerdBContract.dBMessages.TABLE_NAME,
                    dBProjection,
                    selection_criteria,
                    selection_args,
                    null,
                    null,
                    sortOrder
            );
            Log.v("query", selection);
            return dBCursor;
        }catch (Exception e){
            Log.e(TAG,"dB Query Error");
            e.printStackTrace();
        }
        return null;
    }
}
