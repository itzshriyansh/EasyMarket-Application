package com.easy.market;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.IOException;

public class AddSubCategoryActivity extends AppCompatActivity {

    ImageView iv;
    EditText name;
    Button submit;
    private static int RESULT_LOAD_IMAGE = 1;

    /*Spinner spinner;
    String[] technology = {"Android","Java","Php","IOS"};
    ArrayList<String> categoryId;
    ArrayList<String> categoryName;
    String sCategoryId;*/

    private static int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Bitmap bitmap;
    private Uri filePath;
    Cursor c = null;
    ProgressDialog pd;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_category);
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);
        if (sp.getString(ConstantSp.SUB_CATEGORY_FLAG, "").equals("Add")) {
            getSupportActionBar().setTitle("Add Sub Category");
        } else {
            getSupportActionBar().setTitle("Update Sub Category");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        requestStoragePermission();
        //spinner = findViewById(R.id.list_spinner);
        /*ArrayAdapter adapter = new ArrayAdapter(AddSubCategoryActivity.this,android.R.layout.simple_list_item_checked,technology);
        spinner.setAdapter(adapter);
        if(new ConnectionDetector(AddSubCategoryActivity.this).networkConnected()){
            new getCategory().execute();
        }
        else{
            new ConnectionDetector(AddSubCategoryActivity.this).networkDisconnected();
        }*/

        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sCategoryId = categoryId.get(i);
                Toast.makeText(AddSubCategoryActivity.this, categoryName.get(i), Toast.LENGTH_SHORT).show();
                *//*String s = adapterView.getItemAtPosition(i)+"";
                Toast.makeText(SpinnerListActivity.this, s, Toast.LENGTH_SHORT).show();*//*
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


        iv = findViewById(R.id.add_Sub_category_iv);
        name = findViewById(R.id.add_Sub_category_name);
        submit = findViewById(R.id.add_Sub_category_submit);

        if (sp.getString(ConstantSp.SUB_CATEGORY_FLAG, "").equals("Add")) {

        } else {
            name.setText(sp.getString(ConstantSp.SUB_CATEGORY_NAME, ""));
            Picasso.with(AddSubCategoryActivity.this).load(sp.getString(ConstantSp.SUB_CATEGORY_IMAGE, "")).placeholder(R.mipmap.ic_launcher).into(iv);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);*/
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Permission()) {
                        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhotoIntent, PICK_IMAGE_REQUEST);
                    } else {
                        Permissioncall();
                    }
                } else {
                    Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhotoIntent, PICK_IMAGE_REQUEST);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals("")){
                    name.setError("Subject Name Required");
                }
                else{
                    if(sp.getString(ConstantSp.SUB_CATEGORY_FLAG,"").equals("Add")) {
                        if(new ConnectionDetector(AddSubCategoryActivity.this).networkConnected()){
                            uploadMultipart();
                        }
                        else{
                            new ConnectionDetector(AddSubCategoryActivity.this).networkDisconnected();
                        }
                    }
                    else{
                        if (new ConnectionDetector(AddSubCategoryActivity.this).networkConnected()) {
                            uploadMultipartUpdate();
                        } else {
                            new ConnectionDetector(AddSubCategoryActivity.this).networkDisconnected();
                        }
                    }
                }
            }
        });

    }

    private String getImage(Uri uri) {
        if (uri != null) {
            String path = null;
            String[] s_array = {MediaStore.Images.Media.DATA};
            Cursor c = managedQuery(uri, s_array, null, null, null);
            int id = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (c.moveToFirst()) {
                do {
                    path = c.getString(id);
                }
                while (c.moveToNext());
                c.close();
                if (path != null) {
                    return path;
                }
            }
        }
        return "";
    }

    private void uploadMultipart() {
        String path = getImage(filePath);
        if (!path.equals("")) {
            try {
                pd = new ProgressDialog(AddSubCategoryActivity.this);
                pd.setMessage("Please Wait...");
                pd.setCancelable(false);
                pd.show();
                new MultipartUploadRequest(this, ConstantSp.BASEURL + "user_management.php")
                        .addParameter("action", "addSubCategory")
                        .addParameter("categoryId", sp.getString(ConstantSp.CATEGORY_ID, ""))
                        .addParameter("name", name.getText().toString())
                        .addFileToUpload(path, "file")
                        .setMaxRetries(2)
                        .startUpload();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(AddSubCategoryActivity.this, "Subject Added Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddSubCategoryActivity.this, SubCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                }, 2500);
            } catch (Exception exc) {
                Toast.makeText(this, "Subject Added Unsuccessfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please Select Subject Image", Toast.LENGTH_SHORT).show();
            /*if (new ConnectionDetector(SignupActivity.this).isConnectingToInternet()) {
                pd = new ProgressDialog(SignupActivity.this);
                pd.setMessage("Please Wait...");
                pd.setCancelable(false);
                pd.show();
                addData();
            } else {
                Toast.makeText(SignupActivity.this, R.string.internet, Toast.LENGTH_SHORT).show();
                //new ConnectionDetector(SignupActivity.this).connectiondetect();
            }*/
        }
    }

    private void uploadMultipartUpdate() {
        String path = getImage(filePath);
        if (!path.equals("")) {
            try {
                pd = new ProgressDialog(AddSubCategoryActivity.this);
                pd.setMessage("Please Wait...");
                pd.setCancelable(false);
                pd.show();
                new MultipartUploadRequest(this, ConstantSp.BASEURL + "user_management.php")
                        .addParameter("action", "updateSubCategory")
                        .addParameter("id",sp.getString(ConstantSp.SUB_CATEGORY_ID, ""))
                        .addParameter("name", name.getText().toString())
                        .addFileToUpload(path, "file")
                        .setMaxRetries(2)
                        .startUpload();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(AddSubCategoryActivity.this, "Subject Update Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddSubCategoryActivity.this, SubCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                }, 2500);
            } catch (Exception exc) {
                Toast.makeText(this, "Subject Added Unsuccessfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please Select Subject Image", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            //getPath(filePath);
            if (!filePath.equals("")) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    iv.setImageBitmap(bitmap);
                    iv.setImageURI(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
            }
        } else {
            //Toast.makeText(LoginActivity.this, "Image Not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getPath(Uri filePath) {
        if (filePath != null) {
            Cursor cursor = getContentResolver().query(filePath, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();
            cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
            return path;
        } else {
            return "";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private boolean Permission() {
        int permissiocode = ContextCompat.checkSelfPermission(AddSubCategoryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissiocode == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void Permissioncall() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddSubCategoryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(AddSubCategoryActivity.this, "write external store..", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(AddSubCategoryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    /*private class getCategory extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(AddSubCategoryActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("action", "getCategory");
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL+"user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getString("Status").equals("True")){
                    categoryName =  new ArrayList<>();
                    categoryId =  new ArrayList<>();
                    JSONArray array = jsonObject.getJSONArray("response");
                    for (int i = 0;i<array.length();i++)
                    {
                        JSONObject object = array.getJSONObject(i);
                        categoryId.add(object.getString("id"));
                        categoryName.add(object.getString("name"));
                    }
                    ArrayAdapter adapter = new ArrayAdapter(AddSubCategoryActivity.this,android.R.layout.simple_list_item_checked,categoryName);
                    spinner.setAdapter(adapter);
                }
                else{
                    Toast.makeText(AddSubCategoryActivity.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/
}
