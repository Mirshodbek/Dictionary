package uz.mirshodbek.dictionary_eng_ru_uz.menu;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uz.mirshodbek.dictionary_eng_ru_uz.Word;
import uz.mirshodbek.dictionary_eng_ru_uz.WordAdapter;
import uz.mirshodbek.dictionary_eng_ru_uz.DatabaseHelper;
import uz.mirshodbek.dictionary_eng_ru_uz.LoadDatabaseAsync;
import uz.mirshodbek.dictionary_eng_ru_uz.R;

public class HistoryOfWords extends AppCompatActivity {
    static DatabaseHelper myDbHelper;
    static boolean databaseOpened = false;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    WordAdapter wordAdapter;
    LinearLayout linearLayoutHistory;

    ArrayList<Word> wordList;

    Cursor brainCursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_of_words);

        Toolbar toolbar = findViewById(R.id.historyToolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.arrow_back);

        myDbHelper = new DatabaseHelper(this);
        if (myDbHelper.checkDatabase()) {
            openDatabase();
        } else {
            LoadDatabaseAsync task = new LoadDatabaseAsync(HistoryOfWords.this);
            task.execute();
        }
        recyclerView = findViewById(R.id.recycler_view_history);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        wordAdapter = new WordAdapter(this);
        recyclerView.setAdapter(wordAdapter);

        fetch();

    }

    private void fetch() {
        wordList = new ArrayList<>();

        Word b;
        if (databaseOpened) {
            brainCursor = myDbHelper.getHistory();
            if (brainCursor.moveToFirst()) {
                do {
                    b = new Word(brainCursor.getString(brainCursor.getColumnIndex("word")),
                            brainCursor.getString(brainCursor.getColumnIndex("engru")));
                    wordList.add(b);
                } while (brainCursor.moveToNext());
            }

            wordAdapter.setData(wordList);
            wordAdapter.notifyDataSetChanged();
        }

    }

    public static void openDatabase() {
        try {
            myDbHelper.openDatabase();
            databaseOpened = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                clear();
                showAlertDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clear() {
        myDbHelper = new DatabaseHelper(HistoryOfWords.this);
        try {
            myDbHelper.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog() {
        linearLayoutHistory = findViewById(R.id.linearLayoutHistory);
        AlertDialog.Builder builder = new AlertDialog.Builder(HistoryOfWords.this,
                R.style.MyDialogTheme);
        builder.setTitle(R.string.delete);

        String positiveText = getString(R.string.yes);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myDbHelper.deleteHistory();
                linearLayoutHistory.setVisibility(View.GONE);
            }
        });

        String negativeText = getString(R.string.no);
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
