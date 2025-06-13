package com.huytran.goodlife.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.huytran.goodlife.R;
import com.huytran.goodlife.pages.home.HomeActivity;
import com.huytran.goodlife.pages.login.LoginScreenActivity;

public class IntroFragment5 extends Fragment {

    private Button getStarted;

    public IntroFragment5() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intro5, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getStarted = view.findViewById(R.id.get_started_btn);
        getStarted.setOnClickListener(v -> {

            SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("isIntroShown", true).apply();

            Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }
}
