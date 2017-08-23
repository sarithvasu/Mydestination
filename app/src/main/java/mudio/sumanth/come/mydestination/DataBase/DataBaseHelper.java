package mudio.sumanth.come.mydestination.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;


import static mudio.sumanth.come.mydestination.Common.TabelName.CREATE_TABLE_TRAVEL;
import static mudio.sumanth.come.mydestination.Common.TabelName.DATA_BASE_NAME;


/**
 * Created by sarith.vasu on 03-02-2017.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private Context mContext;

    private static int DB_VERSION = 1;
    private static String DB_PATH = "";

    public DataBaseHelper(Context context){

        super(context, DATA_BASE_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_TRAVEL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public SQLiteDatabase OpenDataBase() {
        SQLiteDatabase db = null;

        try {
            String myPath = DB_PATH + DATA_BASE_NAME;

                db = SQLiteDatabase.openOrCreateDatabase(myPath, null, null);
                db.execSQL("PRAGMA foreign_keys = ON;");




        } catch (SQLiteException e) {
            db = null;
            e.printStackTrace();
        }

        return db;
    }
}
