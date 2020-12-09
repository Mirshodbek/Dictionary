package uz.mirshodbek.dictionary_eng_ru_uz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uz.mirshodbek.dictionary_eng_ru_uz.fragments.FragmentFirst;
import uz.mirshodbek.dictionary_eng_ru_uz.fragments.FragmentSecond;

public class WordMeaning extends AppCompatActivity {
    private ViewPager viewPager;
    private Toolbar toolbar;
    private CheckBox check;

    String eng_word;
    DatabaseHelper myDbHelper;
    Cursor meaning = null;

    public String uzbek;
    public String russian;
    public String headWord;
    public String readWord;
    private Boolean bookmarked;

    boolean startedFromShare = false;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        eng_word = bundle.getString("eng_word");

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                startedFromShare = true;

                if (sharedText != null) {
                    Pattern p = Pattern.compile("[A-Za-z ]{1,25}");
                    Matcher m = p.matcher(sharedText);

                    if (m.matches()) {
                        eng_word = sharedText;
                    } else {
                        eng_word = "Not Available";
                    }
                }
            }
        }

        myDbHelper = new DatabaseHelper(this);
        try {
            myDbHelper.openDatabase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        meaning = myDbHelper.getMeaning(eng_word);

        if (meaning.moveToFirst()) {
            headWord = meaning.getString(meaning.getColumnIndex("eng_word"));
            readWord = meaning.getString(meaning.getColumnIndex("eng_read"));
            russian = meaning.getString(meaning.getColumnIndex("engru"));
            uzbek = meaning.getString(meaning.getColumnIndex("enguz"));
            bookmarked = meaning.getInt(meaning.getColumnIndex("bookmarked")) == 1;

            myDbHelper.insertHistory(eng_word);
        } else {
            eng_word = "Not Available";
        }

        toolbar = findViewById(R.id.wordMeaningToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back);

        viewPager = (ViewPager) findViewById(R.id.tab_viewpager);

        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentFirst(), "Руский");
        adapter.addFrag(new FragmentSecond(), "Узбекиский");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_word_meaning, menu);
        storedWord(menu);
        return true;
    }

    public void storedWord(Menu menu) {
        check = (CheckBox) menu.findItem(R.id.stored_icon).getActionView();

        if (bookmarked) {
            check.setButtonDrawable(R.drawable.stored_icon_red);
            check.setChecked(true);
        } else {
            check.setButtonDrawable(R.drawable.stored_icon);
            check.setChecked(false);
        }

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    myDbHelper.setBookmarked(eng_word, true);
                    check.setButtonDrawable(R.drawable.stored_icon_red);
                } else {
                    myDbHelper.setBookmarked(eng_word, false);
                    check.setButtonDrawable(R.drawable.stored_icon);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.volume:
                volume();
                break;
            case R.id.stored_icon:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void volume() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result ==
                            TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This language is not supported");
                    } else {
                        textToSpeech.setSpeechRate(0.8f);
                        textToSpeech.speak(eng_word, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Log.e("error", "Initialization Failed");
                }
            }
        });
    }
}