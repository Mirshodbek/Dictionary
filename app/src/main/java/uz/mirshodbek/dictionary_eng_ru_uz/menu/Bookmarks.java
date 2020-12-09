package uz.mirshodbek.dictionary_eng_ru_uz.menu;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uz.mirshodbek.dictionary_eng_ru_uz.Word;
import uz.mirshodbek.dictionary_eng_ru_uz.WordAdapter;
import uz.mirshodbek.dictionary_eng_ru_uz.DatabaseHelper;
import uz.mirshodbek.dictionary_eng_ru_uz.LoadDatabaseAsync;
import uz.mirshodbek.dictionary_eng_ru_uz.R;

public class Bookmarks extends AppCompatActivity {
    ItemTouchHelper itemTouchHelper;
    static DatabaseHelper myDbHelper;
    static boolean databaseOpened = false;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    WordAdapter wordAdapter;

    ArrayList<Word> wordList;

    Cursor wordCursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stored_words);

        Toolbar toolbar = findViewById(R.id.storedToolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.arrow_back);

        // set up DB connection
        myDbHelper = new DatabaseHelper(this);
        if (myDbHelper.checkDatabase()) {
            openDatabase();
        } else {
            LoadDatabaseAsync task = new LoadDatabaseAsync(Bookmarks.this);
            task.execute();
        }

        // set up recycleview
        recyclerView = findViewById(R.id.recycler_view_stored);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        wordAdapter = new WordAdapter(this);
        recyclerView.setAdapter(wordAdapter);

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(1000,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                direction = viewHolder.getAdapterPosition();
                wordAdapter.removeWord(direction);
                wordAdapter.notifyDataSetChanged();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        fetch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetch();
    }

    public static void openDatabase() {
        try {
            myDbHelper.openDatabase();
            databaseOpened = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetch() {
        wordList = new ArrayList<>();

        Word b;
        if (databaseOpened) {
            wordCursor = myDbHelper.getBookmarks();

            if (wordCursor.moveToFirst()) {
                do {
                    b = new Word(
                            wordCursor.getString(wordCursor.getColumnIndex("eng_word")),
                            wordCursor.getString(wordCursor.getColumnIndex("engru"))
                    );
                    wordList.add(b);
                } while (wordCursor.moveToNext());
            }

            wordAdapter.setData(wordList);
            wordAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
