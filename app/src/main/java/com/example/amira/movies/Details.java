package com.example.amira.movies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;
import java.util.List;

public class Details extends ActionBarActivity {
    boolean mtwopan;
    public static List<String> movieNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle bundle = new Bundle();
        bundle=getIntent().getExtras();
        DetailFragment fragments =new DetailFragment();
        fragments.setArguments(bundle);
            if (savedInstanceState == null) {
                DetailFragment fragment = new DetailFragment();
                fragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            }
        }


    }

