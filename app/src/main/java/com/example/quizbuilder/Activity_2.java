package com.example.quizbuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    String currAnswer; //store the string value of the current answer
    String currQuestion; //store the string value of the current question

    //variables for UI logic =======================================================================
    int numQuestions; //total number of questions
    int currQuestionNum = 1; //current number of question the user is on
    int streak; //how many answers the user got correct in a row
    int numAnsweredCorrect = 0; //how many questions were correct
    ArrayList<Integer> streakHistory = new ArrayList<Integer>(); //getting the streak history, used for longest streak
    ArrayList<Long> timePerQuestionHistory = new ArrayList<Long>(); //to get the time per question

    //variables for timer
    CountDownTimer timer; //timer object to countdown per question
    boolean timerRunning; //check if the timer is running
    long timerLeft; //time left in the timer
    int PROGRESS_BAR_START = 100; //progress bar starting value
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
            String stackTrace = Log.getStackTraceString(e);
            Log.w("File-Input Exception: ",stackTrace); //using Log.w to keep a log of IOException
        }catch (Exception e){
            String stackTrace = Log.getStackTraceString(e);
            Log.w("Exception: ",stackTrace); //using Log.w to keep track of general exceptions
        }
    }//end readFile method

    private void createHashDefTerms(){ //creating the hasmap of key-value pairs for definitions and terms
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

    private void loadAllAnswers(){ //loading array of answers, 1 correct, 3 wrong
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

    private void startQuestion(){ //starts a new question and multiple choice answers
        if (loadNewQuestionAndAnswer()){ //if we still have definitions/answers, loads the question as well
            loadAllAnswers(); //loading all the answers

            enableCardViewClickable(); //enabling the cards which hold the answers choices to be clickable
            disableButtonNextQuestionClickable(); //disabling the next question button
            resetCardViews();

            updateCurrQuestionNum();
            updateStreak();

            stopTimer();
            startTimer(); //starting the timer
        }else{ //if there are no more questions in the definitions list, go to results activity
            streakHistory.add(streak); //adding the last streak

            Intent i = new Intent(Activity_2.this, Activity_3.class);
            //bundling the data
            i.putExtra("Level", level); //level
            i.putExtra("DIFFICULTY", DIFFICULTY_TIME); //difficulty

            i.putExtra("numQuestions", numQuestions); //number of questions
            i.putExtra("numAnsweredCorrect", numAnsweredCorrect); //number of questions that were correct

            i.putExtra("streakHistory", streakHistory); //streak history
            i.putExtra("timePerQuestionHistory", timePerQuestionHistory); //time per question

            startActivity(i);
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

        timePerQuestionHistory.add(DIFFICULTY_TIME - timerLeft); //tracking the time spent per question
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
    private void resetCardViews(){ //resetting the colors of all the multiple choice answers
        cardViewAnswer1.setCardBackgroundColor(Color.parseColor("#ffffff"));
        cardViewAnswer2.setCardBackgroundColor(Color.parseColor("#ffffff"));
        cardViewAnswer3.setCardBackgroundColor(Color.parseColor("#ffffff"));
        cardViewAnswer4.setCardBackgroundColor(Color.parseColor("#ffffff"));
    }

    private void updateQuestion(){ //loading the new question into the textview
        textViewQuestion.setText(currQuestion);
    }//end updateQuestion method

    private void updateAnswerChoices(ArrayList<String> answers){ //updating the multiple choice answers of the card views
        textViewAnswer1.setText(answers.get(0));
        textViewAnswer2.setText(answers.get(1));
        textViewAnswer3.setText(answers.get(2));
        textViewAnswer4.setText(answers.get(3));
    }//end updateAnswerChoices method

    private void disableCardViewClickable(){ //disable the multiple choice answers from being clickable
        cardViewAnswer1.setClickable(false);
        cardViewAnswer2.setClickable(false);
        cardViewAnswer3.setClickable(false);
        cardViewAnswer4.setClickable(false);
    }//end disableCardViewClickable method

    private void enableCardViewClickable(){ //enabling the multiple choice answers to being clickable
        cardViewAnswer1.setClickable(true);
        cardViewAnswer2.setClickable(true);
        cardViewAnswer3.setClickable(true);
        cardViewAnswer4.setClickable(true);
    }//end enableCardViewClickable method

    private void disableButtonNextQuestionClickable(){ //disabling the next question button from being clickable
        buttonNextQuestion.setClickable(false);
        buttonNextQuestion.setBackgroundColor(Color.parseColor("#95989c"));
    }//end disableButtonNextQuestionClickable method

    private void enableButtonNextQuestionClickable(){ //enabling the next question button to being clickable
        buttonNextQuestion.setClickable(true);
        buttonNextQuestion.setBackgroundColor(Color.parseColor("#4b74fa"));
    }//end enableButtonNextQuestionClickable method

    private void updateStreak(){ //updating the streak, questions answered correctly in a row
        textViewStreakNum.setText(Integer.toString(streak));
    }//end updateStreak method

    private void resetStreak(){ //resetting the streak, when the user gets a question wrong
        streakHistory.add(streak);
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
    }//end startTimer method

    private void stopTimer(){
        if (timerRunning){
            timer.cancel();
        }
        timerLeft = DIFFICULTY_TIME;
        updateCountDownText();
        resetProgressBar();
        timerRunning = false;
    }//end stopTimer method

    private void pauseTimer(){
        if (timerRunning){
            timer.cancel();
        }
        timerRunning = false;
    }//end pauseTimer method

    private void updateCountDownText(){
        int minutes = (int) (timerLeft / 1000) / 60; //if adding minutes
        int seconds = (int) (timerLeft / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes,seconds);

        textViewProgressBarNum.setText(timeLeftFormatted);
    }//end updateCountDownText method

    private void updateProgressBar(){
        progressBarTimer.setProgress((int) progress_bar_curr);
    }

    private void resetProgressBar(){
        progress_bar_curr = PROGRESS_BAR_START;
        updateProgressBar();
    } //end resetProgressBar method


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