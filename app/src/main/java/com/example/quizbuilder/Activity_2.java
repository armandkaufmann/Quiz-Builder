package com.example.quizbuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Activity_2 extends AppCompatActivity {
    //data from bundle
    Level level;
    long DIFFICULTY_TIME;

    //setting up the data structures for the text file =============================================
    ArrayList<String> defList = new ArrayList<String>(); //to hold the definitions
    ArrayList<String> termList = new ArrayList<String>(); //to hold the terms
    HashMap<String, String> hash = new HashMap<String, String>(); //to hold the key value pairs of terms and definitions

    //variables for main quiz logic ================================================================
    String answer;
    boolean questionIsAnswered = false;
    boolean questionIsCorrect = false;

    //variables for UI logic =======================================================================
    int numQuestions;
    int currQuestion;
    int streak;

    //buttons, views ===============================================================================
    //top bar info
    CardView cardViewBack; //to go back to the main page
    TextView textViewStreakNum; //show the streak (i.e. how many questions were correct in a row)
    TextView textViewCurrQuestionNum; //shows the number of the current question
    TextView textViewTotalNumQuestions; //shows the total number of questions

    //quiz question
    TextView textViewQuestion; //displays the question/definition
    //selectable answers
    CardView cardViewAnswer1;
    TextView textViewAnswer1;
    CardView cardViewAnswer2;
    TextView textViewAnswer2;
    CardView cardViewAnswer3;
    TextView textViewAnswer3;
    CardView cardViewAnswer4;
    TextView textViewAnswer4;

    //next question button
    Button buttonNextQuestion; //to progress to the next question when quiz answer is completed

    //progress bar
    ProgressBar progressBarTimer; //to visually show the time left
    TextView textViewProgressBarNum; //to display time left in numbers




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        //data from bundle
        DIFFICULTY_TIME = (long) getIntent().getLongExtra("DIFFICULTY", 90000);
        level = (Level) getIntent().getSerializableExtra("Level");

        //setting up views, buttons
            //top bar
        cardViewBack = findViewById(R.id.cardViewBack);
        cardViewBack.setOnClickListener(cardViewBackListener); //listener

        textViewStreakNum = findViewById(R.id.textViewStreakNum);
        textViewCurrQuestionNum = findViewById(R.id.textViewCurrQuestionNum);
        textViewTotalNumQuestions = findViewById(R.id.textViewTotalNumQuestions);

            //quiz question
        textViewQuestion = findViewById(R.id.textViewQuestion);
            //selectable answers
        cardViewAnswer1 = findViewById(R.id.cardViewAnswer1);
        cardViewAnswer1.setOnClickListener(cardViewAnswer1Listener); //listener
        textViewAnswer1 = findViewById(R.id.textViewAnswer1);

        cardViewAnswer2 = findViewById(R.id.cardViewAnswer2);
        cardViewAnswer2.setOnClickListener(cardViewAnswer2Listener); //listener
        textViewAnswer2 = findViewById(R.id.textViewAnswer2);

        cardViewAnswer3 = findViewById(R.id.cardViewAnswer3);
        cardViewAnswer3.setOnClickListener(cardViewAnswer3Listener); //listener
        textViewAnswer3 = findViewById(R.id.textViewAnswer3);

        cardViewAnswer4 = findViewById(R.id.cardViewAnswer4);
        cardViewAnswer4.setOnClickListener(cardViewAnswer4Listener); //listener
        textViewAnswer4 = findViewById(R.id.textViewAnswer4);

            //next question button
        buttonNextQuestion = findViewById(R.id.buttonNextQuestion);
        buttonNextQuestion.setOnClickListener(buttonNextQuestionListener); //listener

            //progress bar
        progressBarTimer = findViewById(R.id.progressBarTimer);
        textViewProgressBarNum = findViewById(R.id.textViewProgressBarNum);

        //file input and processing
        readFile(); //reading the file into memory and parsing
        createHashDefTerms(); //creating the hashmap for the definitions and terms
        Collections.shuffle(defList); //shuffling the definitions

        //things to do at the start, preparation of UI
        numQuestions = termList.size();
        currQuestion = 1;
        streak = 1;
        disableButtonNextQuestionClickable(); //disabling the next question button
        textViewTotalNumQuestions.setText(Integer.toString(numQuestions));
    }//end onCreate method


    //methods for app ==============================================================================
    private void readFile() {
        String str = null;
        BufferedReader br = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.mobile_apps_android_quiz_1); //declaring input stream
            br = new BufferedReader(new InputStreamReader(is)); //declaring and instantiating buffer and previously declared input stream

            while ((str = br.readLine()) != null) {
                String[] defTerm = str.split(":"); //splitting the string based on the delimiter character $
                this.defList.add(defTerm[0]); //adding the 1st element to the definitions
                this.termList.add(defTerm[1]); //adding the second element to the terms
            }

            is.close(); //closing input stream
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }//end readFile method

    private void createHashDefTerms(){
        for (int i = 0; i < defList.size(); i++){
            hash.put(defList.get(i), termList.get(i));
        }
    }//end createHashDefTerms method

    private void disableCardViewClickable(){
        cardViewAnswer1.setClickable(false);
        cardViewAnswer2.setClickable(false);
        cardViewAnswer3.setClickable(false);
        cardViewAnswer4.setClickable(false);
    }

    private void enableCardViewClickable(){
        cardViewAnswer1.setClickable(true);
        cardViewAnswer2.setClickable(true);
        cardViewAnswer3.setClickable(true);
        cardViewAnswer4.setClickable(true);
    }

    private void disableButtonNextQuestionClickable(){
        buttonNextQuestion.setClickable(false);
        buttonNextQuestion.setBackgroundColor(Color.parseColor("#95989c"));
    }

    private void enableBUttonNextQuestionClickable(){
        buttonNextQuestion.setClickable(true);
        buttonNextQuestion.setBackgroundColor(Color.parseColor("#4b74fa"));
    }

    //listeners ====================================================================================
    private View.OnClickListener cardViewBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    }; //end cardViewBackListener

    private View.OnClickListener cardViewAnswer1Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    }; //end cardViewAnswer1Listener

    private View.OnClickListener cardViewAnswer2Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    }; //end cardViewAnswer2Listener

    private View.OnClickListener cardViewAnswer3Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    }; //end cardViewAnswer3Listener

    private View.OnClickListener cardViewAnswer4Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    }; //end cardViewAnswer4Listener

    private View.OnClickListener buttonNextQuestionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    }; //end buttonNextQuestionListener
}