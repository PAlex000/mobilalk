package com.music_share;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MusicShareActivity extends AppCompatActivity {
    private static final String LOG_TAG = MusicShareActivity.class.getName();

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private TextView countTextView;

    private RecyclerView mRecyclerView;
    private ArrayList<SongItem> mSongList;
    private MusicItemAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private boolean viewRow = true;
    private int gridNumber = 1;

    private NotificationHelper mNotificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_share);
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "UnAuthenticated user!");
            finish();
        }


        mRecyclerView = findViewById(R.id.recyclerViewer);
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));
        mSongList = new ArrayList<>();

        mAdapter = new MusicItemAdapter(this, mSongList);

        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");
        queryData();

        mNotificationHelper = new NotificationHelper(this);
    }

    private void initializeData() {
        String[] itemTitle = getResources().getStringArray(R.array.music_item_title);
        String[] itemDescription = getResources().getStringArray(R.array.music_item_desc);
        String[] itemUrl = getResources().getStringArray(R.array.music_item_url);

        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.music_item_img);

        for (int i = 0; i < itemTitle.length; i++) {
            mItems.add(new SongItem(
                    itemTitle[i],
                    itemDescription[i],
                    itemUrl[i],
                    itemsImageResource.getResourceId(i, 0)));
        }
        itemsImageResource.recycle();
    }

    private void queryData() {
        mSongList.clear();

        mItems.orderBy("title").limit(10).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        SongItem item = document.toObject(SongItem.class);
                        item.setId(document.getId());
                        mSongList.add(item);
                    }
                    if (mSongList.size() == 0) {

                        initializeData();
                        queryData();
                    }
                    mAdapter.notifyDataSetChanged();
                });

    }

    public void deleteFile(SongItem item) {
        DocumentReference ref = mItems.document(item._getId());

        ref.delete()
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Item is successfully deleted: " + item._getId());
                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item: " + item._getId() + "Cannot be deleted", Toast.LENGTH_LONG).show();
                });
        mNotificationHelper.send(item.getTitle());
        queryData();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.music_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.settings_button:
                Log.d(LOG_TAG, "Setting clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.view_selector:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }
}