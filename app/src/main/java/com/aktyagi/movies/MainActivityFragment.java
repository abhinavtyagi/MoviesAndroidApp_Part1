package com.aktyagi.movies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * http://api.themoviedb.org/3/movie/popular?api_key=a16ff5003d5dd8cd309cbb817eba0777
 * http://api.themoviedb.org/3/movie/top_rated?api_key=a16ff5003d5dd8cd309cbb817eba0777
 * http://api.themoviedb.org/3/movie/now_playing?api_key=a16ff5003d5dd8cd309cbb817eba0777
 * http://api.themoviedb.org/3/movie/upcoming?api_key=a16ff5003d5dd8cd309cbb817eba0777
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static GridViewAdapter mAdapter = null;
    private Context mContext;
    private  String mMoviePreference;

    @Override
    public void onStart() {
        super.onStart();
        update();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String user_preference_entry_value = sp.getString(getString(R.string.preferences_movie_key), getString(R.string.preferences_movie_default_value));
        String[] entries= getResources().getStringArray(R.array.preferences_movie_entries);
        String[] entryValues = getResources().getStringArray(R.array.preferences_movie_entryValues);
        int index = 0;
        for(String s : entryValues) {
            if(s.equals(user_preference_entry_value)) {
                user_preference_entry_value = entries[index];
                break;
            }
            index++;
        }

        getActivity().setTitle(user_preference_entry_value + " Movies");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mAdapter = new GridViewAdapter(getURLStringBasedOnPreferences());
    }
    private String getURLStringBasedOnPreferences() {
        String strServer = "api.themoviedb.org";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mMoviePreference = prefs.getString(getString(R.string.preferences_movie_key), getString(R.string.preferences_movie_default_value));
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http").authority(strServer).appendPath("3").appendPath("movie").appendPath(mMoviePreference).
                appendQueryParameter("api_key", "a16ff5003d5dd8cd309cbb817eba0777");
        String url = uriBuilder.toString();
        return  url;
    }

    private void update() {
        String url = getURLStringBasedOnPreferences();
        if(mAdapter==null) {
            mAdapter = new GridViewAdapter(url);
        } else {
            MovieDataTaskInput input = new MovieDataTaskInput();
            input.mStrUrl = url;
            input.mAdapter = mAdapter;
            new FetchMoviesTask().execute(input);
        }
    }

    public MainActivityFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final GridView gridView = (GridView)rootView.findViewById(R.id.mainGridView);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridView gv = gridView;
                Context context = view.getContext();
                Intent intent = new Intent(context, DetailActivity.class);
                MovieData movieData = mAdapter.getData(position);
                intent.putExtra("zzzz", movieData.original_title);
                // e.g. http://image.tmdb.org/t/p/w185//pJ5FjCO9X0jhxtQlUGtV0R0P4Bh.jpg
                intent.putExtra("imgurl", "http://image.tmdb.org/t/p/w185/"+movieData.poster_path);
                intent.putExtra("synopsis", movieData.overview);
                intent.putExtra("release_date", movieData.release_date);
                intent.putExtra("rating", movieData.vote_average);

                startActivity(intent);
            }
        });
        return rootView;
    }

    class GridViewAdapter extends BaseAdapter{
        MovieData[] data;
        public GridViewAdapter(String strURL) {
            MovieDataTaskInput taskInput = new MovieDataTaskInput();
            taskInput.mStrUrl = strURL;
            taskInput.mAdapter = this;
            new FetchMoviesTask().execute(taskInput);
        }
        public void setData(MovieData[] movieData) {
            data = movieData;
        }
        public MovieData getData(int index) {
            if(data!=null && index<=data.length) {
                return data[index];
            }
            return null;
        }
        public MovieData[] getData() {
            return data;
        }

        @Override
        public int getCount() {
            if(data==null)
                return 0;
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            ViewHolder viewHolder = null;
//            if data is not available, populate it using some default image and url (another way is to populate the adapter with default data!)
            MovieData movieData = getData(position);
            if(movieData==null)
                return null;
            if(convertView==null) {
                viewHolder = new ViewHolder();
                Context context = parent.getContext();
                if(context==null)
                    Log.e("Error:", "ctx is null");


                LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                Assert.assertNotNull("LayoutInflater is NULL", layoutInflater);
                convertView = layoutInflater.inflate(R.layout.grid_cell, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.grid_cell_textView);
                ImageView imgView = (ImageView) convertView.findViewById(R.id.grid_cell_image_view);
                viewHolder.mTextView = textView;
                viewHolder.mImageView = imgView;
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.mTextView.setText(movieData.title);

            String strImgURL = "http://image.tmdb.org/t/p/w185/"+movieData.poster_path;
            //http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
            Picasso picasso = Picasso.with(getActivity());
            RequestCreator requestCreator = picasso.load(strImgURL);
            requestCreator.into(viewHolder.mImageView);
            return convertView;
        }

        public class ViewHolder{
            public TextView     mTextView;
            public ImageView    mImageView;
            ViewHolder() {
                mTextView = null;
                mImageView = null;
            }
        }
    }
    class FetchMoviesTask extends AsyncTask<MovieDataTaskInput,Void, MovieDataTaskOutput> {

        private final String LOG_TAG;

        FetchMoviesTask() {
            LOG_TAG = getClass().getName();
        }

        @Override
        protected MovieDataTaskOutput doInBackground(MovieDataTaskInput... params) {
            MovieDataTaskOutput outData = null;
            if(params==null ||params.length==0)
                return outData;
            MovieDataTaskInput input = params[0];

            //1. check if connectivity is there
            boolean isConnected = false;
            Context context = getActivity();
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected = activeNetworkInfo.isConnected();
            Assert.assertEquals(isConnected, true);
            String jsonData = null;
            String strUrl = null;
            URL url=null;
            HttpURLConnection urlConnection = null;
            if(isConnected) {
                try {
                    Log.i(LOG_TAG, "Starting connect etc etc");
                    strUrl = input.mStrUrl;
                    url = new URL(strUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer stringBuffer = new StringBuffer();
                    if(inputStream !=null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line = "";
                        while((line=reader.readLine())!=null) {
                            stringBuffer.append(line+"\n");
                        }
                    }
                    jsonData = stringBuffer.toString();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } catch (Exception e){
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    Log.i(LOG_TAG, "JSONData=" + jsonData);
                    if(urlConnection!=null)
                        urlConnection.disconnect();
                }
                outData = getOutDataFromJSON(jsonData, input.mAdapter);
            }
            return outData;
        }
        private MovieDataTaskOutput getOutDataFromJSON(String jsonString, BaseAdapter adapter) {
            MovieDataTaskOutput outData = null;
            // here get the out data
            try {
                JSONObject rootNode = new JSONObject(jsonString);
                JSONArray movieDataJSONArray = rootNode.getJSONArray("results");
                MovieData[] movieDataArray = new MovieData[movieDataJSONArray.length()];
                for(int i=0; i<movieDataJSONArray.length();i++) {
                    JSONObject object = movieDataJSONArray.getJSONObject(i);
                    MovieData movieData = new MovieData();
                    movieData.title             = object.getString("title");
                    movieData.original_title    = object.getString("original_title");
                    movieData.backdrop_path     = object.getString("backdrop_path");
                    movieData.poster_path       = object.getString("poster_path");
                    movieData.original_language = object.getString("original_language");
                    movieData.overview          = object.getString("overview");
                    movieData.release_date      = object.getString("release_date");
                    movieData.vote_average      = object.getDouble("vote_average");
                    movieData.vote_count        = object.getInt("vote_count");
                    movieData.adult             = object.getBoolean("adult");
                    movieData.popularity        = object.getDouble("popularity");
                    movieDataArray[i] = movieData;
                }
                outData = new MovieDataTaskOutput();
                outData.movieDataArray = movieDataArray;
                outData.mAdapter = adapter;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return outData;
        }

        @Override
        protected void onPostExecute(MovieDataTaskOutput movieDataTaskOutput) {
            super.onPostExecute(movieDataTaskOutput);
            Assert.assertEquals(movieDataTaskOutput==null, false);
            if(movieDataTaskOutput!=null) {
                Log.i(LOG_TAG, movieDataTaskOutput.toString());
                GridViewAdapter adapter = (GridViewAdapter) movieDataTaskOutput.mAdapter;
                adapter.notifyDataSetInvalidated();
                adapter.setData(movieDataTaskOutput.movieDataArray);
                adapter.notifyDataSetChanged();
//                adapter.refresh(movieDataTaskOutput.movieDataArray);

            }
        }
    }

    class MovieDataTaskInput {
        public String mStrUrl;
        public BaseAdapter mAdapter;

    }
    class  MovieDataTaskOutput{
        public MovieData[] movieDataArray;
        public BaseAdapter mAdapter;
    }
    class MovieData{
        public String  title;
        public String  original_title;
        public String  backdrop_path;
        public String  poster_path;
        public String  original_language;
        public String  overview;
        public String  release_date;
        public double  vote_average;
        public int     vote_count;
        public boolean adult;
        public double  popularity;

        public MovieData() {
            title = null; original_language = null; backdrop_path = null; poster_path = null;
            original_language = null; overview = null; release_date = null;
            vote_average = 0; vote_count = 0; adult = false;
        }

        public MovieData(String title, String original_title, String backdrop_path,
                         String poster_path, String original_language, String overview,
                         String release_date, double vote_average, int vote_count,
                         double popularity, Boolean adult) {
            this.title = title;
            this.original_title = original_title;
            this.backdrop_path = backdrop_path;
            this.poster_path = poster_path;
            this.original_language = original_language;
            this.overview = overview;
            this.release_date = release_date;
            this.vote_average = vote_average;
            this.vote_count = vote_count;
            this.adult = adult;
            this.popularity = popularity;
        }
    }
}

