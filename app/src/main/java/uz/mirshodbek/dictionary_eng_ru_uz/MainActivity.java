package uz.mirshodbek.dictionary_eng_ru_uz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uz.mirshodbek.dictionary_eng_ru_uz.menu.HistoryOfWords;
import uz.mirshodbek.dictionary_eng_ru_uz.menu.Bookmarks;

public class MainActivity extends AppCompatActivity {
    SearchView search;
    static DatabaseHelper myDbHelper;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    WordAdapter wordAdapter;

    Cursor wordCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = findViewById(R.id.search_view);
        search.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setIconified(false);
            }
        });

        myDbHelper = new DatabaseHelper(this);
        if (myDbHelper.checkDatabase()) {
            openDatabase();
        } else {
            LoadDatabaseAsync task = new LoadDatabaseAsync(MainActivity.this);
            task.execute();
        }

        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        wordAdapter = new WordAdapter(this);
        recyclerView.setAdapter(wordAdapter);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String text = search.getQuery().toString();
                Pattern p = Pattern.compile("[A-Za-z \\-.]");
                Matcher m = p.matcher(text);

                Cursor c = myDbHelper.getMeaning(text);
                if (m.matches()) {

                    search.clearFocus();
                    search.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this, WordMeaning.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("eng_word", text);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String input) {
                fetch_words(input);
                return false;
            }
        });
    }

    private void fetch_words(String input) {
        ArrayList<Word> wordList = new ArrayList<>();
        Pattern p = Pattern.compile("[A-Za-z \\-.]+[A-Za-z \\-.]");
        Matcher m = p.matcher(input);

        if (m.matches()) {
            Word b;
            wordCursor = myDbHelper.getWordsLike(input);

            if (wordCursor.moveToFirst()) {
                do {
                    b = new Word(
                        wordCursor.getString(wordCursor.getColumnIndex("eng_word")),
                        wordCursor.getString(wordCursor.getColumnIndex("engru"))
                    );
                    wordList.add(b);
                } while (wordCursor.moveToNext());
            }
        }

        wordAdapter.setData(wordList);
        wordAdapter.notifyDataSetChanged();
    }

    protected static void openDatabase() {
        try {
            myDbHelper.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stored_words:
                Intent storedWords = new Intent(this, Bookmarks.class);
                startActivity(storedWords);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                break;
            case R.id.history_words:
                Intent historyOfWords = new Intent(this, HistoryOfWords.class);
                startActivity(historyOfWords);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}