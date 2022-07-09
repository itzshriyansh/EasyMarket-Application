package com.easy.market;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder> {

    Context context;
    ArrayList<OrderHistoryList> arrayList;
    SharedPreferences sp;
    String sStatus, sOrderId;

    public OrderHistoryAdapter(OrderHistoryActivity activity, ArrayList<OrderHistoryList> OrderHistoryLists) {
        this.context = activity;
        this.arrayList = OrderHistoryLists;
        sp = context.getSharedPreferences(ConstantSp.PREF, Context.MODE_PRIVATE);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView orderid, orderdate, orderprice, orderstatus, confirm, cancel, qty,returnOrder;
        LinearLayout confirmLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            orderid = itemView.findViewById(R.id.custome_order_history_orderid);
            orderdate = itemView.findViewById(R.id.custome_order_history_orderdate);
            orderprice = itemView.findViewById(R.id.custome_order_history_orderprice);
            orderstatus = itemView.findViewById(R.id.custome_order_history_orderstatus);
            qty = itemView.findViewById(R.id.custome_order_history_qty);
            confirm = itemView.findViewById(R.id.custome_order_history_confirm);
            cancel = itemView.findViewById(R.id.custome_order_history_cancel);
            confirmLayout = itemView.findViewById(R.id.custome_order_history_confirm_layout);
            returnOrder = itemView.findViewById(R.id.custome_order_history_return);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_order_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.orderid.setText("Order No : " + arrayList.get(position).getOrderid());
        holder.orderprice.setText("Rs." + arrayList.get(position).getOrderprice());
        holder.orderdate.setText(arrayList.get(position).getOrderdate());
        holder.qty.setText(arrayList.get(position).getTransactionId());

        if (arrayList.get(position).getOrderstatus().equals("Pending")) {
            holder.orderstatus.setTextColor(context.getResources().getColor(R.color.colorBlue));
            holder.orderstatus.setText(arrayList.get(position).getOrderstatus());
        } else if (arrayList.get(position).getOrderstatus().equals("Confirm")) {
            holder.orderstatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.orderstatus.setText("Return");
        }
         else if (arrayList.get(position).getOrderstatus().equals("Returned")) {
            holder.orderstatus.setTextColor(context.getResources().getColor(R.color.profilePrimaryDark));
            holder.orderstatus.setText(arrayList.get(position).getOrderstatus());
        }
        else if (arrayList.get(position).getOrderstatus().equals("Cancel")) {
            holder.orderstatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.orderstatus.setText(arrayList.get(position).getOrderstatus());
        } else {
            holder.orderstatus.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.orderstatus.setText(arrayList.get(position).getOrderstatus());
        }
        holder.orderid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.ORDERID, arrayList.get(position).getOrderid()).commit();
                Intent intent = new Intent(context, OrderViewDetailActivity.class);
                context.startActivity(intent);
            }
        });
        if (sp.getString(ConstantSp.USER_TYPE, "").equals("Admin")) {
            if (arrayList.get(position).getOrderstatus().equals("Pending")) {
                holder.confirmLayout.setVisibility(View.VISIBLE);
                holder.confirm.setVisibility(View.VISIBLE);
                holder.cancel.setVisibility(View.VISIBLE);
                holder.returnOrder.setVisibility(View.GONE);
                holder.orderstatus.setVisibility(View.VISIBLE);
            }
            else if (arrayList.get(position).getOrderstatus().equals("Confirm")) {
                holder.confirmLayout.setVisibility(View.VISIBLE);
                holder.confirm.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.GONE);
                holder.returnOrder.setVisibility(View.VISIBLE);
                holder.orderstatus.setVisibility(View.VISIBLE);
            }
            else {
                holder.confirmLayout.setVisibility(View.GONE);
                holder.confirm.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.GONE);
                holder.orderstatus.setVisibility(View.VISIBLE);
            }
        } else {
            if (arrayList.get(position).getOrderstatus().equals("Pending")) {
                holder.confirmLayout.setVisibility(View.GONE);
                holder.orderstatus.setVisibility(View.VISIBLE);
            } else {
                holder.confirmLayout.setVisibility(View.GONE);
                holder.orderstatus.setVisibility(View.VISIBLE);
            }
        }

        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new ConnectionDetector(context).networkConnected()) {
                    sOrderId = arrayList.get(position).getOrderid();
                    sStatus = "Confirm";
                    new updateOrderStatus().execute();
                } else {
                    new ConnectionDetector(context).networkDisconnected();
                }
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new ConnectionDetector(context).networkConnected()) {
                    sOrderId = arrayList.get(position).getOrderid();
                    sStatus = "Cancelled";
                    new updateOrderStatus().execute();
                } else {
                    new ConnectionDetector(context).networkDisconnected();
                }
            }
        });

        holder.returnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new ConnectionDetector(context).networkConnected()) {
                    sOrderId = arrayList.get(position).getOrderid();
                    sStatus = "Returned";
                    new updateOrderStatus().execute();
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

    private class updateOrderStatus extends AsyncTask<String, String, String> {

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
            hashMap.put("action", "updateOrderStatus");
            hashMap.put("id", sOrderId);
            hashMap.put("status", sStatus);
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
                    context.startActivity(new Intent(context, OrderHistoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

