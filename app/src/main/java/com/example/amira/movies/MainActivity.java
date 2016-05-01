package com.example.amira.movies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class MainActivity extends ActionBarActivity implements MovieFragment.Call {



   public static boolean mTwoPane = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  setHasOptionsMenu(true);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()

                        .replace(R.id.movie_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;

                getSupportFragmentManager().beginTransaction()

                        .replace(R.id.container, new MovieFragment())
                        .commit();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//       // getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void get(Bundle bundle) {
        if (mTwoPane) {
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(bundle);
            Log.e("TAF", "inside get" + bundle.toString());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {

            MovieFragment fragments=new MovieFragment();
            fragments.setArguments(bundle);
            Log.e("TAF", "inside get" + bundle.toString());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragments)
                    .commit();
        }
    }
}
