package com.easy.market;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {

    RecyclerView gridView;
    ArrayList<CartList> cartLists;
    CartAdapter cartAdapter;
    Button checkout,continuehome;
    String[] courseName = {"Oracle","Star"};
    String[] price = {"100","150"};
    int[] courseImage = {R.drawable.oracle,R.drawable.starbook};
    SharedPreferences sp;
    TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setTitle("Add to Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);
        gridView = findViewById(R.id.cart_grid);
        checkout = findViewById(R.id.cart_checkout);
        continuehome = findViewById(R.id.cart_continue);
        total = findViewById(R.id.cart_order_total);

        continuehome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartActivity.this,ShippingActivity.class));
            }
        });
        //gridView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        gridView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        gridView.setItemAnimator(new DefaultItemAnimator());

        if(new ConnectionDetector(CartActivity.this).networkConnected()){
            new getData().execute();
        }
        else{
            new ConnectionDetector(CartActivity.this).networkDisconnected();
        }

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

    public class getData extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CartActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("action", "getAddToCart");
            hashMap.put("userId", sp.getString(ConstantSp.ID,""));
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL+"user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if(object.getString("Status").equals("True")){
                    total.setText("Total Amount : Rs."+object.getString("price"));
                    sp.edit().putString(ConstantSp.PRICE,object.getString("price")).commit();
                    JSONArray array = object.getJSONArray("response");
                    cartLists =new ArrayList<>();
                    for (int i = 0; i<array.length(); i++)
                    {
                        JSONObject jsonObject = array.getJSONObject(i);
                        CartList list = new CartList();
                        list.setId(jsonObject.getString("id"));
                        list.setProductId(jsonObject.getString("productId"));
                        list.setName(jsonObject.getString("name"));
                        list.setImage(jsonObject.getString("image"));
                        list.setQty(jsonObject.getString("qty"));
                        list.setPrice(jsonObject.getString("price"));
                        cartLists.add(list);
                    }
                    cartAdapter = new CartAdapter(CartActivity.this, cartLists);
                    gridView.setAdapter(cartAdapter);
                }
                else{
                    Toast.makeText(CartActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
