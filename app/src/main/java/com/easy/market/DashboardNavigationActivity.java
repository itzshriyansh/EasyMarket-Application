package com.easy.market;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DashboardNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView gridView;
    ArrayList<HomeList> homeLists;
    HomeAdapter homeAdapter;
    String[] id = {"1","2"};
    String[] courseName = {"BCA","MCA"};
    int[] courseImage = {R.drawable.bca,R.drawable.mca};
    SharedPreferences sp;
    FloatingActionButton add;
    private static final int STORAGE_PERMISSION_CODE = 123;
    //SearchView searchView;
    ArrayList<OrderHistoryList> orderHistoryLists;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestStoragePermission();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        /*searchView = findViewById(R.id.content_dashboard_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                sp.edit().putString(ConstantSp.SUB_CATEGORY_ID,"").commit();
                sp.edit().putString(ConstantSp.SEARCH,s).commit();
                startActivity(new Intent(DashboardNavigationActivity.this,ProductActivity.class));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/

        add = findViewById(R.id.dashboard_add);
        if(sp.getString(ConstantSp.USER_TYPE,"").equals("Admin")){
            add.setVisibility(View.VISIBLE);
        }
        else{
            add.setVisibility(View.GONE);
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.CATEGORY_FLAG, "Add").commit();
                sp.edit().putString(ConstantSp.CATEGORY_ID, "").commit();
                sp.edit().putString(ConstantSp.CATEGORY_NAME, "").commit();
                sp.edit().putString(ConstantSp.CATEGORY_IMAGE, "").commit();
                startActivity(new Intent(DashboardNavigationActivity.this, AddCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        View hView =  navigationView.getHeaderView(0);
        TextView header_name = (TextView)hView.findViewById(R.id.header_name);
        header_name.setText(sp.getString(ConstantSp.NAME, ""));

        TextView header_email = (TextView)hView.findViewById(R.id.header_email);
        header_email.setText(sp.getString(ConstantSp.EMAIL, ""));

        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        /*mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/
        gridView = findViewById(R.id.home_grid);
        gridView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        gridView.setItemAnimator(new DefaultItemAnimator());
        if(new ConnectionDetector(DashboardNavigationActivity.this).networkConnected()){
            new getData().execute();
            new getExcelData().execute();
        }
        else{
            new ConnectionDetector(DashboardNavigationActivity.this).networkDisconnected();
        }
        /*homeLists =  new ArrayList<>();
        for (int i = 0;i<courseName.length;i++)
        {
            HomeList list = new HomeList();
            list.setId(id[i]);
            list.setName(courseName[i]);
            list.setImage(courseImage[i]);
            homeLists.add(list);
        }
        homeAdapter = new HomeAdapter(DashboardNavigationActivity.this,homeLists);
        gridView.setAdapter(homeAdapter);*/

    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(sp.getString(ConstantSp.USER_TYPE,"").equals("Admin")){
            getMenuInflater().inflate(R.menu.dashboard_admin, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.dashboard, menu);
        }
        MenuItem searchViewItem = menu.findItem(R.id.content_dashboard_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                sp.edit().putString(ConstantSp.SUB_CATEGORY_ID,"").commit();
                sp.edit().putString(ConstantSp.SEARCH,s).commit();
                startActivity(new Intent(DashboardNavigationActivity.this, ProductActivity.class));
             /*   if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }*/
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_user_report)
        {
            //startActivity(new Intent(DashboardNavigationActivity.this,UserListActivity.class));
            saveExcelFile(DashboardNavigationActivity.this,"myExcel.xls");
        }
        if (id==R.id.action_user_list)
        {
            startActivity(new Intent(DashboardNavigationActivity.this, UserListActivity.class));
        }
        if (id==R.id.action_user_order_history)
        {
            startActivity(new Intent(DashboardNavigationActivity.this, OrderHistoryActivity.class));
        }
        if (id==R.id.action_add_to_cart)
        {
            startActivity(new Intent(DashboardNavigationActivity.this, CartActivity.class));
        }
        /*if (id==R.id.action_order_history)
        {
            startActivity(new Intent(DashboardNavigationActivity.this,OrderHistoryActivity.class));
        }*/
        if (id==R.id.action_wishlist)
        {
            startActivity(new Intent(DashboardNavigationActivity.this, WishlistActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

     boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("myOrder");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("OrderId");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Customer Name");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Payment Method");
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Price");
        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue("Order Status");
        c.setCellStyle(cs);

        c = row.createCell(5);
        c.setCellValue("Order Date");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));
        sheet1.setColumnWidth(4, (15 * 500));
        sheet1.setColumnWidth(5, (15 * 500));

        int rowNum = 1;
        for (int i=0;i<orderHistoryLists.size();i++) {
            Row row1 = sheet1.createRow(rowNum++);
            row1.createCell(0).setCellValue(orderHistoryLists.get(i).getOrderid());
            row1.createCell(1).setCellValue(orderHistoryLists.get(i).getName());
            row1.createCell(2).setCellValue(orderHistoryLists.get(i).getTransactionId());
            row1.createCell(3).setCellValue(orderHistoryLists.get(i).getOrderprice());
            row1.createCell(4).setCellValue(orderHistoryLists.get(i).getOrderstatus());
            row1.createCell(5).setCellValue(orderHistoryLists.get(i).getOrderdate());
        }
         Toast.makeText(context, "Save File In Folder", Toast.LENGTH_SHORT).show();
        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }

        return success;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if(id==R.id.nav_home){
            startActivity(new Intent(DashboardNavigationActivity.this,DashboardNavigationActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        if(id==R.id.nav_add_cart){
            startActivity(new Intent(DashboardNavigationActivity.this, CartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        if(id==R.id.nav_wishlist){
            startActivity(new Intent(DashboardNavigationActivity.this, WishlistActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        if(id==R.id.nav_order_history){
            startActivity(new Intent(DashboardNavigationActivity.this, OrderHistoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        if(id==R.id.nav_logout){
            sp.edit().clear().commit();
            startActivity(new Intent(DashboardNavigationActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private class getData extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(DashboardNavigationActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("action", "getCategory");
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL+"user_management.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getString("Status").equals("True")){
                    homeLists =  new ArrayList<>();
                    JSONArray array = jsonObject.getJSONArray("response");
                    for (int i = 0;i<array.length();i++)
                    {
                        JSONObject object = array.getJSONObject(i);
                        HomeList list = new HomeList();
                        list.setId(object.getString("id"));
                        list.setName(object.getString("name"));
                        list.setImage(object.getString("image"));
                        homeLists.add(list);
                    }
                    homeAdapter = new HomeAdapter(DashboardNavigationActivity.this,homeLists);
                    gridView.setAdapter(homeAdapter);
                }
                else{
                    Toast.makeText(DashboardNavigationActivity.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class getExcelData extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(DashboardNavigationActivity.this);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("action", "getOrderList");
            hashMap.put("userType", sp.getString(ConstantSp.USER_TYPE,""));
            hashMap.put("userId", sp.getString(ConstantSp.ID,""));
            return new MakeServiceCall().MakeServiceCall(ConstantSp.BASEURL+"user_management.php", MakeServiceCall.POST , hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if(object.getString("Status").equals("True")){
                    orderHistoryLists = new ArrayList<>();
                    JSONArray array = object.getJSONArray("response");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        OrderHistoryList list = new OrderHistoryList();
                        list.setOrderid(jsonObject.getString("id"));
                        if(jsonObject.getString("paymentType").equalsIgnoreCase("Cash On Delivery")) {
                            list.setTransactionId(jsonObject.getString("paymentType"));
                        }
                        else{
                            list.setTransactionId(jsonObject.getString("paymentType")+" ("+jsonObject.getString("transactionId")+")");
                        }
                        list.setOrderprice(jsonObject.getString("price"));
                        list.setName(jsonObject.getString("name"));
                        //list.setOrderprice(jsonObject.getString("price"));
                        list.setOrderdate(jsonObject.getString("created_date"));
                        list.setOrderstatus(jsonObject.getString("status"));
                        orderHistoryLists.add(list);
                    }
   }
                else{
                    Toast.makeText(DashboardNavigationActivity.this, object.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
