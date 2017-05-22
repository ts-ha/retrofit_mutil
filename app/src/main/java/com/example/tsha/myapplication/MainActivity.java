package com.example.tsha.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.tsha.myapplication.network.ItemDetailManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;

public class MainActivity extends FragmentActivity implements View.OnClickListener, SmartServiceMessageHandler.SmartServiceHandlerInterface {
    private static final String TAG = "MainActivity";
    private ImageButton mImageBtn;
    private ImageButton mBtnQr;
    private SmartServiceMessageHandler handler = new SmartServiceMessageHandler(this);

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageBtn = (ImageButton) findViewById(R.id.image_btn);
        mImageBtn.setOnClickListener(this);

        mBtnQr = (ImageButton) findViewById(R.id.btnQr);
        mBtnQr.setOnClickListener(this);


        verifyStoragePermissions(this);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private static final int REQUEST_PICK_IMAGE = 1;

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.image_btn) {
            File targetFile = new File(getFilesDir(), "profile_test.jpg");
            int targetWidth = mImageBtn.getWidth();
            int targetHeight = mImageBtn.getHeight();

            Intent pickImageIntent = new Intent(this, PickImageActivity.class);

            pickImageIntent.putExtra(PickImageActivity.EXTRA_TARGET_FILE, targetFile);

            pickImageIntent.putExtra(PickImageActivity.EXTRA_TARGET_WIDTH, targetWidth);
            pickImageIntent.putExtra(PickImageActivity.EXTRA_TARGET_HEIGHT, targetHeight);
            startActivityForResult(pickImageIntent, REQUEST_PICK_IMAGE);
        } else if (id == R.id.btnQr) {

            IntentIntegrator intent = new IntentIntegrator(MainActivity.this);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            intent.addExtra("SCAN_WIDTH", dm.heightPixels);
            intent.addExtra("SCAN_HEIGHT", dm.widthPixels / 4);
            intent.addExtra("SCAN_MODE", "ONE_D_MODE");
            intent.setBarcodeImageEnabled(true).initiateScan();
        }


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "InfoFragment onActivityResult  ~~~~");
                File imageFile = (File) data.getSerializableExtra(PickImageActivity.EXTRA_IMAGE_FILE);
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                mImageBtn.setImageBitmap(bitmap);
            }
        } else if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            Log.i(TAG, ">>> result.getContents()   :  " + result.getContents());
            Log.i(TAG, ">>> result.getFormatName()   :  " + result.getFormatName());
            Log.i(TAG, ">>> result.getBarcodeImagePath()   :  " + result.getBarcodeImagePath());


            Log.d(TAG, "onActivityResult: " + this.getApplicationContext().getPackageName());


            Uri photoURI = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(result.getBarcodeImagePath()));
            ItemDetailManager itemDetailManager = new ItemDetailManager();


            itemDetailManager.uploadFile(MainActivity.this, photoURI);
            Bitmap myBitmap = BitmapFactory.decodeFile(result.getBarcodeImagePath());
            mBtnQr.setImageBitmap(myBitmap);
        } else {
            Log.d(TAG, "InfoFragment onActivityResult  else ~~~~");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void handleServiceMessage(Message message) {

    }
}
