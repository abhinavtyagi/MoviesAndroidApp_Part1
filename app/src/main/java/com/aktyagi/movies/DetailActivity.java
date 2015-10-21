package com.aktyagi.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root_view = getLayoutInflater().inflate(R.layout.activity_detail, null);
        setContentView(root_view);
        TextView textView = (TextView) root_view.findViewById(R.id.detail_fragment_text_view_title);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String strIntentExtra = intent.getStringExtra("zzzz");
        textView.setText(strIntentExtra);

        String strPosterURL = intent.getStringExtra("imgurl");
        Log.i("PosterURL:", strPosterURL);
        ImageView imgView = (ImageView) root_view.findViewById(R.id.detail_fragment_img_view);
        Picasso picasso = Picasso.with(this);
        RequestCreator requestCreator = picasso.load(strPosterURL);
        requestCreator.into(imgView);

        String synopsis = intent.getStringExtra("synopsis");
        TextView synopsis_view = (TextView) root_view.findViewById(R.id.detail_fragment_text_view_synopsis);
        synopsis_view.setText("Synopsis: "+synopsis);

        String release_date = intent.getStringExtra("release_date");
        TextView release_date_view = (TextView) root_view.findViewById(R.id.detail_fragment_text_view_release_date);
        release_date_view.setText("Released on "+release_date);

        String rating = ""+intent.getDoubleExtra("rating", 0);
        TextView rating_view = (TextView) root_view.findViewById(R.id.detail_fragment_text_view_user_rating);
        rating_view.setText("Rating: "+rating);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setTitle(strIntentExtra);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
