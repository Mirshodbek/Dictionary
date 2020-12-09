package uz.mirshodbek.dictionary_eng_ru_uz;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import uz.mirshodbek.dictionary_eng_ru_uz.menu.HistoryOfWords;
import uz.mirshodbek.dictionary_eng_ru_uz.menu.Bookmarks;

public class LoadDatabaseAsync extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private DatabaseHelper myDbHelper;

    public LoadDatabaseAsync(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        myDbHelper = new DatabaseHelper(context);
        try {
            myDbHelper.createDatabase();
        } catch (IOException e) {
            throw new Error("Database was not created");
        }
        myDbHelper.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        MainActivity.openDatabase();
        HistoryOfWords.openDatabase();
        Bookmarks.openDatabase();
    }
}
