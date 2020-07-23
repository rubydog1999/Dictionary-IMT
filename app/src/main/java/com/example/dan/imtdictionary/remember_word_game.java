package com.example.dan.imtdictionary;

import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import static com.example.dan.imtdictionary.MainActivity.database;

public class remember_word_game extends AppCompatActivity {
    Button btnAns1, btnAns2,btnAns3, btnStart;
    ImageButton btnMute;
    TextView txtScore, txtHighScore,  txtQuestion;
    Switch swLanguage;
    Random random;
    ProgressBar pbTime;
    CountDownTimer countDownTimer;
    ArrayList<String> enArray;
    ArrayAdapter<String> enArrayAdapter;
    ArrayList<String> viArray;
    ArrayAdapter<String> viArrayAdapter;
    ArrayList<String> frArray;
    ArrayAdapter<String> frArrayAdapter;
    MediaPlayer correct, wrong, theme, theme2;
    private int progressStatus, counter;
    int ans,score, highScore;
    public String DATABASE_NAME="wordsList.sqlite";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_word_game);
        addControl();
        addEvents();
        prepareSounds();
        btnAns1.setClickable(false);
        btnAns2.setClickable(false);
        btnAns3.setClickable(false);
        theme.start();
    }

    @Override
    protected void onPause() {
        if(theme.isPlaying()) {theme.pause();}
        else if(theme2.isPlaying()){theme2.pause();}
        super.onPause();
    }


    private void prepareSounds() {
        try
        {
            AssetFileDescriptor afd = getAssets().openFd("correct_sound.mp3");
            correct = new MediaPlayer();
            correct.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            correct.prepare();
            afd = getAssets().openFd("wrong_sound.mp3");
            wrong = new MediaPlayer();
            wrong.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            wrong.prepare();
            afd = getAssets().openFd("theme_song.mp3");
            theme = new MediaPlayer();
            theme.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            theme.prepare();
            afd = getAssets().openFd("theme2_song.mp3");
            theme2 = new MediaPlayer();
            theme2.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            theme2.prepare();
            theme.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    theme2.start();
                }
            });
            theme2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    theme.start();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Failed to prepare sounds", Toast.LENGTH_SHORT).show();
        }
    }


    private void addEvents() {
        txtScore.setText(getString(R.string.txtScore)  + "        " + score);
        txtHighScore.setText(getString(R.string.txtHighScore)+ "        " + highScore);
        btnAns1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(0);
            }
        });
        btnAns2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {choose(1);
            }
        });
        btnAns3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(2);
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
                btnStart.setClickable(false);
                swLanguage.setClickable(false);
                btnAns1.setClickable(true);
                btnAns2.setClickable(true);
                btnAns3.setClickable(true);
            }
        });
        btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(theme.isPlaying() || theme2.isPlaying())
                {
                    btnMute.setImageResource(R.drawable.ic_lock_silent_mode);
                    if(theme.isPlaying()) { theme.pause(); }
                    else{ theme2.pause();}
                }
                else
                {
                    btnMute.setImageResource(R.drawable.ic_lock_silent_mode_off);
                    if( !theme.isPlaying() && theme.getCurrentPosition() > 1) {
                        theme.start();
                    }
                    else{theme2.start();}
                }
            }
        });
    }

    private void choose(int i) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(250); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(2);
        if (ans == 0) { btnAns1.startAnimation(anim); }
        else if(ans == 1) { btnAns2.startAnimation(anim); }
        else { btnAns3.startAnimation(anim); }
        countDownTimer.cancel();
        if(i == ans)
        {
            correct.start();
            score++;
            if(score>= highScore)
            {
                highScore = score;
            }
            txtHighScore.setText(getString(R.string.txtHighScore)+ "        " + highScore);
            txtScore.setText(getString(R.string.txtScore)  + "        " + score);
            play();
        }
        else
        {
            wrong.start();
            Toast.makeText(this, "Game over", Toast.LENGTH_SHORT).show();
            score = 0;
            txtScore.setText(getString(R.string.txtScore)  + "        " + score);
            btnStart.setClickable(true);
            swLanguage.setClickable(true);
            btnAns1.setClickable(false);
            btnAns2.setClickable(false);
            btnAns3.setClickable(false);
        }
    }

    private void addControl() {
        btnAns1 = findViewById(R.id.btnAns1);
        btnAns2 = findViewById(R.id.btnAns2);
        btnAns3 = findViewById(R.id.btnAns3);
        btnStart = findViewById(R.id.btnStart);
        btnMute = findViewById(R.id.btnMute);
        txtScore = findViewById(R.id.txtScore);
        txtHighScore = findViewById(R.id.txtHighScore);
        txtQuestion = findViewById(R.id.txtQuestion);
        pbTime = findViewById(R.id.pbTime);
        swLanguage = findViewById(R.id.swLanguage);
        score = 0;
        highScore = 0;
        random = new Random();
        enArray = new ArrayList<>();
        enArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,enArray);
        viArray = new ArrayList<>();
        viArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,viArray);
        frArray = new ArrayList<>();
        frArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,frArray);
        createWordsList();
    }

    private void play()
    {
        int key = random.nextInt(enArray.size());
        int false1 = random.nextInt(enArray.size());
        int false2 = random.nextInt(enArray.size());
        double chance = random.nextDouble();
        if(swLanguage.isChecked()) { txtQuestion.setText(enArray.get(key)); }
        else{txtQuestion.setText(frArray.get(key)); }
        progressStatus = 100;
        counter = 5;
        pbTime.setProgress(progressStatus);
        countDownTimer = new CountDownTimer(5000, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressStatus--;
                pbTime.setProgress(progressStatus);
            }

            @Override
            public void onFinish() {
                pbTime.setProgress(0);
                choose(3);
            }};
        countDownTimer.start();
        if(chance < 0.33)
        {
            btnAns1.setText(viArray.get(key));
            ans = 0;
            generateQuestion(btnAns2, btnAns3, false1, false2);
        }
        else if( chance < 0.67)
        {
            btnAns2.setText(viArray.get(key));
            ans = 1;
            generateQuestion(btnAns1, btnAns3, false1, false2);
        }
        else
        {
            btnAns3.setText(viArray.get(key));
            ans = 2;
            generateQuestion(btnAns1, btnAns2, false1, false2);
        }
    }

    private void generateQuestion(Button b1, Button b2, int false1, int false2)
    {
        double chance = random.nextDouble();
        if(chance> 0.5)
        {
            b1.setText(viArray.get(false1));
            b2.setText(viArray.get(false2));
        }
        else
        {
            b1.setText(viArray.get(false2));
            b2.setText(viArray.get(false1));
        }
    }

    private void createWordsList()
    {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.query("Sheet1",null,null,null,null,null,null);
        enArray.clear();
        viArray.clear();
        frArray.clear();
        while(cursor.moveToNext()) {
            enArray.add(cursor.getString(0));
            viArray.add(cursor.getString(1));
            frArray.add(cursor.getString(2));
        }
        cursor.close();
        enArrayAdapter.notifyDataSetChanged();
        viArrayAdapter.notifyDataSetChanged();
        frArrayAdapter.notifyDataSetChanged();
    }
}
