package com.example.quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button buttonEasy;
    Button buttonMedium;
    Button buttonHard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonEasy = findViewById(R.id.buttonEasy);
        buttonEasy.setOnClickListener(buttonEasyListener);
//        buttonEasy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, activity_2.class);
//                startActivity(i); // brings up the second activity
//            }//end onClick
//        });//end listener

        buttonMedium = findViewById(R.id.buttonMedium);
        buttonMedium.setOnClickListener(buttonMediumListener);

        buttonHard = findViewById(R.id.buttonHard);
        buttonHard.setOnClickListener(buttonHardListener);
    }

    private View.OnClickListener buttonEasyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            long DIFFICULTY = 90000;
            Level level = Level.EASY;

            Intent i = new Intent(MainActivity.this, Activity_2.class);
            i.putExtra("Level", level);
            i.putExtra("DIFFICULTY", DIFFICULTY);

            startActivity(i);
        }
    };

    private View.OnClickListener buttonMediumListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            long DIFFICULTY = 60000;
            Level level = Level.MEDIUM;

            Intent i = new Intent(MainActivity.this, Activity_2.class);
            i.putExtra("Level", level);
            i.putExtra("DIFFICULTY", DIFFICULTY);

            startActivity(i);
        }
    };

    private View.OnClickListener buttonHardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            long DIFFICULTY = 30000;
            Level level = Level.HARD;

            Intent i = new Intent(MainActivity.this, Activity_2.class);
            i.putExtra("Level", level);
            i.putExtra("DIFFICULTY", DIFFICULTY);

            startActivity(i);
        }
    };


}