package edu.buffalo.cse.cse486586.groupmessenger1;


/**
 * Created by pavanjoshi on 2/15/17.
 */

public final class MessengerdBContract {
    private MessengerdBContract(){}
    public static final String DATABASE_NAME = "GroupMessenger.db";
    public static final String CREATE_TABLE_MESSAGE = "CREATE TABLE " + dBMessages.TABLE_NAME + " (" +
            dBMessages.COLUMN_NAME_1 + " TEXT PRIMARY KEY," +
            dBMessages.COLUMN_NAME_2 + " TEXT)";
    public static final String DROP_TABLE_MESSAGE = "DROP TABLE IF EXISTS "+dBMessages.TABLE_NAME;
    public static class dBMessages{
        public static final String TABLE_NAME= "Messages";
        public static final String COLUMN_NAME_1 = "key";
        public static final String COLUMN_NAME_2 = "value";
    }
}
