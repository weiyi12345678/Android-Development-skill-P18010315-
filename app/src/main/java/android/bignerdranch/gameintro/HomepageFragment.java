package android.bignerdranch.gameintro;

import android.bignerdranch.gameintro.Models.Article;
import android.bignerdranch.gameintro.Models.News;
import android.bignerdranch.gameintro.api.ApiClient;
import android.bignerdranch.gameintro.api.ApiInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomepageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    //the news api key
    public static final String API_KEY = "3cce2fcba7d246cf946b6c9cd708c961";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private HomeAdapter adapter;
    private TextView topHeadLine;
    private SwipeRefreshLayout swipeRefreshLayout;

    private EditText etQuery;
    private Button btnSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_homepage, container, false);



        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //when swipe refresh function will happen
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView =(RecyclerView) rootView.findViewById(R.id.recyclerViewGameInfo);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new HomeAdapter(getActivity(),articles);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        etQuery = (EditText) rootView.findViewById(R.id.edit_search);
        btnSearch = (Button) rootView.findViewById(R.id.button_search);
        topHeadLine = (TextView) rootView.findViewById(R.id.text_top_headLines);

        //when click search button if search not equal "" send the search text to LoadNews() method else send "" which mean no search function occur
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //run when etQuery is not empty
                if(!etQuery.getText().toString().equals("")){
                    topHeadLine.setText("Search Results");
                    //pass the search word into LoadNews function
                    LoadNews(etQuery.getText().toString());
                }
                //run when etQuery is empty
                else{
                    topHeadLine.setText("Top HeadLines for technology and gaming");
                    //pass the empty string into LoadNews function
                    LoadNews("");
                }
            }
        });

        onLoadingSwipeRefresh("");


        return rootView;
    }

    public void LoadNews(String query){
        //the refresh button will appear
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        String country = Utils.getCountry();
        String category = "technology";

        //call news class
        Call<News> call;

        //if search is not equal "" getSpecificNews method will be call else getNews method will be call
        if(!etQuery.getText().toString().equals("")){
            call = apiInterface.getSpecificNews(query, API_KEY);
        }
        else{
            call = apiInterface.getNews(country, category,API_KEY);
        }
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if(response.isSuccessful() && response.body().getArticle() != null){
                    articles.clear();
                    articles = response.body().getArticle();
                    adapter = new HomeAdapter(getActivity(),articles);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                    initListener();
                    //refresh button disappear
                    swipeRefreshLayout.setRefreshing(false);
                }
                if(response.body().getArticle().size() == 0){
                    //refresh button disappear
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "No Result", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<News> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //for on item click
    private void initListener(){
        adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);

                Article article = articles.get(position);
                intent.putExtra("url",article.getUrl());
                intent.putExtra("title",article.getTitle());
                intent.putExtra("img",article.getUrlToImage());
                intent.putExtra("date",article.getPublishedAt());
                intent.putExtra("source", article.getSource().getName());
                intent.putExtra("author",article.getAuthor());

                startActivity(intent);

            }
        });
    }

    @Override
    public void onRefresh() {
        topHeadLine.setText("Top HeadLines for technology and gaming");
        etQuery.setText("");
        LoadNews("");
    }

    private void onLoadingSwipeRefresh(final String keyword){
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        LoadNews(keyword);
                    }
                }
        );
    }
}
