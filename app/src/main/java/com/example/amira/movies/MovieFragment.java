package com.example.amira.movies;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MovieFragment extends Fragment {

    GridView grid;
    ArrayList<Movie> moviesList;
    private GridAdapter gridAdapter;


    public MovieFragment() {
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        Boolean isInternetPresent = false;
        InternetConnection cd;
        setHasOptionsMenu(true);
        moviesList = new ArrayList<>();
        Log.d("movieFragment", "inside movie fragment");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.actin_top_rated) {
            moviesList.clear();

            new TaskManag().execute(Constants.topRated);
            Toast.makeText(getActivity(), "new adapter", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_popular) {
            moviesList.clear();

            new TaskManag().execute(Constants.sortType);
            Toast.makeText(getActivity(), "new adapter", Toast.LENGTH_SHORT).show();
            grid.setVisibility(View.VISIBLE);
            return true;

        } else if (id == R.id.action_fav) {
            Database d = new Database(getActivity());
            Cursor data = d.Fetch_all();
            if (data != null) {
              //  ArrayList<Movie> movies = new ArrayList<>();
                moviesList.clear();
                Movie movie;
                do {
                    movie = new Movie();
                    movie.setPoster(data.getString(data.getColumnIndex("poster")));
                    movie.setTitle(data.getString(data.getColumnIndex("title")));
                    movie.setOverview(data.getString(data.getColumnIndex("overview")));
                    movie.setReleaseDate(data.getString(data.getColumnIndex("relasedate")));
                    movie.setVoteAverage(data.getString(data.getColumnIndex("vote_average")));
                    movie.setId(data.getInt(data.getColumnIndex("movie_id")));
                    moviesList.add(movie);
                    grid.setAdapter(new GridAdapter(getActivity(), moviesList));
                    Toast.makeText(getActivity(), "new adapter", Toast.LENGTH_SHORT).show();
                } while (data.moveToNext());

            } else {
                Toast.makeText(getActivity(), "null data", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        grid = (GridView) rootView.findViewById(R.id.gridView1);

        TaskManag taskManag = new TaskManag();
        taskManag.execute(Constants.sortType);
        //grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = getBundle(position);
                ((Call) (getActivity())).get(bundle);


                if (MainActivity.mTwoPane == false) {
                    Intent intent = new Intent(getActivity(), Details.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @NonNull
    private Bundle getBundle(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("over", moviesList.get(position).getOverview());//overview
        bundle.putString("title", moviesList.get(position).getTitle());//title
        bundle.putString("rate", moviesList.get(position).getVoteAverage());//avearge rate
        bundle.putString("date", moviesList.get(position).getReleaseDate());//relasedate
        bundle.putString("image", moviesList.get(position).getPoster());//path
        bundle.putInt("id", moviesList.get(position).getId());//id
        Log.e("GRID", bundle.toString());
        return bundle;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    public class TaskManag extends AsyncTask<String, Void, ArrayList<Movie>> {//a3ed tany async task
// awou

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
// ham3l 3 asnk task one for movie //done  ll reviews and le trial

            try {


                String api_key = "da84c4afea059e8ae06f74c450ea8793";

                Constants.Movie_URL =
                        "https://api.themoviedb.org/3/movie/" + params[0] + "?api_key=" + api_key;
                Log.d("url", "movie url" + Constants.Movie_URL);
                URL url = new URL(Constants.Movie_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();


                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                Constants.fetchmovies = buffer.toString();
            } catch (IOException e) {

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        // Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

            }


            try {
                Log.e("TAG", Constants.fetchmovies);
                return Getmovies(Constants.fetchmovies);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public ArrayList<Movie> Getmovies(String mo)
                throws JSONException {
//            Constants.postersList.clear();
            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String ORIGINAL_TITLE = "original_title";
            final String VOTE_AVG = "vote_average";
            final String id = "id";
            JSONObject obj = new JSONObject(mo);
            JSONArray jsonArray = obj.getJSONArray(RESULTS);

            Movie movie;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieData = jsonArray.getJSONObject(i);
                String moviePosterPath = movieData.getString(POSTER_PATH);

                movie = new Movie();
                movie.setTitle(movieData.getString(ORIGINAL_TITLE));
                movie.setPoster(moviePosterPath);
                movie.setOverview(movieData.getString(OVERVIEW));
                movie.setReleaseDate(movieData.getString(RELEASE_DATE));
                movie.setVoteAverage(movieData.getString(VOTE_AVG));
                movie.setId(movieData.getInt(id));
                moviesList.add(movie);

            }
            return moviesList;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> posterPaths) {
            if (posterPaths != null) {

                gridAdapter = new GridAdapter(getActivity(), posterPaths);
                grid.setAdapter(gridAdapter);
                if (MainActivity.mTwoPane) {
                    ((Call) getActivity()).get(getBundle(0));//3shan ageb awoul movie
                }

            }
        }
    }


    public interface Call {
        void get(Bundle bundle);
    }

    public interface Type {
        int get_Type();
    }


}

