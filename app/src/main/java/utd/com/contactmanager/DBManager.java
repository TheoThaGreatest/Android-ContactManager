package utd.com.contactmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.sql.Types.NULL;

public class DBManager extends SQLiteOpenHelper {

    private static final String dbName = "Contacts.db";
    private static final String DB_Table = "Contacts_Table";
    //columns
    private static final String id = "ID";
    private static final String NAME = "NAME";
    //
    private static final String FIRST_NAME = "FIRST_NAME";
    private static final String LAST_NAME = "LAST_NAME";
    private static final String PHONE_NUMBER = "PHONE";
    private static final String ADDRESS = "ADDRESS";

    private static final String create_table = "CREATE TABLE " + DB_Table + " (" +
            id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIRST_NAME + " char(25), " +
            LAST_NAME + " char(25), " +
            PHONE_NUMBER + " char(15), " +
            ADDRESS + " char(100) " + ");";
    //private static final String create_table = "create table DB_Table2 (" + "id integer primary key autoincrement," + "NAME," + "TEXT" + ");";


    public DBManager(Context context) {

        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS '" +DB_Table + "'");
        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" +DB_Table + "'");
    }

    public boolean insertData(String fname, String lname, String pnum, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIRST_NAME, fname);
        contentValues.put(LAST_NAME,lname);
        contentValues.put(PHONE_NUMBER, pnum);
        contentValues.put(ADDRESS, address);
        long result = db.insert(DB_Table, null, contentValues);
        return result != -1; // if result = -1 data does not insert.
    }


    public Cursor viewData()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "Select * from " + DB_Table;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor getRow(int nameid){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + DB_Table + " WHERE "
                + id + " = " + nameid;

        Cursor c = db.rawQuery(selectQuery, null);

        return c;

    }

    public void updateTable(String fname, String lname, String pnum, String address, int nameID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(FIRST_NAME, fname);
        cv.put(LAST_NAME, lname);
        cv.put(PHONE_NUMBER, pnum);
        cv.put(ADDRESS, address);

        db.update(DB_Table, cv, " id = " + nameID, null);
        db.close();
    }

    public void deleteRow(String fname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DB_Table + " WHERE " + FIRST_NAME + "= '" + fname + "'");
        db.close();
    }

    public void clearTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM '" + DB_Table + "'");
    }

    public Cursor reArrange()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT  * FROM " + DB_Table + " ORDER BY " + FIRST_NAME + " ASC";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }
}
