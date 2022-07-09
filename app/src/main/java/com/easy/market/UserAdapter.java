package com.easy.market;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder>  {

    Context context;
    ArrayList<UserList> arrayList;

    public UserAdapter(UserListActivity activity, ArrayList<UserList> UserLists) {
        this.context = activity;
        this.arrayList = UserLists;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,email,contact;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.custome_user_list_name);
            email = itemView.findViewById(R.id.custome_user_list_email);
            contact = itemView.findViewById(R.id.custome_user_list_contact);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_user_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(arrayList.get(position).getName());
        holder.email.setText(arrayList.get(position).getEmail());
        holder.contact.setText(arrayList.get(position).getContact());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}


