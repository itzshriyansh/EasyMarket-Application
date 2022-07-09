package com.easy.market;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    Context context;
    ArrayList<CartList> arrayList;
    String sProductId, sCartId, sQty,sPlusMinus;
    SharedPreferences sp;

    public CartAdapter(CartActivity activity, ArrayList<CartList> CartLists) {
        this.context = activity;
        this.arrayList = CartLists;
        sp = context.getSharedPreferences(ConstantSp.PREF, Context.MODE_PRIVATE);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageButton plus, minus;
        TextView addtocart;
        TextView wishlist;
        TextView textView;
        TextView price;
        TextView qty;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.custome_cart_iv);
            addtocart = itemView.findViewById(R.id.custome_cart_addtocart);
            //wishlist = itemView.findViewById(R.id.custome_cart_wishlist);
            textView = itemView.findViewById(R.id.custome_cart_name);
            price = itemView.findViewById(R.id.custome_cart_price);
            qty = itemView.findViewById(R.id.custom_cart_qty);
            plus = itemView.findViewById(R.id.custom_cart_plus);
            minus = itemView.findViewById(R.id.custom_cart_minus);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_cart, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView.setText(arrayList.get(position).getName());
        holder.price.setText(arrayList.get(position).getPrice());
        holder.qty.setText(arrayList.get(position).getQty());
        //holder.imageView.setImageResource(arrayList.get(position).getImage());
        Picasso.with(context).load(arrayList.get(position).getImage()).placeholder(R.mipmap.ic_launcher).into(holder.imageView);
        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sCartId = arrayList.get(position).getId();
                sProductId = arrayList.get(position).getProductId();
                if (new ConnectionDetector(context).networkConnected()) {
                    new removeCart().execute();
                } else {
                    new ConnectionDetector(context).networkDisconnected();
                }
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sCartId = arrayList.get(position).getId();
                sQty = String.valueOf(Integer.parseInt(arrayList.get(position).getQty()) + 1);
                sProductId = arrayList.get(position).getProductId();
                sPlusMinus = "Plus";
                if (new ConnectionDetector(context).networkConnected()) {
                    new updateQtyCart().execute();
                } else {
                    new ConnectionDetector(context).networkDisconnected();
                }

            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sCartId = arrayList.get(position).getId();
                sProductId = arrayList.get(position).getProductId();
                sPlusMinus = "Minus";
                if (arrayList.get(position).getQty().equals("1")) {
                    if (new ConnectionDetector(context).networkConnected()) {
                        new removeCart().execute();
                    } else {
                        new ConnectionDetector(context).networkDisconnected();
                    }
                } else {
                    sQty = String.valueOf(Integer.parseInt(arrayList.get(position).getQty()) - 1);
                    if (new ConnectionDetector(context).networkConnected()) {
                        new updateQtyCart().execute();
                    } else {
                        new ConnectionDetector(context).networkDisconnected();
                    }
                }
            }
        });

        /*holder.wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Wishlist", Toast.LENGTH_SHORT).show();
                sProductId = arrayList.get(position).getProductId();
                if (new ConnectionDetector(context).networkConnected()) {
                    new addWishlist().execute();
                } else {
                    new ConnectionDetector(context).networkDisconnected();
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private class addWishlist extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
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
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class removeCart extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("action", "deleteAddToCart");
            hashMap.put("id", sCartId);
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL + "user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("Status").equals("True")) {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, CartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class updateQtyCart extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("action", "updateCartQty");
            hashMap.put("id", sCartId);
            hashMap.put("qty", sQty);
            hashMap.put("productId", sProductId);
            hashMap.put("plusMinus", sPlusMinus);
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL + "user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("Status").equals("True")) {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, CartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

