package com.example.tsha.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;


import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PickImageActivity extends FragmentActivity {

    private static final String TAG = "PickImageActivity";

    public static final String EXTRA_TARGET_FILE = "extra.TARGET_FILE";
    public static final String EXTRA_TARGET_WIDTH = "extra.TARGET_WIDTH";
    public static final String EXTRA_TARGET_HEIGHT = "extra.TARGET_HEIGHT";
    public static final String EXTRA_IMAGE_FILE = "extra.IMAGE_FILE";
    public static final String EXTRA_ERROR = "extra.ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Intent data = getIntent();

            File targetFile = (File) data.getSerializableExtra(EXTRA_TARGET_FILE);
            int targetWidth = data.getIntExtra(EXTRA_TARGET_WIDTH, 400);
            int targetHeight = data.getIntExtra(EXTRA_TARGET_HEIGHT, 400);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = PickImageFragment.newInstance(targetFile, targetWidth, targetHeight);
            ft.add(fragment, "pick_image").commit();
        }
    }

    public void onPickImageSuccess(File file) {

        Intent result = getIntent();
        result.putExtra(EXTRA_IMAGE_FILE, file);

        setResult(RESULT_OK, result);
    }

    public void onPickImageFailure() {
        Intent result = getIntent();
        result.putExtra(EXTRA_ERROR, true);

        setResult(RESULT_CANCELED, result);
    }

    public static class PickImageFragment extends Fragment {

        private static final int REQUEST_CAPTURE_IMAGE = 1;
        private static final int REQUEST_CROP_IMAGE = 2;
        private static final int REQUEST_PICK_IMAGE = 3;

        private static final String EXTRA_PICK_IMAGE_FILE = "extra.PICK_IMAGE_FILE";
        private static final String EXTRA_CROP_IMAGE_FILE = "extra.CROP_IMAGE_FILE";
        private static final String EXTRA_TEMP_IMAGE_FILE = "extra.TEMP_IMAGE_FILE";

        private File mTargetFile;
        private int mTargetWidth;
        private int mTargetHeight;
        private File mPickImageFile;
        private File mCropImageFile;
        private File mTempImageFile;

        public static PickImageFragment newInstance(File targetFile, int targetWidth, int targetHeight) {
            PickImageFragment fragment = new PickImageFragment();
            Bundle args = new Bundle();
            args.putSerializable(EXTRA_TARGET_FILE, targetFile);
            args.putInt(EXTRA_TARGET_WIDTH, targetWidth);
            args.putInt(EXTRA_TARGET_HEIGHT, targetHeight);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (savedInstanceState == null) {
                Bundle args = getArguments();
                mTargetFile = (File) args.getSerializable(EXTRA_TARGET_FILE);
                mTargetWidth = args.getInt(EXTRA_TARGET_WIDTH);
                mTargetHeight = args.getInt(EXTRA_TARGET_HEIGHT);
            } else {
                mTargetFile = (File) savedInstanceState.getSerializable(EXTRA_TARGET_FILE);
                mTargetWidth = savedInstanceState.getInt(EXTRA_TARGET_WIDTH);
                mTargetHeight = savedInstanceState.getInt(EXTRA_TARGET_HEIGHT);
                mPickImageFile = (File) savedInstanceState.getSerializable(EXTRA_PICK_IMAGE_FILE);
                mCropImageFile = (File) savedInstanceState.getSerializable(EXTRA_CROP_IMAGE_FILE);
                mTempImageFile = (File) savedInstanceState.getSerializable(EXTRA_TEMP_IMAGE_FILE);
            }

        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putSerializable(EXTRA_TARGET_FILE, mTargetFile);
            outState.putInt(EXTRA_TARGET_WIDTH, mTargetWidth);
            outState.putInt(EXTRA_TARGET_HEIGHT, mTargetHeight);
            outState.putSerializable(EXTRA_PICK_IMAGE_FILE, mPickImageFile);
            outState.putSerializable(EXTRA_CROP_IMAGE_FILE, mCropImageFile);
            outState.putSerializable(EXTRA_TEMP_IMAGE_FILE, mTempImageFile);

            super.onSaveInstanceState(outState);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (savedInstanceState == null) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                Fragment fragment = new ImagePickerDialogFragment();
                ft.add(fragment, "image-picker-dialog").commit();
            }
        }

        public static class ImagePickerDialogFragment extends DialogFragment {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final CharSequence[] items = {
                        getString(R.string.pick_image_camera),
                        getString(R.string.pick_image_gallery)
                };

                return new CustomAlertDialog.Builder(getActivity())
                        .setTitle(R.string.pick_image_title)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    default:
                                    case 0: /* camera */
                                        ((PickImageFragment) getParentFragment()).requestCapture();
                                        break;
                                    case 1: /* gallery */
                                        ((PickImageFragment) getParentFragment()).requestPick();
                                        break;
                                }
                            }
                        })
                        .setNeutralButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((PickImageActivity) getActivity()).onPickImageFailure();

                                ((PickImageFragment) getParentFragment()).canceled();
                            }
                        })
                        .create();
            }

            @Override
            public void onCancel(DialogInterface dialog) {
                super.onCancel(dialog);

                ((PickImageFragment) getParentFragment()).canceled();
            }

        }

        private void canceled() {
            finish();
        }

        private void finish() {
            if (mTempImageFile != null && mTempImageFile.exists()) {
                if (mTempImageFile.delete()) {
                }
            }

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(PickImageFragment.this).commit();

            getActivity().finish();
        }

        private void requestCapture() {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".jpg";
            mPickImageFile = new File(dir, name);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", mPickImageFile));
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
            } else {
                ((PickImageActivity) getActivity()).onPickImageFailure();
            }
        }

        private void requestPick() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_PICK_IMAGE);
            } else {
                ((PickImageActivity) getActivity()).onPickImageFailure();
            }
        }

        private boolean requestCrop(File file) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", file), "image/*");
            intent.putExtra("outputX", mTargetWidth);
            intent.putExtra("outputY", mTargetHeight);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            mCropImageFile = new File(file.getParentFile(), "crop_" + file.getName());
            intent.putExtra("output", FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", mCropImageFile));
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CROP_IMAGE);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_CAPTURE_IMAGE) {

                onCaptureImageResult(resultCode);

            } else if (requestCode == REQUEST_PICK_IMAGE) {
                onPickImageResult(resultCode, data);
            }
//            else if (requestCode == REQUEST_CROP_IMAGE) {
//                onCropImageResult(resultCode);
//            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        private void onCaptureImageResult(int resultCode) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onCaptureImageResult: " + mPickImageFile.getPath());
                FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", mPickImageFile);

                saveImageToTargetFile(mPickImageFile.getAbsolutePath());

                ((PickImageActivity) getActivity()).onPickImageSuccess(mTargetFile);
                finish();
//                Intent mediaScan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                mediaScan.setData(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", mPickImageFile));
//
//                getActivity().sendBroadcast(mediaScan);
            } else {
                ((PickImageActivity) getActivity()).onPickImageFailure();
                finish();
            }
        }

        private void onPickImageResult(int resultCode, Intent data) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri uri = data.getData();
                String filePath = getPickImageFilePath(uri);

                if (filePath != null) {

                        Logger.getLogger(TAG).warn("no need to crop image");
                        saveImageToTargetFile(filePath);

                        ((PickImageActivity) getActivity()).onPickImageSuccess(mTargetFile);
                        finish();
                } else {
                    Logger.getLogger(TAG).warn("else");
                    ((PickImageActivity) getActivity()).onPickImageFailure();
                    finish();
                }
            } else {
                Logger.getLogger(TAG).warn("else");
                ((PickImageActivity) getActivity()).onPickImageFailure();
                finish();
            }
        }


        private String getPickImageFilePath(Uri uri) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            if (ImageUtils.isValidImage(filePath)) {
                Logger.getLogger(TAG).debug("image path=" + filePath);
                return filePath;
            } else {
                if (ImageUtils.isValidImage(getActivity().getContentResolver(), uri)) {
                    // copy to local storage
                    InputStream input = null;
                    OutputStream output = null;
                    try {
                        mTempImageFile = File.createTempFile("temp_", ".jpg",
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
                        filePath = mTempImageFile.getAbsolutePath();
                        input = getActivity().getContentResolver().openInputStream(uri);
                        output = new FileOutputStream(mTempImageFile);
                        byte buffer[] = new byte[1024];
                        int read;
                        while ((read = input.read(buffer)) > 0) {
                            output.write(buffer, 0, read);
                        }
                        output.flush();
                        Logger.getLogger(TAG).debug("image path=" + filePath);
                        return filePath;
                    } catch (IOException e) {
                        Logger.getLogger(TAG).warn("failed to copy image");
                    } finally {
                        if (output != null) {
                            try {
                                output.close();
                            } catch (IOException e) {
                                Logger.getLogger(TAG).warn("cannot close output stream");
                            }
                        }
                        if (input != null) {
                            try {
                                input.close();
                            } catch (IOException e) {
                                Logger.getLogger(TAG).warn("cannot close input stream");
                            }
                        }
                    }
                }
            }
            return null;
        }

        private void saveImageToTargetFile(String filePath) {
            Log.d(TAG, "saveImageToTargetFile: " + filePath.toString());
            Bitmap bitmap = ImageUtils.scale(filePath, mTargetWidth, mTargetHeight);
            bitmap = ImageUtils.rotateBitmap(filePath, bitmap);
            ImageUtils.saveTo(bitmap, mTargetFile);
            bitmap.recycle();
        }
    }
}
