package android.bignerdranch.gameintro;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class GameInfoFragment extends Fragment implements  GameInfoAdapter.OnGameInfoListener {

    private static final String TAG = "GameInfoFragment";
    private RecyclerView mRecyclerView;
    private GameInfoAdapter mAdapter, adapter;
    private ProgressBar mProgressBar;
    private SearchView searchView;
    private TextView headline;

    private SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseReference;
    private List<Insert> mInserts = new ArrayList<>();
    private List<Insert> list;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game_info, container, false);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("gameInfo");

        headline = (TextView) rootView.findViewById(R.id.text_top_headLines);

        mRecyclerView = rootView.findViewById(R.id.recyclerViewGameInfo);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new GameInfoAdapter(getActivity(), mInserts);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);


        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar2);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout2);
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


        searchView = rootView.findViewById(R.id.searchView);

        onLoadingSwipeRefresh();
        setSearchViewListener();




        return rootView;
    }

    public void loadGameInfo(){

        swipeRefreshLayout.setRefreshing(true);
        mInserts = new ArrayList<>();
        mStorage =FirebaseStorage.getInstance();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot insertSnapShot : snapshot.getChildren()){
                    Insert insert = insertSnapShot.getValue(Insert.class);
                    insert.setKey(insertSnapShot.getKey());
                    mInserts.add(insert);
                }

                mAdapter = new GameInfoAdapter(getActivity(), mInserts);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnGameInfoListener(GameInfoFragment.this);

                mProgressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),  error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    protected void setSearchViewListener() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s != null) {

                    search(s);
                    headline.setText("search result (Click comment icon for game info detail)");

                }
                return true;
            }

        });
    }

    public void search(String str){
        list = new ArrayList<>();
        for(Insert object : mInserts)
        {
            if(object.getName().toLowerCase().contains(str.toLowerCase()))
            {
                list.add(object);
            }
        }
        adapter = new GameInfoAdapter(getActivity(), list);
        mRecyclerView.setAdapter(adapter);
    }

    public void onRefresh() {
        headline.setText("Game Information");
        searchView.setQuery("",true);
        loadGameInfo();
    }

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

    @Override
    public void onDetailClick(int position) {

        String selectedKey, game_name, description, date, company, type, platform, imgUrl;
        Insert selectedItem = mInserts.get(position);


        selectedKey = selectedItem.getKey();
        game_name = selectedItem.getName();
        description = selectedItem.getDescription();
        date = selectedItem.getDate();
        company = selectedItem.getCompany();
        type = selectedItem.gettGame();
        platform = selectedItem.gettPlatform();
        imgUrl = selectedItem.getImageUrl();


        Intent intent = new Intent (getActivity(), GameInfoDetail.class);
        intent.putExtra("keyGameInfo", selectedKey);
        intent.putExtra("nameGameInfo", game_name);
        intent.putExtra("descGameInfo", description);
        intent.putExtra("dateGameInfo", date);
        intent.putExtra("companyGameInfo", company);
        intent.putExtra("typeGameInfo", type);
        intent.putExtra("platformGameInfo", platform);
        intent.putExtra("imgUrlGameInfo", imgUrl);
        startActivity(intent);


    }


}
