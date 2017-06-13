package info.androidhive.recyclerview;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    ;
    private RecyclerView recyclerView;
    private PlaceAdapter mAdapter;
    public static String imageDirPath;
    public static File directory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        directory = cw.getDir("imageDir", Context.MODE_APPEND);
        imageDirPath = directory.getAbsolutePath();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        //recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new PlaceAdapter(getBaseContext(),(LinearLayoutManager)mLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(MainActivity.this,AddPlace.class);
        startActivityForResult(i,1);
        Log.v("cameback","");
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            Log.v("name1", data.getStringExtra("name"));
            mAdapter.notifyDataSetChanged();
        }
    }
}
