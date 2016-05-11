package com.ms.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.ms.ks.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class BitmapUtils {
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public static Bitmap decodeFile(String path) {

        int orientation;

        try {

            if(path==null){

                return null;
            }
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 4;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            Bitmap bm = BitmapFactory.decodeFile(path,o2);


            Bitmap bitmap = bm;

            ExifInterface exif = new ExifInterface(path);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.e("orientation", "" + orientation);
            Matrix m=new Matrix();

            if((orientation==3)){

                m.postRotate(180);
                m.postScale((float)bm.getWidth(), (float)bm.getHeight());

//             if(m.preRotate(90)){

                bitmap = Bitmap.createBitmap(bm, 0, 0,bm.getWidth(),bm.getHeight(), m, true);
                return  bitmap;
            }
            else if(orientation==6){

                m.postRotate(90);

                bitmap = Bitmap.createBitmap(bm, 0, 0,bm.getWidth(),bm.getHeight(), m, true);
                return  bitmap;
            }

            else if(orientation==8){

                m.postRotate(270);

                bitmap = Bitmap.createBitmap(bm, 0, 0,bm.getWidth(),bm.getHeight(), m, true);
                return  bitmap;
            }
            return bitmap;
        }
        catch (Exception e) {
        }
        return null;
    }

    public static void savePic(final Context ctx, String picUrl, final String yourTitle) {
        final String saveFile = SysUtils.getPicSavePath(ctx, yourTitle);
        ImageRequest f = new ImageRequest(
                picUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        try {
                            File image = new File(saveFile);

                            if(image.exists()) {
                                image.delete();
                            }

                            //写到此文件
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            response.compress(Bitmap.CompressFormat.PNG, 0, bos);
                            byte[] bitmapdata = bos.toByteArray();

                            FileOutputStream out = new FileOutputStream(image);
                            out.write(bitmapdata);
                            out.close();

                            SysUtils.showSuccess("图片已保存到本地相册中");
                            SysUtils.addImageToGallery(saveFile, ctx);

                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                0,
                0,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        SysUtils.showError("图片下载失败");
                    }
                }
        );
        ((BaseActivity)ctx).executeRequest(f);
    }

}
