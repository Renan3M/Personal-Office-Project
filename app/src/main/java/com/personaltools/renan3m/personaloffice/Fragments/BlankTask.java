package com.personaltools.renan3m.personaloffice.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.personaltools.renan3m.personaloffice.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

// Motivation stuff
public class BlankTask extends Fragment implements OnTaskInteraction{
    private static final String TAG = "BlankTask"; // Será iniciado toda vez q o usuario iniciar o app e ñ houverem tasks atuais.

    private OnTaskInteraction mListener;


    public BlankTask() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank_task, container, false);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskInteraction ) {
            mListener = (OnTaskInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTaskInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
