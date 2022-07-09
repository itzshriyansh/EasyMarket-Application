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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    Context context;
    ArrayList<HomeList> arrayList;
    SharedPreferences sp;

    public HomeAdapter(FragmentActivity activity, ArrayList<HomeList> homeLists) {
        this.context = activity;
        this.arrayList = homeLists;
        sp = context.getSharedPreferences(ConstantSp.PREF, Context.MODE_PRIVATE);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView, edit, delete;
        LinearLayout editLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.custome_home_name);
            imageView = itemView.findViewById(R.id.custome_home_iv);
            edit = itemView.findViewById(R.id.custom_home_edit);
            delete = itemView.findViewById(R.id.custom_home_delete);
            editLayout = itemView.findViewById(R.id.custom_home_edit_layout);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custome_home, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView.setText(arrayList.get(position).getName());
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
                sp.edit().putString(ConstantSp.CATEGORY_FLAG, "Edit").commit();
                sp.edit().putString(ConstantSp.CATEGORY_ID, arrayList.get(position).getId()).commit();
                sp.edit().putString(ConstantSp.CATEGORY_NAME, arrayList.get(position).getName()).commit();
                sp.edit().putString(ConstantSp.CATEGORY_IMAGE, arrayList.get(position).getImage()).commit();
                context.startActivity(new Intent(context, AddCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new ConnectionDetector(context).networkConnected()) {
                    sp.edit().putString(ConstantSp.CATEGORY_ID, arrayList.get(position).getId()).commit();
                    new deleteData().execute();
                } else {
                    new ConnectionDetector(context).networkDisconnected();
                }
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.CATEGORY_ID, arrayList.get(position).getId()).commit();
                context.startActivity(new Intent(context, SubCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.CATEGORY_ID, arrayList.get(position).getId()).commit();
                context.startActivity(new Intent(context, SubCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
            hashMap.put("action", "deleteCategory");
            hashMap.put("id", sp.getString(ConstantSp.CATEGORY_ID, ""));
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL + "user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("Status").equals("True")) {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, DashboardNavigationActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    Toast.makeText(context, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

/*    @Override
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
        view = layoutInflater.inflate(R.layout.custome_home, null);
        ImageView imageView = view.findViewById(R.id.custome_home_iv);
        TextView textView = view.findViewById(R.id.custome_home_name);
        textView.setText(arrayList.get(i).getName());
        imageView.setImageResource(arrayList.get(i).getImage());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, SubCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, SubCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        return view;
    }*/
}
