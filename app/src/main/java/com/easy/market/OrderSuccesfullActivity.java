package com.easy.market;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OrderSuccesfullActivity extends AppCompatActivity {

    Button button;
    TextView orderNo;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_succesfull);
        getSupportActionBar().hide();
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);
        orderNo=findViewById(R.id.success_order_no);
        button = findViewById(R.id.success_order_continue_shopping);

        orderNo.setText("Order No : "+sp.getString(ConstantSp.ORDERID, ""));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(OrderSuccesfullActivity.this,DashboardNavigationActivity.class));
            }
        });
    }
}
