package uz.mirshodbek.dictionary_eng_ru_uz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String DB_PATH = null;
    private static String DB_NAME = "eng_dictionar.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.d("Path 1", DB_PATH);
    }

    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();
        if (!dbExist) {
            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    public boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {

        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDatabase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
        Log.i("copyDatabase", "Database copied");
    }

    public void openDatabase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            this.getReadableDatabase();
            myContext.deleteDatabase(DB_NAME);
            copyDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Cursor getMeaning(String text) {
        Cursor c = myDataBase.rawQuery("SELECT * FROM englishword WHERE eng_word == LOWER('" +
                text + "')", null);
        return c;
    }

    public Cursor getWords() {
        Cursor c = myDataBase.rawQuery("SELECT * FROM englishword", null);
        return c;
    }

    public Cursor getWordsLike(String text) {
        Cursor c = myDataBase.rawQuery("SELECT _ideng,eng_word,engru FROM englishword WHERE " +
                "eng_word LIKE '" + text + "%' LIMIT 400", null);
        return c;
    }

    public void insertHistory(String text) {
        myDataBase.execSQL("INSERT INTO history(word) VALUES(LOWER('" + text + "'))");
    }

    public Cursor getHistory() {
        Cursor c = myDataBase.rawQuery("SELECT DISTINCT word,engru FROM history h JOIN " +
                "englishword w ON h.word==w.eng_word ORDER BY h._ideng DESC", null);
        return c;
    }

    public void deleteHistory() {
        myDataBase.execSQL("DELETE FROM history");
    }

    public Cursor getBookmarks() {
        Cursor c = myDataBase.rawQuery("select * from englishword where bookmarked = 1",
                null);
        return c;
    }

    public void setBookmarked(String engWord, Boolean bookmarked) {
        ContentValues values = new ContentValues();
        values.put("bookmarked", bookmarked ? 1 : 0);

        myDataBase.update("englishword", values, "eng_word = ?",
                new String[]{engWord});
    }

    public void deleteStored(String text) {
        myDataBase.execSQL("DELETE FROM storedwords WHERE savedword==LOWER('" + text + "')");
    }
}
