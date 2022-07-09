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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductActivity extends AppCompatActivity {

    RecyclerView gridView;
    ArrayList<ProductList> productLists;
    ProductAdapter productAdapter;
    String[] courseName = {"Oracle","Star"};
    String[] price = {"100","150"};
    int[] courseImage = {R.drawable.oracle,R.drawable.starbook};

    SharedPreferences sp;
    FloatingActionButton add;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setTitle("Books");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);
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
                sp.edit().putString(ConstantSp.PRODUCT_FLAG, "Add").commit();
                sp.edit().putString(ConstantSp.PRODUCT_ID, "").commit();
                sp.edit().putString(ConstantSp.PRODUCT_NAME, "").commit();
                sp.edit().putString(ConstantSp.PRODUCT_PRICE, "").commit();
                sp.edit().putString(ConstantSp.PRODUCT_IMAGE, "").commit();
                sp.edit().putString(ConstantSp.PRODUCT_DESCRIPTION, "").commit();
                startActivity(new Intent(ProductActivity.this, AddProductActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        gridView = findViewById(R.id.product_grid);
        //gridView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        gridView.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
        gridView.setItemAnimator(new DefaultItemAnimator());

        if(new ConnectionDetector(ProductActivity.this).networkConnected()){
            new getData().execute();
        }
        else{
            new ConnectionDetector(ProductActivity.this).networkDisconnected();
        }

        /*productLists =new ArrayList<>();
        for (int i = 0; i<courseName.length; i++)

        {
            ProductList list = new ProductList();
            list.setName(courseName[i]);
            list.setImage(courseImage[i]);
            list.setPrice(price[i]);
            productLists.add(list);
        }
        productAdapter = new ProductAdapter(ProductActivity.this, productLists);
        gridView.setAdapter(productAdapter);*/
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
            pd = new ProgressDialog(ProductActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (sp.getString(ConstantSp.SUB_CATEGORY_ID, "").equals("")) {
                HashMap<String, String> hashMap = new HashMap();
                hashMap.put("action", "getSearchProduct");
                hashMap.put("search", sp.getString(ConstantSp.SEARCH, ""));
                return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL + "user_management.php", MakeServiceCall.POST, hashMap);
            }
            else {
                HashMap<String, String> hashMap = new HashMap();
                hashMap.put("action", "getProduct");
                hashMap.put("subCategoryId", sp.getString(ConstantSp.SUB_CATEGORY_ID, ""));
                return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL + "user_management.php", MakeServiceCall.POST, hashMap);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getString("Status").equals("True")){
                    productLists =  new ArrayList<>();
                    JSONArray array = jsonObject.getJSONArray("response");
                    for (int i = 0;i<array.length();i++)
                    {
                        JSONObject object = array.getJSONObject(i);
                        ProductList list = new ProductList();
                        list.setId(object.getString("id"));
                        list.setName(object.getString("name"));
                        list.setImage(object.getString("image"));
                        list.setPrice(object.getString("price"));
                        list.setQty(object.getString("qty"));
                        list.setDescription(object.getString("description"));
                        productLists.add(list);
                    }
                    productAdapter = new ProductAdapter(ProductActivity.this,productLists);
                    gridView.setAdapter(productAdapter);
                }
                else{
                    Toast.makeText(ProductActivity.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}

