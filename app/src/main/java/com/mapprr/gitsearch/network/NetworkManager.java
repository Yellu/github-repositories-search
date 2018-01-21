package com.mapprr.gitsearch.network;

import android.content.Context;

import com.ihsanbal.logging.LoggingInterceptor;
import com.mapprr.gitsearch.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.internal.platform.Platform;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by appigizer on 17/1/18.
 */

public class NetworkManager {
    private String baseUrl;
    private static NetworkManager manager = null;

    private NetworkManager(){
        baseUrl = "https://api.github.com/";
    }

    public static NetworkManager getInstance(){
        if (manager == null){
            manager = new NetworkManager();
        }
        return manager;
    }

    private interface GitSearchApiClient{
        @GET("search/repositories")
        Call<ResponseBody> getRepos(@Query("q") String searchKey);
    }

    public static LoggingInterceptor provideOkHttpLogging(){
        return new LoggingInterceptor.Builder()
                .loggable(BuildConfig.DEBUG)
                .log(Platform.INFO)
                .request("Request")
                .response("Response")
                .build();
    }

    private <T> T createService(Class<T> service, Context context){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

//        builder.retryOnConnectionFailure(true);
//        builder.readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS);
//        if (context != null){
//            builder.addInterceptor(new AddCookiesInterceptor(context));
//            builder.addInterceptor(new ReceivedCookiesInterceptor(context));
//        }

        builder.addInterceptor(provideOkHttpLogging());

        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(service);
    }

    private GitSearchApiClient getService(Context context){
        return createService(GitSearchApiClient.class, context);
    }

    public Call<ResponseBody> searchRequest(Context context, String query){
        return getService(context).getRepos(query);
    }


}
