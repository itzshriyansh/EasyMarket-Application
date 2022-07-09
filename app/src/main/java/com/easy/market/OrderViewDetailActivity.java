package com.easy.market;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
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

public class OrderViewDetailActivity extends AppCompatActivity {

    RecyclerView gridView;
    ArrayList<OrderViewList> orderViewLists;
    OrderViewAdapter orderViewAdapter;
    String[] courseName = {"Oracle","Star"};
    String[] price = {"100","150"};
    int[] courseImage = {R.drawable.oracle,R.drawable.starbook};
    TextView orderid,contact,name,address;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view_detail);
        getSupportActionBar().setTitle("Order Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);
        orderid = findViewById(R.id.order_view_order_id);
        contact = findViewById(R.id.order_view_contact_no);
        name = findViewById(R.id.order_view_name);
        address = findViewById(R.id.order_view_shipping_address);

        orderid.setText("Order No : "+sp.getString(ConstantSp.ORDERID,""));
        gridView = findViewById(R.id.order_view_grid);

        if(new ConnectionDetector(OrderViewDetailActivity.this).networkConnected()){
            new getShippingAddress().execute();
            new getOrderProduct().execute();
        }
        else{
            new ConnectionDetector(OrderViewDetailActivity.this).networkDisconnected();
        }


        gridView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        gridView.setItemAnimator(new DefaultItemAnimator());

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

    private class getShippingAddress extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(OrderViewDetailActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("action", "getShippingAddress");
            hashMap.put("orderId", sp.getString(ConstantSp.ORDERID,""));
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL+"user_management.php",MakeServiceCall.POST , hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if(object.getString("Status").equals("True")){
                    JSONArray array = object.getJSONArray("response");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        name.setText(jsonObject.getString("name"));
                        contact.setText(jsonObject.getString("contact"));
                        address.setText(jsonObject.getString("address")+"\n"+jsonObject.getString("city")+"\n"+jsonObject.getString("state")+"\n"+jsonObject.getString("pincode"));
                    }

                }
                else{
                    Toast.makeText(OrderViewDetailActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class getOrderProduct extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(OrderViewDetailActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("action", "getOrderProduct");
            hashMap.put("orderId", sp.getString(ConstantSp.ORDERID,""));
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL+"user_management.php",MakeServiceCall.POST , hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if(object.getString("Status").equals("True")){
                    JSONArray array = object.getJSONArray("response");
                    orderViewLists =new ArrayList<>();

                    for (int i = 0; i<array.length(); i++)

                    {
                        JSONObject jsonObject = array.getJSONObject(i);
                        OrderViewList list = new OrderViewList();
                        list.setName(jsonObject.getString("name"));
                        list.setImage(jsonObject.getString("image"));
                        list.setPrice(jsonObject.getString("price"));
                        orderViewLists.add(list);
                    }
                    orderViewAdapter = new OrderViewAdapter(OrderViewDetailActivity.this, orderViewLists);
                    gridView.setAdapter(orderViewAdapter);
                }
                else{
                    Toast.makeText(OrderViewDetailActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
