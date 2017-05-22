package com.example.tsha.myapplication.network;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by ts.ha on 2017-04-24.
 */
public interface ItemDetailApi {
    @Multipart
    @POST("post.php?dir=example")
    Call<ResponseBody> upload(@Part("description") RequestBody description,
                              @Part  MultipartBody.Part file);

}
