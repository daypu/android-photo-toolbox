package com.example.change_back;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.show.api.ShowApiRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import util.Img2base64;

public class change_clothes extends AppCompatActivity {
    private Bitmap bitmap = null;

    private Bitmap bitmap1 = null;

    private ImageView imageView = null;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private String[] spinnerOptions = {"c1", "c8", "w6", "w7", "m1","m4"};

    private String selection = "c1";

    private Spinner spinnerModel;

    private ImageView save_img;

    private Bitmap targetImage = null;

    private Bitmap styledImage = null;

    private String base64_code = null;

    private Button btnTakePhoto;

    private File outputImage;

    private Uri imageUri;

    private static final int takePhoto = 3;

    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public String selectedImagePath = null;
    //启动图像选择器，调用onActivityResult
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        //// 加载底部导航栏的 Fragment
        //getSupportFragmentManager().beginTransaction()
        //        .replace(R.id.fragment_container, new BottomNavigationFragment())
        //        .commit();

        verifyStoragePermissions(this);

        //final TextView txt = findViewById(R.id.textView1);
        Button myBtn = findViewById(R.id.button1);
        imageView = findViewById(R.id.imageView1);

        myBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (base64_code != null) {
                    performApiCall(String.valueOf(base64_code));
                }
                else {
//                    Toast.makeText(this, "b64返回空值", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button myBtn1 = findViewById(R.id.xuanzetupian);

        myBtn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 启动图像选择器
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);

            }
        });
        spinnerModel = (Spinner) findViewById(R.id.spinnerModel);
        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
            {
                selection = spinnerOptions[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });

        save_img = findViewById(R.id.saveimg);
        save_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (targetImage != null)
                {
                    String path = FileSaveToInside(change_clothes.this,"result.jpg",bitmap1);
                    Toast.makeText(change_clothes.this, path, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(change_clothes.this,"无图片", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnTakePhoto = findViewById(R.id.photo);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        //  创建File对象，用于存储拍照后的图片,命名为output_image.jdp
                        //  存放在手机SD卡的应用关联缓存目录下
                        outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                        try {
                            outputImage.createNewFile();
                            //  如果运行设备的系统高于Android 7.0
                            //  就调用FileProvider的getUriForFile()方法将File对象转换成一个封装过的Uri对象。
                            //  该方法接收3个参数：Context对象， 任意唯一的字符串， 创建的File对象。
                            //  这样做的原因：Android 7.0 开始，直接使用本地真实路径的Uri是被认为是不安全的，会抛出FileUriExposedException异常；
                            //      而FileProvider是一种特殊的ContentProvider，他使用了和ContentProvider类似的机制对数据进行保护，可以选择性地将封装过的Uri共享给外部。
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                imageUri = FileProvider.getUriForFile(change_clothes.this, "com.example.permissiontest.fileprovider", outputImage);
                            } else {
                                //  否则，就调用Uri的fromFile()方法将File对象转换成Uri对象
                                imageUri = Uri.fromFile(outputImage);
                            }
                            //  启动相机
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            //  指定图片的输出地址,这样拍下的照片会被输出到output_image.jpg中。
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, takePhoto);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }

        });
    }
    //转换b64编码，调用api:performApiCall
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                // 将选中的图像转换为Base64编码
                selectedImagePath = getPathFromUri(selectedImageUri);
                base64_code = Img2base64.img2base64(selectedImagePath);
                //Toast.makeText(this, "base64:" + base64_code, Toast.LENGTH_LONG).show();
                bitmap = decodeUri(selectedImageUri);
                targetImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                imageView.setImageBitmap(bitmap);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    //新线程调用api,主线程更新UI,加载图片
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    public String ret_code;
    public void showToastForRetCode() {
        final String errorMessage = getErrorMessageForRetCode(ret_code);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(errorMessage);
            }
        });
    }

    private void performApiCall(final String base64_code) {
        // 在新线程中执行API调用
        new Thread() {
            public void run() {
                try {
                    String appid = "1573578"; // 替换成你的App ID
                    String secret = "854551406f2f4e329d102b2bd17175e2"; // 替换成你的Secret
                    final String res = new ShowApiRequest("http://route.showapi.com/2673-19", appid, secret)
                            .addTextPara("img_base64", base64_code)
//                            .addTextPara("img_url", "")
                            .addTextPara("color", "")
                            .addTextPara("clothes_num", selection)
                            .addTextPara("code", "1350")
                            .post();
                    // 解析 JSON 并获取 ret_code 字段的值
                    ret_code = parseRetcodeFromJson(res);
                    showToastForRetCode();
                    //getErrorMessageForRetCode(ret_code);
                    // 解析 JSON 并获取 order 字段的值
                    String order = parseOrderFromJson(res);

                    final String urll = new ShowApiRequest("http://route.showapi.com/2673-20", appid, secret)
                            .addTextPara("order", order)
                            .post();

                    String final_url = parseUrlFromJson(urll);

                    //// 在主线程更新UI
                    //runOnUiThread(new Runnable() {
                    //    public void run() {
                    //        TextView txt = findViewById(R.id.textView1);
                    //        txt.setText(final_url);
                    //    }
                    //});

                    // 加载图片并在主线程更新UI
                    bitmap1 = loadImageFromUrl(final_url);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ImageView imageView = findViewById(R.id.imageView1);
                            if (bitmap1 != null) {
                                imageView.setImageBitmap(bitmap1);
                            } else {
                                //TextView txt = findViewById(R.id.textView1);
                                //txt.setText("Failed to load image");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    //从uri获得本地图片路径
    private String getPathFromUri(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        android.database.Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
    //关于授权
    public void verifyStoragePermissions(AppCompatActivity activity) {
        int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
    //关于授权
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，可以执行相关操作
            } else {
                // 权限被拒绝，可以给用户一些提示或者做其他处理
            }
        }
    }
    //从showapi_res_body获取order
    private String parseOrderFromJson(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();

        // 获取 "showapi_res_body" 下的 "order" 字段的值
        JsonObject resBody = jsonObject.getAsJsonObject("showapi_res_body");
        if (resBody != null && resBody.has("order")) {
            return resBody.get("order").getAsString();
        } else {
            return "未找到 order 字段";
        }
    }
    private String parseRetcodeFromJson(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();

        // 获取 "showapi_res_body" 下的 "ret_code" 字段的值
        JsonObject resBody = jsonObject.getAsJsonObject("showapi_res_body");
        if (resBody != null && resBody.has("ret_code")) {
            return resBody.get("ret_code").getAsString();
        } else {
            return "未找到 ret_code 字段";
        }
    }

    // 从showapi_res_body获取url
    private String parseUrlFromJson(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();

        // 获取 "showapi_res_body" 下的 "url" 字段的值
        JsonObject resBody = jsonObject.getAsJsonObject("showapi_res_body");
        if (resBody != null && resBody.has("url")) {
            return resBody.get("url").getAsString();
        } else {
            return "未找到 url 字段";
        }
    }
    //从url获取bitmap对象
    private Bitmap loadImageFromUrl(String imageUrl) {
        Bitmap imageBitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            imageBitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageBitmap;
    }
    public String getErrorMessageForRetCode(String jsonResult) {
        String errorMessage = "";

        switch (jsonResult) {
            case "0":
                // 无异常
                errorMessage = "执行成功";
                break;
            case "10":
                // 参数错误
                errorMessage = "参数错误";
                break;
            case "20":
                // 文件格式错误
                errorMessage = "文件格式错误";
                break;
            case "30":
                // 操作失败,请勿重复提交
                errorMessage = "操作失败，请勿重复提交";
                break;
            case "40":
                // 文件下载失败
                errorMessage = "文件下载失败";
                break;
            case "50":
                // 文件内容过大
                errorMessage = "文件内容过大";
                break;
            case "60":
                // 图片解析失败
                errorMessage = "图片解析失败";
                break;
            case "70":
                // 识别失败
                errorMessage = "识别失败";
                break;
            case "80":
                // 服务超时
                errorMessage = "服务超时";
                break;
            case "90":
                // 未知错误
                errorMessage = "未知错误";
                break;
        }

        // 显示错误消息
        return errorMessage;

    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException
    {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 640;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

        // Rotate according to EXIF
        int rotate = 0;
        try
        {
            ExifInterface exif = new ExifInterface(getContentResolver().openInputStream(selectedImage));
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        }
        catch (IOException e)
        {
            Log.e("MainActivity", "ExifInterface IOException");
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static String FileSaveToInside(Context context, String fileName, Bitmap bitmap) {
        FileOutputStream fos = null;
        String path = null;
        try {
            //设置路径 /Android/data/com.panyko.filesave/Pictures/
            File folder = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //判断目录是否存在
            //目录不存在时自动创建
            if (folder.exists() ||folder.mkdir()) {
                File file = new File(folder, fileName);
                fos = new FileOutputStream(file);
                //写入文件
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                path = file.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (fos != null) {
                    //关闭流
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    path,path, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,  Uri.parse("file://"
                + Environment.getExternalStorageDirectory())));
        //返回路径
        return path;
    }


}
