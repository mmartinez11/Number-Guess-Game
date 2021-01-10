package com.example.project4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int SET_START = 1;
    public int round = 0;
    int newCounter = 0;

    //UI Thread Handler
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {

            int what = msg.what;

            if(round == 40)
            {
                what = 3;
            }

            switch(what)
            {
                //Get Turn Message From Player 1
                case 1:
                    Log.i("TEST", "One Case Main");

                    round++;

                    ArrayList<Integer> extractData1 = (ArrayList<Integer>) msg.obj;
                    displayInfo1(extractData1, msg.arg1, msg.arg2);

                    checkForWin(1, msg.arg1);

                    l2.adapter.add("Thinking..." + "(" + round + ")");
                    //Now Its Players 2 Turn
                    Message m2 = p2.handler.obtainMessage(1);
                    p2.handler.sendMessage(m2);

                    break;

                //Get Turn Message From Player 2
                case 2:
                    Log.i("TEST", "Two Case Main");

                    round++;

                    ArrayList<Integer> extractData2 = (ArrayList<Integer>) msg.obj;
                    displayInfo2(extractData2, msg.arg1, msg.arg2);

                    checkForWin(2, msg.arg1);

                    l1.adapter.add("Thinking..." + "(" + round + ")");
                    //Now Its Players 1 Turn
                    Message m1 = p1.handler.obtainMessage(1);
                    p1.handler.sendMessage(m1);


                    break;

                //End The Game
                case 3:
                    Log.i("TEST", "Three Case Main");
                    endGame(3);
                    break;
            }
        }
    };

    ArrayList<Integer> numberOne = new ArrayList<Integer>();
    ArrayList<Integer> numberTwo = new ArrayList<Integer>();

    private EditText textField1;
    private EditText textField2;

    public player1 p1;
    public player2 p2;

    public ListOne l1;
    public ListTwo l2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        textField1 = (EditText) findViewById(R.id.List1Number);
        textField1.setEnabled(false);

        textField2 = (EditText) findViewById(R.id.List2Number);
        textField2.setEnabled(false);

        l1 = (ListOne)getSupportFragmentManager().findFragmentById(R.id.List1);
        l2 = (ListTwo)getSupportFragmentManager().findFragmentById(R.id.List2);

        startThreads();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate Action Bar
        getMenuInflater().inflate(R.menu.new_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Menu Options
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.newGameB)
        {

            //If counter is more than 0 that means that the "New Game" button has been pressed more than once
            //Therefore create a new game
            if(p2.isAlive() && p1.isAlive() && newCounter != 0)
            {
                //Toast.makeText(this,"THREAD IS STOPED", Toast.LENGTH_SHORT).show();
                endGame(4);
                clearGameBoard();
                startThreads();
                round = 0;

                Log.i("NEW GAME", "NEW GAME STARTED");
            }

            newCounter++;

            l2.adapter.add("New Game: Player 2");
            l1.adapter.add("New Game: Player 1");

            mainGame();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Start the game and get the numbers picked by each player
    public void mainGame()
    {
        //Set the numbers each thread picked
        getNumbers();
        playerGameMoveStart(SET_START);

    }

    //Start Brand New Threads
    public void startThreads()
    {
        p1 = new player1(this);
        p2 = new player2(this);

        p1.start();
        p2.start();

    }

    //Get the numbers each player will use
    void getNumbers()
    {
        numberOne = getRandomNumber();
        numberTwo = getRandomNumber();

        p1.setNumber1(numberOne);
        p2.setNumber2(numberTwo);

        textField1.setText("Number: " + numberOne.get(0) + numberOne.get(1) + numberOne.get(2) + numberOne.get(3));
        textField2.setText("Number: " + numberTwo.get(0) + numberTwo.get(1) + numberTwo.get(2) + numberTwo.get(3));
    }


    //Start the game with player 1 making the first move
    public void playerGameMoveStart(int c)
    {
        l1.adapter.add("Thinking...");
        Message msg = p1.handler.obtainMessage(c);
        p1.handler.sendMessage(msg);
    }

    //Return a 4 digit non repeating number
    public ArrayList<Integer> getRandomNumber()
    {
        ArrayList<Integer> a = new ArrayList<Integer>();

        while(a.size() != 4)
        {
            int randomNum = (int) (Math.random() * 10);  // 0 to 9

            if(a.isEmpty())
            {
                a.add(randomNum);
            }
            else
            {
                if(!a.contains(randomNum))
                {
                    a.add(randomNum);
                }
            }
        }

        return a;
    }

    //Displays the information of player 1
    public void displayInfo1(ArrayList<Integer> guess, int accuracy, int hint)
    {
        int correctSpot = accuracy / 100;
        int wrongSpot = accuracy % 10;

        l1.adapter.add("My Guess is: " + guess.get(0) + guess.get(1) + guess.get(2) + guess.get(3));
        l1.adapter.add("Correct: " + correctSpot + " Wrong: " + wrongSpot);

        if(hint != -1)
        {
            l1.adapter.add("Hint: " + hint);
        }
        else
        {
            l1.adapter.add("Hint: N/A");
        }

    }

    //Displays the information of player 2
    public void displayInfo2(ArrayList<Integer> guess, int accuracy, int hint)
    {
        int correctSpot = accuracy / 100;
        int wrongSpot = accuracy % 10;

        l2.adapter.add("My Guess is: " + guess.get(0) + guess.get(1) + guess.get(2) + guess.get(3));
        l2.adapter.add("Correct: " + correctSpot + " Wrong: " + wrongSpot);

        if(hint != -1)
        {
            l2.adapter.add("Hint: " + hint);
        }
        else
        {
            l2.adapter.add("Hint: N/A");
        }
    }

    //Ends The Game
    void endGame(int player)
    {
        if(player == 1)
        {
            l1.adapter.add("PLAYER 1 WON!!!");
            l2.adapter.add("PLAYER 2 LOST!!!");
        }
        else if(player == 2)
        {
            l1.adapter.add("PLAYER 1 LOST!!!");
            l2.adapter.add("PLAYER 2 WON!!!");
        }
        else if(player == 3)
        {
            l1.adapter.add("MAXED OUT ROUNDS!!!");
            l2.adapter.add("MAXED OUT ROUNDS!!!");
        }

        p1.looper1.quit();
        p2.looper2.quit();

    }

    //Checks for a Player win
    public void checkForWin(int player, int accuracy)
    {
        int correctSpot = accuracy / 100;

        if(correctSpot == 4)
        {
            endGame(player);
        }
    }

    //Clears the Game Board
    public void clearGameBoard()
    {
        l1.adapter.clear();
        l2.adapter.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        p1.looper1.quit();
        p2.looper2.quit();

    }
}