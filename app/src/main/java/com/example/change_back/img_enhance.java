package com.example.change_back;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import util.HttpUtil;

public class img_enhance extends AppCompatActivity {

    private Bitmap bitmap = null; // 目标图片

    private String selectedImagePath = null;

    private ImageView imageView = null;

    private Spinner spinnerModel;

    private String option = "https://aip.baidubce.com/rest/2.0/image-process/v1/dehaze";

    private String[] Options = {"https://aip.baidubce.com/rest/2.0/image-process/v1/dehaze", "https://aip.baidubce.com/rest/2.0/image-process/v1/contrast_enhance", "https://aip.baidubce.com/rest/2.0/image-process/v1/image_quality_enhance", "https://aip.baidubce.com/rest/2.0/image-process/v1/image_quality_enhance", "https://aip.baidubce.com/rest/2.0/image-process/v1/inpainting","https://aip.baidubce.com/rest/2.0/image-process/v1/image_definition_enhance","https://aip.baidubce.com/rest/2.0/image-process/v1/color_enhance","https://aip.baidubce.com/rest/2.0/image-process/v1/denoise"};

    private Button btnTakePhoto;

    private File outputImage;

    private Uri imageUri;

    private static final int takePhoto = 3;
    @Override
    protected void onStart() {
        super.onStart();
        checkPermission(this);
    }

    public static final int REQUEST_CODE = 5;
    //定义三个权限
    private static final String[] permission = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    //每个权限是否已授
    public static boolean isPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            for (int i = 0; i < permission.length; i++) {
                int checkPermission = ContextCompat.checkSelfPermission(activity, permission[i]);
                /***
                 * checkPermission返回两个值
                 * 有权限: PackageManager.PERMISSION_GRANTED
                 * 无权限: PackageManager.PERMISSION_DENIED
                 */
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    public static boolean checkPermission(Activity activity) {
        if (isPermissionGranted(activity)) {
            return true;
        } else {
            //如果没有设置过权限许可，则弹出系统的授权窗口
            ActivityCompat.requestPermissions(activity, permission, REQUEST_CODE);
            return false;
        }
    }

    /**********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enhance);

        Button btnExecuteJavaProgram = findViewById(R.id.xuanzetupian);
        imageView = findViewById(R.id.imageView1);

        btnExecuteJavaProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });

        Button Button2 = findViewById(R.id.button1);
        Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String path = executeImageProcessing(selectedImagePath);
            }
        });

        ImageView Button3 = findViewById(R.id.saveimg);
        Button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (bitmap != null)
                {
                    String path = FileSaveToInside(img_enhance.this,"result.jpg",bitmap);
                    Toast.makeText(img_enhance.this, path, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(img_enhance.this,"无图片", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spinnerModel = (Spinner) findViewById(R.id.spinnerModel);
        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
            {
                option = Options[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
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
                                imageUri = FileProvider.getUriForFile(img_enhance.this, "com.example.permissiontest.fileprovider", outputImage);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            selectedImagePath = getPathFromUri(selectedImageUri);
            // 这里可以添加代码将选定照片的地址存储到数据库或做其他操作
            //Toast.makeText(this, "Selected Image Path: " + selectedImagePath, Toast.LENGTH_LONG).show();

            // 在这里调用图像处理方法并获取返回的路径
            //String path = executeImageProcessing(selectedImagePath);

            // 在这里进行其他操作，使用返回的路径

        }
    }

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

    private String executeImageProcessing(String selectedImagePath) {
        // 在这里调用你的图像处理方法并传入选定的图片路径
        String result = enhance_api.colourize(selectedImagePath,option);
        // 假设处理方法返回的是路径字符串，你可以根据实际情况修改这部分代码

        // 处理图像处理结果
        if (result != null) {
            Toast.makeText(img_enhance.this, "图像处理成功", Toast.LENGTH_SHORT).show();
            try {
                // 解析 JSON 数据
                JSONObject jsonObject = new JSONObject(result);
                String base64Image = jsonObject.getString("image");

                // 解码 Base64 编码的图像数据为字节数组
                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);

                // 创建 Bitmap 对象
                bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                imageView.setImageBitmap(bitmap);

                // 保存 Bitmap 到相册
                //saveBitmapToGallery(bitmap);
                //Toast.makeText(Animation_BW.this, "图片保存成功", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(img_enhance.this, "JSON 解析出错", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 处理失败，给出相应的提示
            Toast.makeText(img_enhance.this, "图像处理失败", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void saveBitmapToGallery(Bitmap bitmap) {
        // 创建保存图片的文件夹
        File imagesDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyImages");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        // 生成文件名
        String fileName = "image_" + System.currentTimeMillis() + ".png";

        // 创建文件
        File imageFile = new File(imagesDir, fileName);

        // 保存 Bitmap 到文件
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // 插入图片到相册
            insertImageToGallery(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(img_enhance.this, "文件未找到", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(img_enhance.this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertImageToGallery(File imageFile) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, imageFile.getName());
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image saved from MyApp");
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, imageFile.getAbsolutePath());

        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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

/*******************************************************************/
class enhance_api {

    public static String colourize(final String imgurl,String url) {
        final String[] result = {null};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 请求url
                //String url ="https://aip.baidubce.com/rest/2.0/image-process/v1/style_trans";


                try {
                    byte[] imgData = util.FileUtil.readFileByBytes(imgurl);
                    String imgStr = util.Base64Util.encode(imgData); // 图片转base64
                    String imgParam = URLEncoder.encode(imgStr, "UTF-8");

                    String param = "image=" + imgParam ;


                    // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
                    String accessToken = "24.e07c5368886fcea07312cc35328cf4b8.2592000.1715526321.282335-61094609";

                    result[0] = HttpUtil.post(url, accessToken, param);
                    System.out.println(result[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join(); // 等待线程执行完毕
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }
}