package com.easy.market;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class
ShippingActivity extends AppCompatActivity  implements PaymentResultListener {

    Button shippingbutton;
    EditText name, contact, address, city, state, pincode;
    SharedPreferences sp;
    String sPaymentType = "", sTransactionId = "";
    private static final String TAG = ShippingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);
        getSupportActionBar().setTitle("Shipping Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);
        //placeorder = findViewById(R.id.shipping_place_order);

        name = findViewById(R.id.shipping_name);
        contact = findViewById(R.id.shipping_contact);
        address = findViewById(R.id.shipping_address);
        city = findViewById(R.id.shipping_city);
        state = findViewById(R.id.shipping_state);
        pincode = findViewById(R.id.shipping_pincode);
        shippingbutton = findViewById(R.id.shipping_place_order);

        shippingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")) {
                    name.setError("Name Is Empty");
                    return;
                } else if (contact.getText().toString().equals("")) {
                    contact.setError("Contact Required");
                    return;
                } else if (address.getText().toString().equals("")) {
                    address.setError("Address Required");
                    return;
                } else if (city.getText().toString().equals("")) {
                    city.setError("City Required");
                    return;
                } else if (state.getText().toString().equals("")) {
                    state.setError("State Required");
                    return;
                } else if (pincode.getText().toString().equals("")) {
                    pincode.setError("Pincode Required");
                    return;
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShippingActivity.this);
                    builder.setMessage("Select Payment Method");
                    builder.setPositiveButton("Cash On Delivery", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (new ConnectionDetector(ShippingActivity.this).networkConnected()) {
                                sPaymentType = "Cash On Delivery";
                                sTransactionId = "";
                                new addShipping().execute();
                            } else {
                                new ConnectionDetector(ShippingActivity.this).networkDisconnected();
                            }
                        }
                    });
                    builder.setNeutralButton("Card/UPI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (new ConnectionDetector(ShippingActivity.this).networkConnected()) {
                                sPaymentType = "Card/UPI";
                                startPayment();
                            } else {
                                new ConnectionDetector(ShippingActivity.this).networkDisconnected();
                            }
                        }
                    });
                    builder.show();
                }
            }
        });

        /*placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new ConnectionDetector(ShippingActivity.this).networkConnected()) {
                    new addShipping().execute();
                } else {
                    new ConnectionDetector(ShippingActivity.this).networkDisconnected();
                }
            }
        });*/
    }

    public void startPayment() {
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Razorpay Corp");
            //options.put("description", "Demoing Charges");
            options.put("description", "Beyond Book Rental");
            options.put("send_sms_hash", true);
            options.put("allow_rotation", true);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", Double.parseDouble(String.valueOf(sp.getString(ConstantSp.PRICE,""))) * 100);

            JSONObject preFill = new JSONObject();
            preFill.put("email", sp.getString(ConstantSp.EMAIL, ""));
            preFill.put("contact", sp.getString(ConstantSp.CONTACT, ""));

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Log.d("RESPONSE", e.getMessage());
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            //Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            sTransactionId = razorpayPaymentID;
            if (new ConnectionDetector(ShippingActivity.this).networkConnected()) {
                new addShipping().execute();
            } else {
                new ConnectionDetector(ShippingActivity.this).networkDisconnected();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Log.d("RESPONSE", "Payment Cancelled " + code + " " + response);
            //Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
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

    private class addShipping extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ShippingActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("action", "addShippingAddress");
            hashMap.put("userId", sp.getString(ConstantSp.ID, ""));
            hashMap.put("name", name.getText().toString());
            hashMap.put("contact", contact.getText().toString());
            hashMap.put("address", address.getText().toString());
            hashMap.put("city", city.getText().toString());
            hashMap.put("state", state.getText().toString());
            hashMap.put("pincode", pincode.getText().toString());
            hashMap.put("price", sp.getString(ConstantSp.PRICE, ""));
            hashMap.put("paymentType", sPaymentType);
            hashMap.put("transactionId", sTransactionId);
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL + "user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("Status").equals("True")) {
                    Toast.makeText(ShippingActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                    sp.edit().putString(ConstantSp.ORDERID, object.getString("orderId")).commit();
                    startActivity(new Intent(ShippingActivity.this, OrderSuccesfullActivity.class));
                } else {
                    Toast.makeText(ShippingActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
