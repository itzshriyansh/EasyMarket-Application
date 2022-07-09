package com.easy.market;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
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

public class OrderHistoryActivity extends AppCompatActivity {

    RecyclerView gridView;
    ArrayList<OrderHistoryList> subCategoryLists;
    OrderHistoryAdapter subCategoryAdapter;
    String[] orderid = {"2","5"};
    String[] orderprice ={"1000","1500"};
    String[] orderdate = {"01-12-2019","05-12-2019"};
    String[] orderstatus ={"Delivered","Pending"};

    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);
        getSupportActionBar().setTitle("Order History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = findViewById(R.id.order_history_grid);
        gridView.setLayoutManager(new LinearLayoutManager(OrderHistoryActivity.this));
        gridView.setItemAnimator(new DefaultItemAnimator());
        if(new ConnectionDetector(OrderHistoryActivity.this).networkConnected()){
            new getData().execute();
        }
        else{
            new ConnectionDetector(OrderHistoryActivity.this).networkDisconnected();
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
            pd = new ProgressDialog(OrderHistoryActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("action", "getOrderList");
            hashMap.put("userType", sp.getString(ConstantSp.USER_TYPE,""));
            hashMap.put("userId", sp.getString(ConstantSp.ID,""));
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL+"user_management.php", MakeServiceCall.POST , hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if(object.getString("Status").equals("True")){
                    JSONArray array = object.getJSONArray("response");
                    subCategoryLists =new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        OrderHistoryList list = new OrderHistoryList();
                        list.setOrderid(jsonObject.getString("id"));
                        if(jsonObject.getString("paymentType").equalsIgnoreCase("Cash On Delivery")) {
                            list.setTransactionId(jsonObject.getString("paymentType"));
                        }
                        else{
                            list.setTransactionId(jsonObject.getString("paymentType")+" ("+jsonObject.getString("transactionId")+")");
                        }
                        list.setOrderprice(jsonObject.getString("price"));
                        //list.setOrderprice(jsonObject.getString("price"));
                        list.setOrderdate(jsonObject.getString("created_date"));
                        list.setOrderstatus(jsonObject.getString("status"));
                        subCategoryLists.add(list);
                    }
                    subCategoryAdapter = new OrderHistoryAdapter(OrderHistoryActivity.this, subCategoryLists);
                    gridView.setAdapter(subCategoryAdapter);
                }
                else{
                    Toast.makeText(OrderHistoryActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

