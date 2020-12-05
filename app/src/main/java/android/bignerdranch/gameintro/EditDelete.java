package android.bignerdranch.gameintro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class EditDelete extends AppCompatActivity implements GameInfoEditDeleteAdapter.onItemCLickListener {

    private RecyclerView mRecyclerView;
    private GameInfoEditDeleteAdapter mAdapter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference mDatabaseReference, commentRef, likeRef;
    private ValueEventListener mDBListener;
    private List<Insert> mInserts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete);

        //for back button
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button


        mRecyclerView = findViewById(R.id.recyclerViewGameInfoEditDelete);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(EditDelete.this));
        mAdapter = new GameInfoEditDeleteAdapter(EditDelete.this, mInserts);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar3);

        //refresh function
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout3);
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        onLoadingSwipeRefresh();
    }

    //loading the game info
    public void loadGameInfo(){
        mInserts = new ArrayList<>();

        mAdapter = new GameInfoEditDeleteAdapter(EditDelete.this, mInserts);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(EditDelete.this);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("gameInfo");

        mDBListener = mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mInserts.clear();

                //use for loop to pass data in firebase to insert insertSnapShot
                for(DataSnapshot insertSnapShot : snapshot.getChildren()){
                    Insert insert = insertSnapShot.getValue(Insert.class);
                    insert.setKey(insertSnapShot.getKey());
                    mInserts.add(insert);
                }

                mAdapter.notifyDataSetChanged();

                mProgressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditDelete.this,  error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        Toast.makeText(EditDelete.this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    //on click to go to edit game info page
    @Override
    public void onEditClick(int position) {
        Insert selectedItem = mInserts.get(position);
        String selectedKey = selectedItem.getKey();
        String game_name = selectedItem.getName();
        String description = selectedItem.getDescription();
        String date = selectedItem.getDate();
        String company = selectedItem.getCompany();
        String type = selectedItem.gettGame();
        String platform = selectedItem.gettPlatform();
        String imgUrl = selectedItem.getImageUrl();

        Intent intent = new Intent (EditDelete.this, EditGameInfo.class);
        intent.putExtra("key", selectedKey);
        intent.putExtra("name", game_name);
        intent.putExtra("desc", description);
        intent.putExtra("date", date);
        intent.putExtra("company", company);
        intent.putExtra("type", type);
        intent.putExtra("platform", platform);
        intent.putExtra("imgUrl", imgUrl);
        startActivity(intent);
    }

    //delete function
    @Override
    public void onDeleteClick(int position) {
        Insert selectedItem = mInserts.get(position);
        String selectedKey = selectedItem.getKey();

        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        commentRef = FirebaseDatabase.getInstance().getReference("Comment");
        likeRef = FirebaseDatabase.getInstance().getReference("likes");

        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(EditDelete.this);
        builder.setTitle("Are you sure you want to delete this game info ?");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabaseReference.child(selectedKey).removeValue();
                        commentRef.child(selectedKey).removeValue();
                        likeRef.child(selectedKey).removeValue();
                        Toast.makeText(EditDelete.this, "Successfully delete item !", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditDelete.this, "Fail to delete item !", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(EditDelete.this, "Delete process have been cancel !" , Toast.LENGTH_LONG).show();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    //refresh function
    public void onRefresh() {
        loadGameInfo();
    }

    //for swipe refresh function
    private void onLoadingSwipeRefresh(){
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        loadGameInfo();
                    }
                }
        );
    }

    public void onDestroy() {
        super.onDestroy();
        mDatabaseReference.removeEventListener(mDBListener);
    }

    //back button function
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
