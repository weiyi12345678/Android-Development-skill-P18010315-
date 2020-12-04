package android.bignerdranch.gameintro.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient implements Serializable {

    public static final String BASE_URL = "https://newsapi.org/v2/";
    public static ApiClient apiClient;
    public static Retrofit retrofit;

    //to get the usl of news
    public static Retrofit getApiClient (){

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        return retrofit;
    }


    public static synchronized ApiClient getInstance(){
        if(apiClient == null){
            apiClient = new ApiClient();
        }
        return apiClient;
    }

    public static ApiInterface getApi(){
        return retrofit.create(ApiInterface.class);
    }


}
