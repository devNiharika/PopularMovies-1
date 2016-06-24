package dev.NiharikaRastogi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.NiharikaRastogi.adapter.RecyclerAdapter;
import dev.NiharikaRastogi.models.Structure;
import dev.NiharikaRastogi.utils.AppController;
import dev.NiharikaRastogi.utils.GridAutofitLayoutManager;

public class MainActivity extends AppCompatActivity {
    ArrayList<Structure> arrayList = new ArrayList<>();
    RecyclerAdapter recyclerAdapter;
    AlertDialog dialog;
    int selected;
    @BindString(R.string.POPULARITY)
    String sortBy;
    @BindString(R.string.baseURL)
    String baseURL;
    @BindString(R.string.KEY)
    String KEY;
    int page;
    String TAG = "MainActivity";
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = ButterKnife.findById(this, R.id.recyclerView);
        gridLayoutManager = new GridAutofitLayoutManager(getApplicationContext(), 150);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
            setupRecyclerView();
        } else {
            selected = 0;
            page = 1;
            setupRecyclerView();
            fetch();
        }
        Log.d("OnCreate", "OnCreate called");

    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerAdapter = new RecyclerAdapter(MainActivity.this, arrayList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == arrayList.size() - 1) {
                    fetch();
                }
            }
        });
    }

    private void fetch() {
        String finalURL = baseURL + KEY + "&sort_by=" + sortBy + "&page=" + page;
        Log.d("URL", finalURL);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(finalURL,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE", response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                arrayList.add(new Structure(jsonObject.getInt("id"), jsonObject.getString("title"), jsonObject.getString("poster_path"), jsonObject.getString("popularity"), jsonObject.getString("vote_average"), jsonObject.getString("overview"), jsonObject.getString("release_date"), jsonObject.getString("backdrop_path")));
                            }
                            recyclerAdapter.notifyDataSetChanged();
                            page++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(TAG, "Error: " + error.getMessage());
                for (int i = 0; i < 10; i++) {
                    arrayList.add(new Structure(12345, " Test Title", "poster_path", "100", "8.9", "overview", "release_date", "backdrop_path"));
                }
                recyclerAdapter.notifyDataSetChanged();
                Snackbar.make(findViewById(R.id.root), "Aw, Snap! Something went wrong", Snackbar.LENGTH_SHORT).show();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "JSON_OBJECT_REQUEST");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("DATA", arrayList);
        outState.putInt("selected", selected);
        outState.putParcelable("LayoutManager", gridLayoutManager.onSaveInstanceState());
        outState.putInt("page", page);
        Log.d("onSaveInstanceState", "onSaveInstanceState called");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        arrayList = savedInstanceState.getParcelableArrayList("DATA");
        selected = savedInstanceState.getInt("selected");
        gridLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable("LayoutManager"));
        page = savedInstanceState.getInt("page");
        Log.d("onRestoreInstanceState", "onRestoreInstanceState called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            displayDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Sort by")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(R.array.options, selected,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, which + " selected");
                                if (which != selected) {
                                    selected = which;
                                    if (selected == 0)
                                        sortBy = getResources().getString(R.string.POPULARITY);
                                    else
                                        sortBy = getResources().getString(R.string.RATING);
                                    page = 1;
                                    arrayList.clear();
                                    fetch();
                                }
                                dialog.dismiss();
                            }
                        })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        dialog = builder.create();
        dialog.show();
    }
}
