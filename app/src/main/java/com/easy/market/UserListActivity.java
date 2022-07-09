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

public class UserListActivity extends AppCompatActivity {

    RecyclerView gridView;
    ArrayList<UserList> userLists;
    UserAdapter userAdapter;
    String[] name = {"Harkishan", "Parth", "Shivani"};
    String[] email = {"harkishan@gmail.com", "parth@gmail.com", "shivani@gmail.com"};
    String[] contact = {"9876543210", "9871234506", "9988123421"};
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        getSupportActionBar().setTitle("User List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);
        gridView = findViewById(R.id.user_list_grid);
        gridView.setLayoutManager(new LinearLayoutManager(UserListActivity.this));
        gridView.setItemAnimator(new DefaultItemAnimator());

        if (new ConnectionDetector(UserListActivity.this).networkConnected()) {
            new getUserData().execute();
        } else {
            new ConnectionDetector(UserListActivity.this).networkDisconnected();
        }
    }

    private class getUserData extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(UserListActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("action", "getUser");
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL + "user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("Status").equals("True")) {
                    JSONArray array = object.getJSONArray("response");
                    userLists = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        UserList list = new UserList();
                        list.setName(jsonObject.getString("name"));
                        list.setEmail(jsonObject.getString("email"));
                        list.setContact(jsonObject.getString("contact"));
                        userLists.add(list);
                    }
                    userAdapter = new UserAdapter(UserListActivity.this, userLists);
                    gridView.setAdapter(userAdapter);

                } else {
                    Toast.makeText(UserListActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

