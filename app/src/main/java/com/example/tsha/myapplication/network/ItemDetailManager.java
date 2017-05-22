package com.example.tsha.myapplication.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.apache.log4j.lf5.util.StreamUtils.getBytes;


/**
 * Created by ts.ha on 2017-05-19.
 */

public class ItemDetailManager {


    private static final String TAG = "ItemDetailManager";


    public void uploadFile(Context context, Uri fileUri) {
        // create upload service client
        ItemDetailApi service =
                ServiceGenerator.createService(ItemDetailApi.class);

        File file = new File(fileUri.getPath());

        Log.e(TAG, "getPath: " + file.getPath());
        Log.e(TAG, "getName: " + file.getName());

        InputStream iStream = null;
        byte[] inputData = new byte[0];
        try {
            iStream = context.getContentResolver().openInputStream(fileUri);
            inputData = getBytes(iStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "length uploadFile: " + inputData.length);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), inputData);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");


        Call<ResponseBody> call = service.upload(name, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
                Log.e(TAG, "onResponse: " + response.message());
                Log.e(TAG, "onResponse: " + response.toString());
                Log.e(TAG, "onResponse: " + response.body().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }


}
