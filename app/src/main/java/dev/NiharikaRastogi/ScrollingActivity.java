package dev.NiharikaRastogi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import butterknife.ButterKnife;
import dev.NiharikaRastogi.models.Structure;
import dev.NiharikaRastogi.utils.AppController;

public class ScrollingActivity extends AppCompatActivity{

    Structure movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Added to your Favourite", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        movie = intent.getParcelableExtra("Structure");
        CollapsingToolbarLayout collapse_toolbar = ButterKnife.findById(this, R.id.toolbar_layout);
        collapse_toolbar.setTitle(movie.title);
        TextView dr = ButterKnife.findById(this, R.id.date);
        dr.setText(movie.release_date);
        TextView rate = ButterKnife.findById(this, R.id.rating);
        rate.setText(movie.rating);
        String popularity = movie.popularity;
        popularity = popularity.substring(0, 2) + "%";

        TextView popular = ButterKnife.findById(this, R.id.popularity);
        popular.setText(popularity);
        TextView over = ButterKnife.findById(this, R.id.detail);
        over.setText(movie.overview);
        NetworkImageView pic = (NetworkImageView) findViewById(R.id.backdrop_pic);
        pic.setImageUrl("http://image.tmdb.org/t/p/w342/" + movie.backdrop_path, AppController.getInstance().getImageLoader());
    }



}
