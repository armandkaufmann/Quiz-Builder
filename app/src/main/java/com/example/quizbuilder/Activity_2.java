package com.example.quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Activity_2 extends AppCompatActivity {
    Level level;
    long DIFFICULTY;

    //setting up the data structures for the text file
    ArrayList<String> defList = new ArrayList<String>(); //to hold the definitions
    ArrayList<String> termList = new ArrayList<String>(); //to hold the terms
    HashMap<String, String> hash = new HashMap<String, String>(); //to hold the key value pairs of terms and definitions

    //variables for quiz
    String answer;
    int numQuestions;
    int streak = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        //data from bundle
        DIFFICULTY = (long) getIntent().getLongExtra("DIFFICULTY", 90000);
        level = (Level) getIntent().getSerializableExtra("Level");

        //file input and processing
        readFile(); //reading the file into memory and parsing
        createHashDefTerms(); //creating the hashmap for the definitions and terms
        Collections.shuffle(defList); //shuffling the definitions
    }

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
}