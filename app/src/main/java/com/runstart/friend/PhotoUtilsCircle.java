//需要声明、申请访问文件的权限


package com.runstart.friend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 17-9-22.
 */
public class PhotoUtilsCircle {
    public static final int NONE = 0;
    public static final String IMAGE_UNSPECIFIED = "image/*";//任意图片类型
    public static final int PHOTOGRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果
    public static final int PICTURE_HEIGHT = 500;
    public static final int PICTURE_WIDTH = 500;
    public static String imageName;

    /**
     * 从系统相册中选取照片上传
     *
     * @param activity
     */
    public static void selectPictureFromAlbum(Activity activity) {
        // 调用系统的相册
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_UNSPECIFIED);

        // 调用剪切功能
        activity.startActivityForResult(intent, PHOTOZOOM);
    }

    /**
     * 从系统相册中选取照片上传
     *
     * @param fragment
     */
    public static void selectPictureFromAlbum(Fragment fragment) {
        // 调用系统的相册
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_UNSPECIFIED);

        // 调用剪切功能
        fragment.startActivityForResult(intent, PHOTOZOOM);
    }

    /**
     * 拍照
     *
     * @param activity
     */
    public static void photograph(Activity activity) {
        imageName = File.separator + getStringToday() + ".png";
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //String status = Environment.getExternalStorageState();
        /*if(status.equals(Environment.MEDIA_MOUNTED)){
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                    Environment.getExternalStorageDirectory(), imageName)));
        }else{
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                    activity.getFilesDir(), imageName)));
        }*/
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.DATA, Environment.getExternalStorageDirectory()+ File.separator + activity.getPackageName()+ File.separator+"myimages/" + imageName);
        Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, PHOTOGRAPH);
    }

    /**
     * 拍照
     *
     * @param fragment
     */
    public static void photograph(Fragment fragment) {
        imageName = "/" + getStringToday() + ".jpg";

        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                    Environment.getExternalStorageDirectory(), imageName)));
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                    fragment.getActivity().getFilesDir(), imageName)));
        }
        fragment.startActivityForResult(intent, PHOTOGRAPH);
    }

    /**
     * 图片裁剪
     *
     * @param activity
     * @param uri      原图的地址
     * @param height   指定的剪辑图片的高
     * @param width    指定的剪辑图片的宽
     * @param destUri  剪辑后的图片存放地址
     */
    public static void startPhotoZoom(Activity activity, Uri uri, int aspectX, int aspectY, int height, int width, Uri destUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", aspectX);
//        intent.putExtra("aspectY", aspectY);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputY", width);
        intent.putExtra("noFaceDetection", true); //关闭人脸检测
        intent.putExtra("return-data", false);//如果设为true则返回bitmap
        intent.putExtra(MediaStore.EXTRA_OUTPUT, destUri);//输出文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, PHOTORESOULT);
    }

    /**
     * 获取当前系统时间并格式化
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 制作图片的路径地址
     *
     * @param context
     * @return
     */
    public static String getPath(Context context) {
        String path = null;
        File file = null;
        long tag = System.currentTimeMillis();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //SDCard是否可用
            path = Environment.getExternalStorageDirectory() + File.separator  + context.getPackageName() + File.separator+"myimages/";
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            path = Environment.getExternalStorageDirectory() + File.separator  + context.getPackageName() + File.separator + "myimages/" + tag + ".png";
        } else {
            path = context.getFilesDir() + File.separator + context.getPackageName() + File.separator+"myimages/";
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            path = context.getFilesDir()+ File.separator + context.getPackageName() + File.separator + "myimages/" + tag + ".png";
        }
        return path;
    }

    static public String myPictureOnResultOperate(int requestCode, int resultCode, Intent data, Activity context) {
        String BxfPath = "";
        final String TAG = "database";
        if (resultCode == PhotoUtilsCircle.NONE) {
            return "0";
        }
        // /////////////////////////////////////////拍照///////////////////////////////////////////////
        if (requestCode == PhotoUtilsCircle.PHOTOGRAPH) {
            // 设置文件保存路径这里放在跟目录下
            File picture;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                picture = new File(Environment.getExternalStorageDirectory() + PhotoUtilsCircle.imageName);
                if (!picture.exists()) {
                    picture = new File(Environment.getExternalStorageDirectory() + File.separator + context.getPackageName() + File.separator+"myimages/"+ PhotoUtilsCircle.imageName);
                }
            } else {
                picture = new File(context.getFilesDir() + File.separator + context.getPackageName() + File.separator+"myimages/"+ PhotoUtilsCircle.imageName);
                if (!picture.exists()) {
                    picture = new File(context.getFilesDir() + PhotoUtilsCircle.imageName);
                }
            }

            BxfPath = PhotoUtilsCircle.getPath(context);// 生成一个地址用于存放剪辑后的图片
            if (TextUtils.isEmpty(BxfPath)) {
                Log.e(TAG, "随机生成的用于存放剪辑后的图片的地址失败");
                return "1";
            }
            Uri imageUri = Uri.fromFile(new File(BxfPath));

            WindowManager wm = context.getWindowManager();
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            PhotoUtilsCircle.startPhotoZoom(context, Uri.fromFile(picture), 328, 116, height, width, imageUri);
            return "bxf" + BxfPath;
        }
        if (data == null)
            return "4";
        ////////////////////////////// 读取相册缩放图片///////////////////////////////////////////////////////////
        if (requestCode == PhotoUtilsCircle.PHOTOZOOM) {
            BxfPath = PhotoUtilsCircle.getPath(context);// 生成一个地址用于存放剪辑后的图片
            if (TextUtils.isEmpty(BxfPath)) {
                Log.e(TAG, "随机生成的用于存放剪辑后的图片的地址失败");
                return "2";
            }
            Uri imageUri = Uri.fromFile(new File(BxfPath));
            WindowManager wm = context.getWindowManager();
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            PhotoUtilsCircle.startPhotoZoom(context, data.getData(), 328, 328, height, width, imageUri);
        }
        /////////////////////////////// 处理裁剪结果////////////////////////////////////////////////////////////////
        if (requestCode == PhotoUtilsCircle.PHOTORESOULT) {
            /**
             * 在这里处理剪辑结果，可以获取缩略图，获取剪辑图片的地址。得到这些信息可以选则用于上传图片等等操作
             * */

            /**
             * 如，根据path获取剪辑后的图片
             */
            return "3";
        }
        return "bxf" + BxfPath;
    }

    static public void showImage(ImageView imageView, String path) {
        File file = new File(path);
        if (file.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bm);
        }
    }

    static public void options(final Activity activity) {
        new AlertDialog.Builder(activity).setTitle("选择").setCancelable(false)
                .setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            try {
                                PhotoUtilsCircle.photograph(activity);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            PhotoUtilsCircle.selectPictureFromAlbum(activity);
                        }
                    }
                }).show();
    }
}
