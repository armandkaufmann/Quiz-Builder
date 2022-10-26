package com.example.quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Activity_3 extends AppCompatActivity {
    //data from bundles
    Level level;
    Long DIFFICULTY_TIME;

    int numQuestions; //number of total questions
    int numAnsweredCorrect; //number of questions answered correctly

    ArrayList<Integer> streakHistory; //history of the user's streak
    ArrayList<Long> timePerQuestionHistory; //time spent per question

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

    Button buttonRetry;
    Button buttonChangeDiff;

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

        buttonRetry = findViewById(R.id.buttonRetry);
        buttonRetry.setOnClickListener(buttonRetryListener);
        buttonChangeDiff = findViewById(R.id.buttonChangeDiff);
        buttonChangeDiff.setOnClickListener(buttonChangeDiffListener);

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

    private void getTimePerQuestion(){ //calculating the average time spent per question
        long sum = 0;
        for (int i = 0; i < timePerQuestionHistory.size(); i++){
            sum += timePerQuestionHistory.get(i);
        }
        timePerQuestion = sum / timePerQuestionHistory.size();
    }//end getTimePerQuestion method

    //methods ======================================================================================
    private void getDifficultySetText(){ //getting and displaying the difficulty the user completed the quiz on
        if (level == Level.EASY){
            textViewDifficultyText.setText(getResources().getString(R.string.easy));
            textViewDifficultyText.setTextColor(Color.parseColor("#3CFA52")); //green
        }else if (level == Level.MEDIUM){
            textViewDifficultyText.setText(getResources().getString(R.string.Medium));
            textViewDifficultyText.setTextColor(Color.parseColor("#f18e00")); //orange
        }else{
            textViewDifficultyText.setText(getResources().getString(R.string.Hard));
            textViewDifficultyText.setTextColor(Color.parseColor("#f56464")); //red
        }
    }//end getDifficultySetText method

    private void getPercentageCorrect(){ //calculating the percentage of questions answered correctly
        percentageCorrect = 100 * numAnsweredCorrect / numQuestions;
    }//end getPercentageCorrect method

    private void getHighestStreak(){ //getting the highest streak the user had
        int max = 0;
        for (int i = 0; i < streakHistory.size(); i++){
            if (streakHistory.get(i) > max){
                max = streakHistory.get(i);
            }
        }
        highestStreak = max;
    }//end getHighestStreak method

    private void getRatingToOutput(){ //gets the rating of the user based on percentage of questions correct
        if (percentageCorrect <= 50){ //failed
            textViewResultsRating.setText(getResources().getString(R.string.ratingBad));
            textViewResultsRating.setTextColor(Color.parseColor("#f56464"));
            textViewScorePercentage.setTextColor(Color.parseColor("#f56464"));
        }else if (percentageCorrect <= 90){ //passed
            textViewResultsRating.setText(getResources().getString(R.string.ratingGood));
            textViewResultsRating.setTextColor(Color.parseColor("#3CFA52"));
        }else{ //perfect to almost perfect
            textViewResultsRating.setText(getResources().getString(R.string.ratingPerfect));
            textViewResultsRating.setTextColor(Color.parseColor("#3CFA52"));
        }
    }//end getRatingToOutput method

    private void setScore(){ //displaying the percentage of correct questions
        String text = String.format("%.2f", percentageCorrect);
        String textFinal = text.concat("%");
        textViewScorePercentage.setText(textFinal);
    }//end setScore

    private void setHighestStreak(){ //setting the highest streak the user had
        textViewHighestStreakNum.setText(Integer.toString(highestStreak));
    }//end setHighestStreak

    private void setTimePerQuestion(){ //setting the time per question
        int minutes = (int) (timePerQuestion / 1000) / 60; //if adding minutes
        int seconds = (int) (timePerQuestion / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes,seconds);

        textViewTimePerQuestionNum.setText(timeLeftFormatted);
    }//end setTimePerQuestion

    //listeners ====================================================================================
    private View.OnClickListener buttonChangeDiffListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Activity_3.this, MainActivity.class);
            startActivity(i);
        }
    }; //end buttonNextQuestionListener

    private View.OnClickListener buttonRetryListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Activity_3.this, Activity_2.class);
            i.putExtra("Level", level);
            i.putExtra("DIFFICULTY", DIFFICULTY_TIME);
            startActivity(i);
        }
    };//end
}