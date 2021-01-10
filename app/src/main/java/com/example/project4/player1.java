package com.example.project4;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

public class player1 extends Thread {

    private static final String TAG = "Player 1 Thread";
    public Handler handler;
    public Handler mainHandle;
    public Handler twoHandle;

    //Looper of thread 1 reference
    public Looper looper1;

    Message msg1;

    //Reference to the main activity
    MainActivity test;

    //The Current Guess of Player 1
    public ArrayList<Integer> guessOne;

    //The Current Number Owned by Player 1
    public ArrayList<Integer> oneNumber;

    //Store the index values of hints
    public ArrayList<Integer> hintValues = new ArrayList<Integer>();

    //Get a reference to the main activity
    public player1(MainActivity mainH)
    {
        test = mainH;
    }

    //Get the number that belongs to player 1
    public void setNumber1(ArrayList<Integer> n)
    {
        oneNumber = n;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void run() {

        Looper.prepare();

        looper1 = Looper.myLooper();

        handler = new Handler(){
            public void handleMessage(Message msg) {

                int what = msg.what;

                switch(what)
                {
                    //Make Guess
                    case 1:
                        Log.i(TAG, "One Case Thread 1");

                    twoHandle = test.p2.handler;
                    guessOne = getRandomNumberOne();

                    msg1 = twoHandle.obtainMessage(2);
                    msg1.obj = guessOne;

                    twoHandle.sendMessage(msg1);

                    break;
                    //Check Player 2 Guess
                    case 2:
                        Log.i(TAG, "Two Case Thread 1");

                        twoHandle = test.p2.handler;

                        ArrayList<Integer> extractData = (ArrayList<Integer>) msg.obj;
                        msg1 = twoHandle.obtainMessage(3);

                        msg1.arg1 = getPositionAccuracy(extractData);
                        msg1.arg2 = getHintNumber(extractData);

                        twoHandle.sendMessage(msg1);

                        break;
                    //Send Results to UI & Get Results From Player 2
                    case 3:
                        Log.i(TAG, "Three Case Thread 1");

                        mainHandle = test.mHandler;
                        msg1 = mainHandle.obtainMessage(1);
                        msg1.arg1 = msg.arg1;

                        msg1.arg2 = msg.arg2;

                        //Add The Values That Cannot Be in The guess
                        hintValues.add(msg.arg2);

                        msg1.obj = guessOne;

                        try { Thread.sleep(5000); }
                        catch (InterruptedException e) { System.out.println("Thread interrupted!") ; } ;

                        mainHandle.sendMessage(msg1);
                        break;
                }
            }
        };

        Log.i(TAG, "Thread 1 has started");

        Looper.loop();
    }

    //Get how many digits were guessed in the right/wrong position
    public int getPositionAccuracy(ArrayList<Integer> extractData)
    {

        int correctSpot = 0;
        int wrongSpot = 0;

        for(int i = 0; i < 4; i++)
        {
            if(extractData.get(i).equals(oneNumber.get(i)))
            {
                correctSpot++;
            }
            else if(extractData.indexOf(oneNumber.get(i)) != oneNumber.indexOf(oneNumber.get(i)) && oneNumber.indexOf(oneNumber.get(i)) != -1 && extractData.indexOf(oneNumber.get(i)) != -1)
            {
                wrongSpot++;
            }

        }

        int result = (correctSpot * 100) + wrongSpot;

        return result;
    }

    //Get the random hint number that is not on the opponents number
    public int getHintNumber(ArrayList<Integer> extractData)
    {
        int counter = 0;
        ArrayList<Integer> hints = new ArrayList<Integer>();

        for(int i = 0; i < 4; i++)
        {
            if(!oneNumber.contains(extractData.get(i)))
            {
                counter++;
                hints.add(extractData.get(i));
            }
        }

        if(counter != 0) {
            int randomNum = (int) (Math.random() * counter);
            return hints.get(randomNum);
        }

        return -1;

    }

    //Return a 4 digit non repeating number
    public ArrayList<Integer> getRandomNumberOne()
    {
        ArrayList<Integer> a = new ArrayList<Integer>();

        while(a.size() != 4)
        {
            int randomNum = (int) (Math.random() * 10);  // 0 to 9

            if(a.isEmpty() && !hintValues.contains(randomNum))
            {
                a.add(randomNum);
            }
            else
            {
                if(!a.contains(randomNum) && !hintValues.contains(randomNum))
                {
                    a.add(randomNum);
                }
            }
        }

        return a;
    }

}
