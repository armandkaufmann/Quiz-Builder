package com.example.quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class Activity_3 extends AppCompatActivity {
    //data from bundles
    Level level;
    Long DIFFICULTY_TIME;

    int numQuestions;
    int numAnsweredCorrect;

    ArrayList<Integer> streakHistory;
    ArrayList<Long> timePerQuestionHistory;

    //variables needed for UI logic
    long timePerQuestion;
    float percentageCorrect;
    int highestStreak;

    //views
    TextView textViewResultsRating;
    TextView textViewScorePercentage;
    TextView textViewTimePerQuestionNum;
    TextView textViewHighestStreakNum;
    TextView textViewDifficultyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        //data from bundle
        level = (Level) getIntent().getSerializableExtra("Level");
        DIFFICULTY_TIME = (long) getIntent().getLongExtra("DIFFICULTY", 90000);

        numQuestions = (int) getIntent().getIntExtra("numQuestions", 1);
        numAnsweredCorrect = (int) getIntent().getIntExtra("numAnsweredCorrect", 1);

        streakHistory = (ArrayList<Integer>) getIntent().getSerializableExtra("streakHistory");
        timePerQuestionHistory = (ArrayList<Long>) getIntent().getSerializableExtra("timePerQuestionHistory");

        //connecting views
        textViewResultsRating = findViewById(R.id.textViewResultsRating);
        textViewScorePercentage = findViewById(R.id.textViewScorePercentage);
        textViewTimePerQuestionNum = findViewById(R.id.textViewTimePerQuestionNum);
        textViewHighestStreakNum = findViewById(R.id.textViewHighestStreakNum);
        textViewDifficultyText = findViewById(R.id.textViewDifficultyText);

        //setting text based on bundle data
        getTimePerQuestion();
        getPercentageCorrect();
        getHighestStreak();

        //setting the text of the data to the view
        getRatingToOutput();
        getDifficultySetText();

        setScore();
        setHighestStreak();
        setTimePerQuestion();

    }//end onCreate method

    private void getTimePerQuestion(){
        long sum = 0;
        for (int i = 0; i < timePerQuestionHistory.size(); i++){
            sum += timePerQuestionHistory.get(i);
        }
        timePerQuestion = sum / timePerQuestionHistory.size();
    }//end getTimePerQuestion method

    private void getDifficultySetText(){
        if (level == Level.EASY){
            textViewDifficultyText.setText(getResources().getString(R.string.easy));
            textViewDifficultyText.setTextColor(Color.parseColor("#64f575")); //green
        }else if (level == Level.MEDIUM){
            textViewDifficultyText.setText(getResources().getString(R.string.Medium));
            textViewDifficultyText.setTextColor(Color.parseColor("#f18e00")); //orange
        }else{
            textViewDifficultyText.setText(getResources().getString(R.string.Hard));
            textViewDifficultyText.setTextColor(Color.parseColor("#f56464")); //red
        }
    }

    private void getPercentageCorrect(){
        percentageCorrect = 100 * numAnsweredCorrect / numQuestions;
    }//end getPercentageCorrect method

    private void getHighestStreak(){
        int max = 0;
        for (int i = 0; i < streakHistory.size(); i++){
            if (streakHistory.get(i) > max){
                max = streakHistory.get(i);
            }
        }
        highestStreak = max;
    }//end getHighestStreak method

    private void getRatingToOutput(){
        if (percentageCorrect <= 50){
            textViewResultsRating.setText(getResources().getString(R.string.ratingBad));
        }else if (percentageCorrect <= 90){
            textViewResultsRating.setText(getResources().getString(R.string.ratingGood));
        }else{
            textViewResultsRating.setText(getResources().getString(R.string.ratingPerfect));
        }
    }//end getRatingToOutput method

    private void setScore(){
        String text = String.format("%.2f", percentageCorrect);
        String textFinal = text.concat("%");
        textViewScorePercentage.setText(textFinal);
    }

    private void setHighestStreak(){
        textViewHighestStreakNum.setText(Integer.toString(highestStreak));
    }

    private void setTimePerQuestion(){
        int minutes = (int) (timePerQuestion / 1000) / 60; //if adding minutes
        int seconds = (int) (timePerQuestion / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes,seconds);

        textViewTimePerQuestionNum.setText(timeLeftFormatted);
    }
}