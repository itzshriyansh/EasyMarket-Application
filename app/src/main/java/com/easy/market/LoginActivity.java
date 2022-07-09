package com.easy.market;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private TextView btRegister;
    TextView tvLogin, forget, signup;
    Button login;
    EditText email, password;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);
        btRegister = findViewById(R.id.signup_button);
        //tvLogin = findViewById(R.id.tvLogin);
        login = findViewById(R.id.login_button);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        forget = findViewById(R.id.forgotclk);

        signup = findViewById(R.id.login_signup_text);

        signup.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().equals("")) {
                    email.setError("Email Id Required");
                    return;
                } else if (password.getText().toString().equals("")) {
                    password.setError("Password Required");
                } else {
                    if (new ConnectionDetector(LoginActivity.this).networkConnected()) {
                        new loginData().execute();
                    } else {
                        new ConnectionDetector(LoginActivity.this).networkDisconnected();
                    }
                }
            }
        });
        /*btRegister.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(tvLogin, "tvLogin");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, activityOptions.toBundle());
            }
        });*/
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));

            }
        });

    }

    private class loginData extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("action", "login");
            hashMap.put("contact", email.getText().toString());
            hashMap.put("password", password.getText().toString());
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
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        if (jsonObject.getString("email").equals("admin@gmail.com")) {
                            sp.edit().putString(ConstantSp.USER_TYPE, "Admin").commit();
                        } else {
                            sp.edit().putString(ConstantSp.USER_TYPE, "User").commit();
                        }
                        sp.edit().putString(ConstantSp.ID, jsonObject.getString("id")).commit();
                        sp.edit().putString(ConstantSp.NAME, jsonObject.getString("name")).commit();
                        sp.edit().putString(ConstantSp.EMAIL, jsonObject.getString("email")).commit();
                        sp.edit().putString(ConstantSp.CONTACT, jsonObject.getString("contact")).commit();
                        sp.edit().putString(ConstantSp.PASSWORD, jsonObject.getString("password")).commit();
                        startActivity(new Intent(LoginActivity.this, DashboardNavigationActivity.class));
                    }
                } else {
                    Toast.makeText(LoginActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

