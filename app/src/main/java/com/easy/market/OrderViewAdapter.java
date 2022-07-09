package com.easy.market;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class OrderViewAdapter extends RecyclerView.Adapter<OrderViewAdapter.MyViewHolder>  {

    Context context;
    ArrayList<OrderViewList> arrayList;

    public OrderViewAdapter(OrderViewDetailActivity activity, ArrayList<OrderViewList> OrderViewLists) {
        this.context = activity;
        this.arrayList = OrderViewLists;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;
        TextView price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.custome_order_view_iv);
            textView = itemView.findViewById(R.id.custome_order_view_name);
            price = itemView.findViewById(R.id.custome_order_view_price);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_order_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(arrayList.get(position).getName());
        holder.price.setText(arrayList.get(position).getPrice());
        //holder.imageView.setImageResource(arrayList.get(position).getImage());
        Picasso.with(context).load(arrayList.get(position).getImage()).placeholder(R.mipmap.ic_launcher).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}

