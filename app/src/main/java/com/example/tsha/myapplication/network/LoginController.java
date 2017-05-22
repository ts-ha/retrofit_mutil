package com.example.tsha.myapplication.network;

import android.content.Context;
import android.util.Log;

import com.example.tsha.myapplication.R;
import com.example.tsha.myapplication.modle.Feature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ts.ha on 2017-04-24.
 */

public class LoginController implements Callback<Feature> {

    final static String BASE_URL = "https://apis.skplanetx.com/tmap/";
    //    final static String BASE_URL = NetConst.host + "login/appLogin.json/";
    final static String TAG = "LoginController";

    public LoginController(Context mContext) {
        this.mContext = mContext;
    }

    private Context mContext;

    public void start() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AddTMapKeyInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(okHttpClient)
                .build();


        RetrofitApi gerritAPI = retrofit.create(RetrofitApi.class);


//        Log.d(TAG, "client.cookieJar().toString() : " + okHttpClient.cookieJar().toString());

//        Log.d(TAG, "start: admin_id: " + admin_id + "\t admin_pwd : " + admin_pwd + "\t admin_telno : " + admin_telno
//                + "\t version : " + version);

        String endX = "129.07579349764512";
        String endY = "35.17883196265564";
        String startX = "126.98217734415019";
        String startY = "37.56468648536046";
        Call<Feature> data = gerritAPI.login(endX, endY, startX, startY);


        data.enqueue(this);
    }


    public class AddTMapKeyInterceptor implements Interceptor {
        private static final String TAG = "AddTMapKeyInterceptor";

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();

            builder.removeHeader("User-Agent").addHeader("appKey", mContext.getResources().getString(R.string.tmap_key));
            return chain.proceed(builder.build());


        }
    }

    @Override
    public void onResponse(Call<Feature> call, Response<Feature> response) {
        if (response.isSuccessful()) {
            Log.d(TAG, "onResponse: " + response.toString());
            Log.d(TAG, "onResponse: " + response.body().toString());
//            Features user = response.body();
//            Log.d(TAG, "onResponse: getSID " + user.getProperties().getTotalDistance());
//            Auth.login(user);
//            Log.d(TAG, "Auth : " + Auth.getSid());
        } else {
            Log.d(TAG, "요청 실패");
        }

    }

    @Override
    public void onFailure(Call<Feature> call, Throwable t) {
        Log.d(TAG, "요청 실패");
        t.printStackTrace();
    }


}
