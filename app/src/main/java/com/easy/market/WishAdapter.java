package com.easy.market;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

class WishAdapter extends RecyclerView.Adapter<WishAdapter.MyViewHolder> {

    Context context;
    ArrayList<Wishlist> arrayList;
    SharedPreferences sp;
    String sProductId;
    String sWishlistId;

    public WishAdapter(WishlistActivity activity, ArrayList<Wishlist> Wishlists) {
        this.context = activity;
        this.arrayList = Wishlists;
        sp = context.getSharedPreferences(ConstantSp.PREF, Context.MODE_PRIVATE);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView addtocart;
        ImageView wishlist;
        TextView textView;
        TextView price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.custome_wish_iv);
            addtocart = itemView.findViewById(R.id.custome_cart_addtocart);
            wishlist = itemView.findViewById(R.id.custome_wish_wishlist);
            textView = itemView.findViewById(R.id.custome_wish_name);
            price = itemView.findViewById(R.id.custome_wish_price);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_wish, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView.setText(arrayList.get(position).getName());
        holder.price.setText(arrayList.get(position).getPrice());
        Picasso.with(context).load(arrayList.get(position).getImage()).placeholder(R.mipmap.ic_launcher).into(holder.imageView);

        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sProductId = arrayList.get(position).getProductId();
                sWishlistId = arrayList.get(position).getId();
                if (new ConnectionDetector(context).networkConnected()) {
                    new addToCartData().execute();
                } else {
                    new ConnectionDetector(context).networkDisconnected();
                }
            }
        });

        holder.wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sWishlistId = arrayList.get(position).getId();
                if (new ConnectionDetector(context).networkConnected()) {
                    new removeWishlist().execute();
                } else {
                    new ConnectionDetector(context).networkDisconnected();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private class addToCartData extends AsyncTask<String, String, String> {

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
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                    if (new ConnectionDetector(context).networkConnected()) {
                        new removeWishlist().execute();
                    } else {
                        new ConnectionDetector(context).networkDisconnected();
                    }
                } else {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class removeWishlist extends AsyncTask<String, String, String> {

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
            hashMap.put("action", "deleteWishlist");
            hashMap.put("id", sWishlistId);
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
                    context.startActivity(new Intent(context,WishlistActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}


