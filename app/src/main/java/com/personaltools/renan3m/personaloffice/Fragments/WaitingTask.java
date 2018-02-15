package com.personaltools.renan3m.personaloffice.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.personaltools.renan3m.personaloffice.R;

public class WaitingTask extends Fragment { // Esse fragment terá um contador em contagem regressiva responsavél pelo intervalo.
                                            // Deverá ter também um botão para zerar o contador e dar inicío ao CurrentTask
    private OnTaskInteraction mListener;

    public WaitingTask() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_waiting_task, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskInteraction) {
            mListener = (OnTaskInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
