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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    Context context;
    ArrayList<ProductList> arrayList;
    SharedPreferences sp;
    String sProductId;

    public ProductAdapter(ProductActivity activity, ArrayList<ProductList> productLists) {
        this.context = activity;
        this.arrayList = productLists;
        sp = context.getSharedPreferences(ConstantSp.PREF, Context.MODE_PRIVATE);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView addtocart;
        TextView wishlist;
        TextView textView,qty;
        TextView price,edit, delete;
        LinearLayout editLayout,ubhipati;
        View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.custome_product_iv);
            addtocart = itemView.findViewById(R.id.custome_product_addtocart);
            wishlist = itemView.findViewById(R.id.custome_product_wishlist);
            textView = itemView.findViewById(R.id.custome_product_name);
            price = itemView.findViewById(R.id.custome_product_price);
            edit = itemView.findViewById(R.id.custom_product_edit);
            delete = itemView.findViewById(R.id.custom_product_delete);
            editLayout = itemView.findViewById(R.id.custom_product_edit_layout);
            view = itemView.findViewById(R.id.custom_view_product);
            ubhipati = itemView.findViewById(R.id.ubhi_pati_custom_product);
            qty = itemView.findViewById(R.id.seller_qty);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_product, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if(sp.getString(ConstantSp.USER_TYPE,"").equals("Admin")){
            holder.addtocart.setVisibility(View.GONE);
            holder.wishlist.setVisibility(View.GONE);
            //holder.view.setVisibility(View.GONE);
            holder.ubhipati.setVisibility(View.GONE);
            holder.qty.setVisibility(View.VISIBLE);
            if(Integer.parseInt(arrayList.get(position).getQty())<=0){
                holder.qty.setText("Out Of Stock");
            }
            else{
                holder.qty.setText(arrayList.get(position).getQty());
            }
        }
        else{
            holder.wishlist.setVisibility(View.VISIBLE);
            if(Integer.parseInt(arrayList.get(position).getQty())<=0){
                holder.addtocart.setText("Out Of Stock");
                holder.qty.setText("Out Of Stock");
                holder.qty.setVisibility(View.VISIBLE);
            }
            else{
                holder.addtocart.setText("ADD TO CART");
                holder.qty.setText(arrayList.get(position).getQty());
                holder.qty.setVisibility(View.GONE);
            }
        }

        holder.textView.setText(arrayList.get(position).getName());
        holder.price.setText("Rs." + arrayList.get(position).getPrice());
        //holder.imageView.setImageResource(arrayList.get(position).getImage());
        Picasso.with(context).load(arrayList.get(position).getImage()).placeholder(R.mipmap.ic_launcher).into(holder.imageView);

        if (sp.getString(ConstantSp.USER_TYPE, "").equals("Admin")) {
            holder.editLayout.setVisibility(View.VISIBLE);
        } else {
            holder.editLayout.setVisibility(View.GONE);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.PRODUCT_FLAG, "Edit").commit();
                sp.edit().putString(ConstantSp.PRODUCT_ID, arrayList.get(position).getId()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_NAME, arrayList.get(position).getName()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_PRICE, arrayList.get(position).getPrice()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_IMAGE, arrayList.get(position).getImage()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_DESCRIPTION, arrayList.get(position).getDescription()).commit();
                context.startActivity(new Intent(context, AddProductActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new ConnectionDetector(context).networkConnected()) {
                    sp.edit().putString(ConstantSp.PRODUCT_ID, arrayList.get(position).getId()).commit();
                    new deleteData().execute();
                } else {
                    new ConnectionDetector(context).networkDisconnected();
                }
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.PRODUCT_ID, arrayList.get(position).getId()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_NAME, arrayList.get(position).getName()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_PRICE, arrayList.get(position).getPrice()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_IMAGE, arrayList.get(position).getImage()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_DESCRIPTION, arrayList.get(position).getDescription()).commit();
                context.startActivity(new Intent(context, ProductDescriptionActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.PRODUCT_ID, arrayList.get(position).getId()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_NAME, arrayList.get(position).getName()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_PRICE, arrayList.get(position).getPrice()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_IMAGE, arrayList.get(position).getImage()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_DESCRIPTION, arrayList.get(position).getDescription()).commit();
                context.startActivity(new Intent(context, ProductDescriptionActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Add to cart", Toast.LENGTH_SHORT).show();
                sProductId = arrayList.get(position).getId();
                if(Integer.parseInt(arrayList.get(position).getQty())<0){
                    Toast.makeText(context, "Out Of Stock", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (new ConnectionDetector(context).networkConnected()) {
                        new addToCartData().execute();
                    } else {
                        new ConnectionDetector(context).networkDisconnected();
                    }
                }
            }
        });

        holder.wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sProductId = arrayList.get(position).getId();
                if (new ConnectionDetector(context).networkConnected()) {
                    new addWishlist().execute();
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

    private class deleteData extends AsyncTask<String, String, String> {

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
            hashMap.put("action", "deleteProduct");
            hashMap.put("id", sp.getString(ConstantSp.PRODUCT_ID, ""));
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL + "user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("Status").equals("True")) {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, ProductActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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
                } else {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
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

    /*@Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.custom_product, null);
        ImageView imageView = view.findViewById(R.id.custome_product_iv);
        ImageView addtocart = view.findViewById(R.id.custome_product_addtocart);
        ImageView wishlist = view.findViewById(R.id.custome_product_wishlist);
        TextView textView = view.findViewById(R.id.custome_product_name);
        TextView price = view.findViewById(R.id.custome_product_price);
        textView.setText(arrayList.get(i).getName());
        price.setText(arrayList.get(i).getPrice());
        imageView.setImageResource(arrayList.get(i).getImage());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ProductDescriptionActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ProductDescriptionActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Add to cart", Toast.LENGTH_SHORT).show();
            }
        });

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Wishlist", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }*/
}

