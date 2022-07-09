package com.easy.market;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ProductDescriptionActivity extends AppCompatActivity {

    ImageView imageView;
    TextView name, price, description;
    Button wishlist, addToCart;
    SharedPreferences sp;
    String sProductId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);
        getSupportActionBar().setTitle(sp.getString(ConstantSp.PRODUCT_NAME, ""));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = findViewById(R.id.product_description_IV);
        name = findViewById(R.id.product_description_name);
        price = findViewById(R.id.product_description_price);
        description = findViewById(R.id.product_description_description);
        wishlist = findViewById(R.id.product_description_wishlist);
        addToCart = findViewById(R.id.product_description_add_cart);

        name.setText(sp.getString(ConstantSp.PRODUCT_NAME, ""));
        price.setText("Rs." + sp.getString(ConstantSp.PRODUCT_PRICE, ""));
        description.setText(sp.getString(ConstantSp.PRODUCT_DESCRIPTION, ""));
        Picasso.with(ProductDescriptionActivity.this).load(sp.getString(ConstantSp.PRODUCT_IMAGE, "")).placeholder(R.mipmap.ic_launcher).into(imageView);

        if(sp.getString(ConstantSp.USER_TYPE,"").equals("Admin")){
            addToCart.setVisibility(View.GONE);
            wishlist.setVisibility(View.GONE);
        }
        else{
            addToCart.setVisibility(View.VISIBLE);
            wishlist.setVisibility(View.VISIBLE);
        }

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Add to cart", Toast.LENGTH_SHORT).show();
                sProductId = sp.getString(ConstantSp.ID, "");
                if (new ConnectionDetector(ProductDescriptionActivity.this).networkConnected()) {
                    new addToCartData().execute();
                } else {
                    new ConnectionDetector(ProductDescriptionActivity.this).networkDisconnected();
                }
            }
        });

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sProductId = sp.getString(ConstantSp.ID, "");
                if (new ConnectionDetector(ProductDescriptionActivity.this).networkConnected()) {
                    new addWishlist().execute();
                } else {
                    new ConnectionDetector(ProductDescriptionActivity.this).networkDisconnected();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class addToCartData extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ProductDescriptionActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("action", "addToCart");
            hashMap.put("productId", sProductId);
            hashMap.put("userId", sp.getString(ConstantSp.ID, ""));
            hashMap.put("qty", "1");
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL + "user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("Status").equals("True")) {
                    Toast.makeText(ProductDescriptionActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDescriptionActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class addWishlist extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ProductDescriptionActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("action", "addWishlist");
            hashMap.put("productId", sProductId);
            hashMap.put("userId", sp.getString(ConstantSp.ID, ""));
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL + "user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("Status").equals("True")) {
                    Toast.makeText(ProductDescriptionActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDescriptionActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
