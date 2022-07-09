package com.easy.market;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WishlistActivity extends AppCompatActivity {

    RecyclerView gridView;
    ArrayList<Wishlist> wishlists;
    WishAdapter wishAdapter;
    String[] courseName = {"Oracle","Star"};
    String[] price = {"100","150"};
    int[] courseImage = {R.drawable.oracle,R.drawable.starbook};
    SharedPreferences sp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish);
        getSupportActionBar().setTitle("Wishlist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);
        gridView = findViewById(R.id.Wish_grid);
        gridView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        gridView.setItemAnimator(new DefaultItemAnimator());

        if(new ConnectionDetector(WishlistActivity.this).networkConnected()){
            new getData().execute();
        }
        else{
            new ConnectionDetector(WishlistActivity.this).networkDisconnected();
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

    private class getData extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(WishlistActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("action", "getWishlist");
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
                    JSONArray array = object.getJSONArray("response");
                    wishlists = new ArrayList<>();
                    for (int i = 0; i<array.length(); i++)

                    {
                        JSONObject jsonObject = array.getJSONObject(i);
                        Wishlist list = new Wishlist();
                        list.setId(jsonObject.getString("id"));
                        list.setProductId(jsonObject.getString("productId"));
                        list.setName(jsonObject.getString("name"));
                        list.setImage(jsonObject.getString("image"));
                        list.setPrice(jsonObject.getString("price"));
                        wishlists.add(list);
                    }
                    wishAdapter = new WishAdapter(WishlistActivity.this, wishlists);
                    gridView.setAdapter(wishAdapter);
                }
                else{
                    Toast.makeText(WishlistActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
}
