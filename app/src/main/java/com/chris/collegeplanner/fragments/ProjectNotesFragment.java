package com.chris.collegeplanner.fragments;

/**
 * Created by Chris on 07/02/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.chris.collegeplanner.R;

public class ProjectNotesFragment extends Fragment {

    RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_project_notes, container, false);

        return rootView;
    }
}
