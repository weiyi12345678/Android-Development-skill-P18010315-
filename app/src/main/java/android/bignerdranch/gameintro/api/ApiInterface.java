package android.bignerdranch.gameintro.api;

import android.bignerdranch.gameintro.Models.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    //for default news
    @GET("top-headlines")
    Call<News> getNews(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    //for searching news
    @GET("everything")
    Call<News> getSpecificNews(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );
}
