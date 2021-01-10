package com.example.project4;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

public class player2 extends Thread {

    private static final String TAG = "Player 2 Thread";

    public Handler handler;
    public Handler mainHandle;
    public Handler oneHandle;

    public Looper looper2;

    //The Current Guess of Player 1
    public ArrayList<Integer> guessTwo;

    //Store player 2s number
    ArrayList<Integer> twoNumber;

    //Store all hint values
    public ArrayList<Integer> hintValues2 = new ArrayList<Integer>();

    Message msg2;

    //Activity reference
    MainActivity test2;

    public player2(MainActivity mainH)
    {
        test2 = mainH;
    }

    //Get player 2 number
    public void setNumber2(ArrayList<Integer> n)
    {
        twoNumber = n;
    }


    @SuppressLint("HandlerLeak")
    @Override
    public void run() {

        Looper.prepare();

        looper2 = Looper.myLooper();

        handler = new Handler(){
            public void handleMessage(Message msg) {

                int what = msg.what;

                switch(what)
                {
                    //Make Guess
                    case 1:
                        Log.i(TAG, "One Case Thread 2");

                        oneHandle = test2.p1.handler;

                        guessTwo = getRandomNumberTwo();

                        msg2 = oneHandle.obtainMessage(2);
                        msg2.obj = guessTwo;

                        oneHandle.sendMessage(msg2);

                        break;
                    //Check Player 1 Guess
                    case 2:
                        Log.i(TAG, "Two Case Thread 2");

                        oneHandle = test2.p1.handler;

                        ArrayList<Integer> extractData = (ArrayList<Integer>) msg.obj;

                        msg2 = oneHandle.obtainMessage(3);

                        msg2.arg1 = getPositionAccuracy(extractData);
                        msg2.arg2 = getHintNumber(extractData);

                        oneHandle.sendMessage(msg2);

                        break;
                    //Send Results To UI
                    case 3:
                        Log.i(TAG, "Three Case Thread 2");

                        mainHandle = test2.mHandler;

                        msg2 = mainHandle.obtainMessage(2);
                        msg2.arg1 = msg.arg1;


                        msg2.arg2 = msg.arg2;
                        hintValues2.add(msg.arg2);

                        msg2.obj = guessTwo;

                        try { Thread.sleep(5000); }
                        catch (InterruptedException e) { System.out.println("Thread interrupted!") ; } ;

                        mainHandle.sendMessage(msg2);

                        break;
                }
            }
        };

        Log.i(TAG, "Thread 2 has started");

        Looper.loop();
    }

    //Get how many digits were guessed in the right/wrong position
    public int getPositionAccuracy(ArrayList<Integer> extractData)
    {

        int correctSpot = 0;
        int wrongSpot = 0;

        for(int i = 0; i < 4; i++)
        {
            if(extractData.get(i).equals(twoNumber.get(i)))
            {
                correctSpot++;
            }
            else if(extractData.indexOf(twoNumber.get(i)) != twoNumber.indexOf(twoNumber.get(i)) && twoNumber.indexOf(twoNumber.get(i)) != -1 && extractData.indexOf(twoNumber.get(i)) != -1)
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
            if(!twoNumber.contains(extractData.get(i)))
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
    public ArrayList<Integer> getRandomNumberTwo()
    {
        ArrayList<Integer> a = new ArrayList<Integer>();

        while(a.size() != 4)
        {
            int randomNum = (int) (Math.random() * 10);  // 0 to 9

            if(a.isEmpty() && !hintValues2.contains(randomNum))
            {
                a.add(randomNum);
            }
            else
            {
                if(!a.contains(randomNum) && !hintValues2.contains(randomNum))
                {
                    a.add(randomNum);
                }
            }
        }

        return a;
    }

}
