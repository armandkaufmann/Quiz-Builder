package com.example.quizbuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Activity_2 extends AppCompatActivity {
    //data from bundle
    Level level;
    long DIFFICULTY_TIME;

    //setting up the data structures for the text file =============================================
    ArrayList<String> defList = new ArrayList<String>(); //to hold the definitions/questions
    ArrayList<String> termList = new ArrayList<String>(); //to hold the terms/answers
    HashMap<String, String> hash = new HashMap<String, String>(); //to hold the key value pairs of terms and definitions

    //variables for main quiz logic ================================================================
    String currAnswer;
    String currQuestion;
    boolean questionIsAnswered = false;
    boolean questionIsCorrect = false;

    //variables for UI logic =======================================================================
    int numQuestions;
    int currQuestionNum = 1;
    int streak;
    int numAnsweredCorrect = 0;

    //variables for timer
    CountDownTimer timer;
    boolean timerRunning;
    long timerLeft;
    int PROGRESS_BAR_START = 100;
    float progress_bar_curr = PROGRESS_BAR_START;

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
        timerLeft = DIFFICULTY_TIME;

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
        setTotalQuestionsNum();
        updateTotalQuestionsNum();
        updateCurrQuestionNum();
        resetStreak();
        updateStreak();
        disableButtonNextQuestionClickable(); //disabling the next question button

        //starting the quiz
        startQuestion();
    }//end onCreate method


    //main methods for app =========================================================================
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

    private boolean loadNewQuestionAndAnswer(){ //gets new question and answer
        //returns false if there are no more definitions/questions to get
        //returns true of there are more definitions/questions to get
        if (defList.size() > 0){
            currQuestion = defList.get(0); //getting the current question from the list
            defList.remove(0); //removing the question from the list

            currAnswer = hash.get(currQuestion);
            updateQuestion();
            return true;
        }else{
            return false;
        }

    }//end loadNewQuestionAndAnswer method

    private void loadAllAnswers(){
        Collections.shuffle(termList);

        ArrayList<String> answers = new ArrayList<String>();
        answers.add(currAnswer);

        int counter = 0;
        while (answers.size() <= 3){ //while the list of the answers array is smaller than or equal to 4, 4 answers
            if (!answers.contains(termList.get(counter)) && !termList.get(counter).equals(currAnswer)){
                //if the term to add is currently not in the list, and the term is not the answer
                answers.add(termList.get(counter));
            }
            counter++;
        }
        Collections.shuffle(answers);
        updateAnswerChoices(answers);
    }

    private void startQuestion(){
        if (loadNewQuestionAndAnswer()){ //if we still have definitions/answers, loads the question as well
            loadAllAnswers(); //loading all the answers

            enableCardViewClickable(); //enabling the cards which hold the answers choices to be clickable
            disableButtonNextQuestionClickable(); //disabling the next question button
            resetCardViews();

            updateCurrQuestionNum();
            updateStreak();

            stopTimer();
            startTimer(); //starting the timer
        }
    }

    private void answerPressed(String stringAnswer){ //user clicked on one of the answers to answer the question
        disableCardViewClickable(); //disabling the cards from being clicked
        enableButtonNextQuestionClickable(); //enabling the next question button to be pressed

        if (timerRunning){
            pauseTimer();
        }

        if (stringAnswer != null){ //if the user pressed an answer before the timer ran out
            if (currAnswer.equals(stringAnswer)){ //user got the answer correct
                revealAnswerCorrectPressed();
                streak += 1;
                numAnsweredCorrect += 1;
            }else{ //user did not get the correct answer
                revealAnswerWrongPressed();
                resetStreak();
            }
        }else{ //if the user did not press an answer before the timer ran out
            revealAnswerWrongPressed();
            resetStreak();
        }

        currQuestionNum += 1;
        }

    private void revealAnswerCorrectPressed(){
        if (((TextView)cardViewAnswer1.getChildAt(0)).getText().toString().equals(currAnswer)){ //if the cardView1 was pressed and is correct
            cardViewAnswer1.setCardBackgroundColor(Color.parseColor("#64f575"));
        }else if (((TextView)cardViewAnswer2.getChildAt(0)).getText().toString().equals(currAnswer)){ //if the cardView2 was pressed and is correct
            cardViewAnswer2.setCardBackgroundColor(Color.parseColor("#64f575"));
        }else if (((TextView)cardViewAnswer3.getChildAt(0)).getText().toString().equals(currAnswer)){ //if the cardView3 was pressed and is correct
            cardViewAnswer3.setCardBackgroundColor(Color.parseColor("#64f575"));
        }else if (((TextView)cardViewAnswer4.getChildAt(0)).getText().toString().equals(currAnswer)){ //if the cardView4 was pressed and is correct
            cardViewAnswer4.setCardBackgroundColor(Color.parseColor("#64f575"));
        }
    }

    private void revealAnswerWrongPressed(){
        if (((TextView)cardViewAnswer1.getChildAt(0)).getText().toString().equals(currAnswer)){ //if the cardView1 was pressed and is correct
            cardViewAnswer1.setCardBackgroundColor(Color.parseColor("#64f575")); //set green
        }else{
            cardViewAnswer1.setCardBackgroundColor(Color.parseColor("#f56464")); //set red if wrong
        }

        if (((TextView)cardViewAnswer2.getChildAt(0)).getText().toString().equals(currAnswer)){ //if the cardView2 was pressed and is correct
            cardViewAnswer2.setCardBackgroundColor(Color.parseColor("#64f575"));
        }else{
            cardViewAnswer2.setCardBackgroundColor(Color.parseColor("#f56464"));
        }

        if (((TextView)cardViewAnswer3.getChildAt(0)).getText().toString().equals(currAnswer)){ //if the cardView3 was pressed and is correct
            cardViewAnswer3.setCardBackgroundColor(Color.parseColor("#64f575"));
        }else{
            cardViewAnswer3.setCardBackgroundColor(Color.parseColor("#f56464"));
        }

        if (((TextView)cardViewAnswer4.getChildAt(0)).getText().toString().equals(currAnswer)){ //if the cardView4 was pressed and is correct
            cardViewAnswer4.setCardBackgroundColor(Color.parseColor("#64f575"));
        }else{
            cardViewAnswer4.setCardBackgroundColor(Color.parseColor("#f56464"));
        }
    }



    //secondary methods for app ====================================================================
    private void resetCardViews(){
        cardViewAnswer1.setCardBackgroundColor(Color.parseColor("#ffffff"));
        cardViewAnswer2.setCardBackgroundColor(Color.parseColor("#ffffff"));
        cardViewAnswer3.setCardBackgroundColor(Color.parseColor("#ffffff"));
        cardViewAnswer4.setCardBackgroundColor(Color.parseColor("#ffffff"));
    }

    private void updateQuestion(){
        textViewQuestion.setText(currQuestion);
    }//end updateQuestion method

    private void updateAnswerChoices(ArrayList<String> answers){
        textViewAnswer1.setText(answers.get(0));
        textViewAnswer2.setText(answers.get(1));
        textViewAnswer3.setText(answers.get(2));
        textViewAnswer4.setText(answers.get(3));
    }//end updateAnswerChoices method

    private void disableCardViewClickable(){
        cardViewAnswer1.setClickable(false);
        cardViewAnswer2.setClickable(false);
        cardViewAnswer3.setClickable(false);
        cardViewAnswer4.setClickable(false);
    }//end disableCardViewClickable method

    private void enableCardViewClickable(){
        cardViewAnswer1.setClickable(true);
        cardViewAnswer2.setClickable(true);
        cardViewAnswer3.setClickable(true);
        cardViewAnswer4.setClickable(true);
    }//end enableCardViewClickable method

    private void disableButtonNextQuestionClickable(){
        buttonNextQuestion.setClickable(false);
        buttonNextQuestion.setBackgroundColor(Color.parseColor("#95989c"));
    }//end disableButtonNextQuestionClickable method

    private void enableButtonNextQuestionClickable(){
        buttonNextQuestion.setClickable(true);
        buttonNextQuestion.setBackgroundColor(Color.parseColor("#4b74fa"));
    }//end enableButtonNextQuestionClickable method

    private void updateStreak(){
        textViewStreakNum.setText(Integer.toString(streak));
    }//end updateStreak method

    private void resetStreak(){
        streak = 0;
    }//end resetStreak

    private void updateCurrQuestionNum(){
        textViewCurrQuestionNum.setText(Integer.toString(currQuestionNum));
    }//end updateCurrQuestionNum

    private void updateTotalQuestionsNum(){
        textViewTotalNumQuestions.setText(Integer.toString(numQuestions));
    }//end updateTotalQuestionsNum

    private void setTotalQuestionsNum(){
        numQuestions = termList.size();
    }//end setTotalQuestionsNum

    //timer methods ================================================================================
    private void startTimer(){
        timer = new CountDownTimer(timerLeft, 500) {
            @Override
            public void onTick(long l) {
                timerLeft = l;
                updateCountDownText();
                progress_bar_curr = 100 * l / DIFFICULTY_TIME;
                updateProgressBar();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                pauseTimer();
                progressBarTimer.setProgress(0);
                answerPressed(null);
            }
        }.start();

        timerRunning = true;
    }

    private void stopTimer(){
        if (timerRunning){
            timer.cancel();
        }
        timerLeft = DIFFICULTY_TIME;
        updateCountDownText();
        resetProgressBar();
        timerRunning = false;
    }

    private void pauseTimer(){
        if (timerRunning){
            timer.cancel();
        }
        timerRunning = false;
    }

    private void updateCountDownText(){
        int minutes = (int) (timerLeft / 1000) / 60; //if adding minutes
        int seconds = (int) (timerLeft / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes,seconds);

        textViewProgressBarNum.setText(timeLeftFormatted);
    }

    private void updateProgressBar(){
        progressBarTimer.setProgress((int) progress_bar_curr);
    }

    private void resetProgressBar(){
        progress_bar_curr = PROGRESS_BAR_START;
        updateProgressBar();
    }


    //listeners ====================================================================================
    private View.OnClickListener cardViewBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            timer.cancel();
            Intent i = new Intent(Activity_2.this, MainActivity.class);
            startActivity(i);
        }
    }; //end cardViewBackListener

    private View.OnClickListener cardViewAnswer1Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            answerPressed(((TextView)cardViewAnswer1.getChildAt(0)).getText().toString());

        }
    }; //end cardViewAnswer1Listener

    private View.OnClickListener cardViewAnswer2Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            answerPressed(((TextView)cardViewAnswer2.getChildAt(0)).getText().toString());
        }
    }; //end cardViewAnswer2Listener

    private View.OnClickListener cardViewAnswer3Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            answerPressed(((TextView)cardViewAnswer3.getChildAt(0)).getText().toString());
        }
    }; //end cardViewAnswer3Listener

    private View.OnClickListener cardViewAnswer4Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            answerPressed(((TextView)cardViewAnswer4.getChildAt(0)).getText().toString());
        }
    }; //end cardViewAnswer4Listener

    private View.OnClickListener buttonNextQuestionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startQuestion();
        }
    }; //end buttonNextQuestionListener
}