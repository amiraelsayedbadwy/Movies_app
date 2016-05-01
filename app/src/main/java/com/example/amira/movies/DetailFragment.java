package com.example.amira.movies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

public class DetailFragment extends android.support.v4.app.Fragment {////////
    ListView list;
    String image = "";
    Context mContext;
    ListAdpater adpater;
    Button fav;
    int x;
    Database data;
    ArrayList<Trailer> trailerlist;
    Bundle bundle;
    String title;
    ArrayList<MovieFragment.Type> itemss;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        itemss = new ArrayList<>();
        trailerlist = new ArrayList<>();
        data = new Database(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.d("detailsFragment", "inside dtails fragment");
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        list = (ListView) rootView.findViewById(R.id.lv_trailers_reviews);
        LayoutInflater inflae = getActivity().getLayoutInflater();

        View v = inflae.inflate(R.layout.listheader, list, false);
        list.addHeaderView(v);
        final TextView overviewTV = (TextView) v.findViewById(R.id.tv_overview);
        final TextView datetv = (TextView) v.findViewById(R.id.tv_reasledate);
        final TextView rateTV = (TextView) v.findViewById(R.id.tv_rate);
        final TextView titleTV = (TextView) v.findViewById(R.id.tv_title);
        final ImageView poster = (ImageView) v.findViewById(R.id.img_poster);
        fav = (Button) v.findViewById(R.id.checkBox);
       Button remove = (Button)v.findViewById(R.id.delete);
       // sharingButton.setLayoutParams(new ViewGroup.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        bundle = getArguments();


        if (bundle != null) {
            x = bundle.getInt("id");
            final String overview = bundle.getString("over");
            overviewTV.setText(overview);
//            Log.d("overview", overview);

            final String releasedate = bundle.getString("date");
            datetv.setText(releasedate);
            title = bundle.getString("title");
            titleTV.setText(title);
            final String rate = bundle.getString("rate");
            rateTV.setText(rate);
            asyictask_reivews myreviews = new asyictask_reivews();
            myreviews.execute(bundle.getInt("id") + "");

            image = bundle.getString("image");
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w300" + image).into(poster);


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (list.getAdapter().getItemViewType(position) == ListAdpater.choosetype.TRAILER_ITEM.ordinal()) {
                        //3shn get itemview type betrg3 int fa ast5damt function el asmha ordinal
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(Constants.BASE_YOUTUBE_URL + ((Trailer) itemss.get(position - 1)).getKey()));
                        startActivity(intent);
                    }
                }
            });
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (list.getAdapter().getItemViewType(position) == ListAdpater.choosetype.TRAILER_ITEM.ordinal())
                    {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

                        String text=Constants.BASE_YOUTUBE_URL + ((Trailer) itemss.get(position - 1)).getKey();
                        sharingIntent.putExtra(Intent.EXTRA_TEXT,text );
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        startActivity(sharingIntent);
                    }
                    return true;
                }
            });

            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.ifexist(title)) {
                        Toast.makeText(getActivity(), "Already exist", Toast.LENGTH_SHORT).show();
                    } else
                        data.add(image, title, overview, releasedate, rate, x);

                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    data.delete_movie(title);
                    Toast.makeText(getActivity(),"removed from favourite ", Toast.LENGTH_LONG).show();
                }
            });

        }

        return rootView;
    }


    public class asyictask_reivews extends AsyncTask<String, Void, ArrayList<MovieFragment.Type>> {


        public ArrayList<MovieFragment.Type> get_review(String json) throws JSONException {
            String results = "results";
            String id = "content";

            JSONObject moviejson = new JSONObject(json);
            JSONArray moviearray = moviejson.getJSONArray(results);

            ArrayList<MovieFragment.Type> result = new ArrayList<>();
            for (int i = 0; i < moviearray.length(); i++) {
                JSONObject movie = moviearray.getJSONObject(i);
                Review re = new Review();
                re.setContent(movie.getString(id));
                result.add(re);
            }
            Log.e("reviews", json);
            return result;

        }

        @Override
        protected ArrayList<MovieFragment.Type> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            try {


                URL url = new URL("http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?api_key=da84c4afea059e8ae06f74c450ea8793");
                Log.v("url", String.valueOf(url));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                JsonStr = buffer.toString();

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

                    }
                }
            }

            try {
                return get_review(JsonStr);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieFragment.Type> review) {
            super.onPostExecute(review);
            // adpater=new ListAdpater(mContext,review);
            // list.setAdapter(adpater);
            itemss.addAll(review);
            asyictask_trailer mytrailer = new asyictask_trailer();
            mytrailer.execute(bundle.getInt("id") + "");



        }
    }

    public class asyictask_trailer extends AsyncTask<String, Void, ArrayList<MovieFragment.Type>> {
        public ArrayList<MovieFragment.Type> get_trailers(String json) throws JSONException {
            String results = "results";
            String id = "key";

            JSONObject moviejson = new JSONObject(json);
            JSONArray moviearray = moviejson.getJSONArray(results);

            ArrayList<MovieFragment.Type> result = new ArrayList<>();
            for (int i = 0; i < moviearray.length(); i++) {
                JSONObject movie = moviearray.getJSONObject(i);
                Trailer trailer = new Trailer();
                trailer.setName(movie.getString("name"));
                trailer.setKey(movie.getString(id));
                result.add(trailer);
                trailerlist.add(trailer);
            }
            return result;
        }

        @Override
        protected ArrayList<MovieFragment.Type> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            try {


                URL url = new URL("http://api.themoviedb.org/3/movie/" + params[0] + "/videos?api_key=11cfc2ad10f26cbd932760da40aabce8");
                Log.d("url", String.valueOf(url));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                JsonStr = buffer.toString();

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

                    }
                }
            }

            try {
                return get_trailers(JsonStr);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieFragment.Type> trailrs) {
            super.onPostExecute(trailrs);
            itemss.addAll(trailrs);
            adpater = new ListAdpater(mContext, itemss);
            list.setAdapter(adpater);

        }
    }
}