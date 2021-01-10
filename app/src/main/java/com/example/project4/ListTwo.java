package com.example.project4;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.ListFragment;

import java.util.ArrayList;

public class ListTwo extends ListFragment {

    public ArrayList<String> listItemsTwo = new ArrayList<String>();

    public ArrayAdapter<String> adapter;

    @Override
    public void onAttach(Context activity){
        super.onAttach(activity);

    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_two, listItemsTwo);
        setListAdapter(adapter);

    }


    @Override
    public void onPause() {
        super.onPause();
    }

}
