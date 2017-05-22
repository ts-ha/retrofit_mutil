package com.example.tsha.myapplication;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    public static Bitmap masking(Resources res, int originalResId, int maskResId) {
        Bitmap original = BitmapFactory.decodeResource(res, originalResId);
        Bitmap mask = BitmapFactory.decodeResource(res, maskResId);

        int width = Math.max(original.getWidth(), mask.getWidth());
        int height = Math.max(original.getHeight(), mask.getHeight());

        Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        float left = (float) (width - original.getWidth()) / 2;
        float top = (float) (height - original.getHeight()) / 2;

        canvas.drawBitmap(original, left, top, null);

        left = (float) (width - mask.getWidth()) / 2;
        top = (float) (height - mask.getHeight()) / 2;

        canvas.drawBitmap(mask, left, top, paint);

        return result;
    }

    public static Bitmap masking(Resources res, BitmapDrawable orginalDrawable, int maskResId) {
        Bitmap mask = BitmapFactory.decodeResource(res, maskResId);

        int width = Math.max(orginalDrawable.getIntrinsicWidth(), mask.getWidth());
        int height = Math.max(orginalDrawable.getIntrinsicHeight(), mask.getHeight());

        Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        float left = (float) (width - orginalDrawable.getIntrinsicWidth()) / 2;
        float top = (float) (height - orginalDrawable.getIntrinsicHeight()) / 2;

        orginalDrawable.setBounds(0, 0, orginalDrawable.getIntrinsicWidth(), orginalDrawable.getIntrinsicHeight());
        canvas.drawBitmap(orginalDrawable.getBitmap(), left, top, null);

        left = (float) (width - mask.getWidth()) / 2;
        top = (float) (height - mask.getHeight()) / 2;

        canvas.drawBitmap(mask, left, top, paint);

        return result;
    }

    public static Point testImageSize(String filePath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        Point size = new Point();
        size.x = bmOptions.outWidth;
        size.y = bmOptions.outHeight;
        return size;
    }

    public static File saveTo(Bitmap bitmap, File to) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(to);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            return to;
        } catch (IOException e) {
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static Bitmap scale(ContentResolver resolver, Uri uri, int targetWidth, int targetHeight) {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            InputStream input = resolver.openInputStream(uri);
            BitmapFactory.decodeStream(input, null, bmOptions);
            int w = bmOptions.outWidth;
            int h = bmOptions.outHeight;

            int scaleFactor = Math.min(w / targetWidth, h / targetHeight);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            input = resolver.openInputStream(uri);
            return BitmapFactory.decodeStream(input, null, bmOptions);
        } catch (IOException e) {
            return null;
        }
    }

    public static Bitmap scale(String filePath, int targetWidth, int targetHeight) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int w = bmOptions.outWidth;
        int h = bmOptions.outHeight;

        int scaleFactor = Math.min(w / targetWidth, h / targetHeight);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        return BitmapFactory.decodeFile(filePath, bmOptions);
    }

    public static Bitmap rotateBitmap(String filePath, Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);


            Matrix mat = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    mat.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    mat.setRotate(180);
                    mat.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    mat.setRotate(90);
                    mat.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    mat.setRotate(-90);
                    mat.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mat.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mat.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    mat.postRotate(-90);
                    break;
                default:
                    break;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        } catch (IOException e) {
            return bitmap;
        }
    }

    public static boolean isValidImage(String filePath) {
        Bitmap test = scale(filePath, 10, 10);
        if (test != null) {
            test.recycle();
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValidImage(ContentResolver resolver, Uri uri) {
        Bitmap test = scale(resolver, uri, 10, 10);
        if (test != null) {
            test.recycle();
            return true;
        } else {
            return false;
        }
    }
}
