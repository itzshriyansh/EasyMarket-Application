package com.easy.market;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SubCategoryActivity extends AppCompatActivity {

    RecyclerView gridView;
    ArrayList<SubCategoryList> subCategoryLists;
    SubCategoryAdapter subCategoryAdapter;
    String[] courseName_BCA = {"Java","Python"};
    int[] courseImage_BCA = {R.drawable.java,R.drawable.python};

    String[] courseName_MCA = {"Android","ASP .NET"};
    int[] courseImage_MCA = {R.drawable.java,R.drawable.python};

    SharedPreferences sp;
    FloatingActionButton add;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);
        getSupportActionBar().setTitle("Subject");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        add = findViewById(R.id.dashboard_add);
        if(sp.getString(ConstantSp.USER_TYPE,"").equals("Admin")){
            add.setVisibility(View.VISIBLE);
        }
        else{
            add.setVisibility(View.GONE);
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.SUB_CATEGORY_FLAG, "Add").commit();
                sp.edit().putString(ConstantSp.SUB_CATEGORY_ID, "").commit();
                sp.edit().putString(ConstantSp.SUB_CATEGORY_NAME, "").commit();
                sp.edit().putString(ConstantSp.SUB_CATEGORY_IMAGE, "").commit();
                startActivity(new Intent(SubCategoryActivity.this,AddSubCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        gridView = findViewById(R.id.sub_category_grid);
        gridView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        gridView.setItemAnimator(new DefaultItemAnimator());

        if(new ConnectionDetector(SubCategoryActivity.this).networkConnected()){
            new getData().execute();
        }
        else{
            new ConnectionDetector(SubCategoryActivity.this).networkDisconnected();
        }

        /*subCategoryLists =new ArrayList<>();
        if(sp.getString(ConstantSp.CATEGORY_ID,"").equals("1")) {
            for (int i = 0; i < courseName_BCA.length; i++) {
                SubCategoryList list = new SubCategoryList();
                list.setName(courseName_BCA[i]);
                list.setImage(courseImage_BCA[i]);
                subCategoryLists.add(list);
            }
        }
        else{
            for (int i = 0; i < courseName_MCA.length; i++) {
                SubCategoryList list = new SubCategoryList();
                list.setName(courseName_MCA[i]);
                list.setImage(courseImage_MCA[i]);
                subCategoryLists.add(list);
            }
        }
        subCategoryAdapter = new SubCategoryAdapter(SubCategoryActivity.this, subCategoryLists);
        gridView.setAdapter(subCategoryAdapter);*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class getData extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SubCategoryActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap();
            hashMap.put("action", "getSubCategory");
            hashMap.put("categoryId", sp.getString(ConstantSp.CATEGORY_ID, ""));
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL+"user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getString("Status").equals("True")){
                    subCategoryLists =  new ArrayList<>();
                    JSONArray array = jsonObject.getJSONArray("response");
                    for (int i = 0;i<array.length();i++)
                    {
                        JSONObject object = array.getJSONObject(i);
                        SubCategoryList list = new SubCategoryList();
                        list.setId(object.getString("id"));
                        list.setName(object.getString("name"));
                        list.setImage(object.getString("image"));
                        subCategoryLists.add(list);
                    }
                    subCategoryAdapter = new SubCategoryAdapter(SubCategoryActivity.this,subCategoryLists);
                    gridView.setAdapter(subCategoryAdapter);
                }
                else{
                    Toast.makeText(SubCategoryActivity.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
