package com.example.dulichhaiphong.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dulichhaiphong.Adapter.Baiviet_Adapter;
import com.example.dulichhaiphong.Adapter.Slider_Adapter;
import com.example.dulichhaiphong.Fragment.Fragment_search_langnghe;
import com.example.dulichhaiphong.Fragment.Fragment_search_nhanvatlichsu;
import com.example.dulichhaiphong.Model.Anh_Slider_URL;
import com.example.dulichhaiphong.Model.Baiviet;
import com.example.dulichhaiphong.Model.DanhMuc;
import com.example.dulichhaiphong.Model.Timkiemlangnghe;
import com.example.dulichhaiphong.R;
import com.example.dulichhaiphong.ultil.CheckConNection;
import com.example.dulichhaiphong.ultil.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LangngheActivity extends AppCompatActivity implements Timkiemlangnghe {
    private Toolbar toolbar;
    private ViewPager2 viewPager2;
    private Baiviet_Adapter baiviet_adapter;
    private Slider_Adapter slider_url_apdapter;
    private RecyclerView recyclerView;
    private ImageView imgDammayLeft;
    private ImageView imgDammayRight,imgBack;
    private Handler sliderHander = new Handler();
    private ArrayList<Anh_Slider_URL> arraySlider;
    private ArrayList<Baiviet> dsBaiViet;
    private ArrayList<DanhMuc> arrayDanhMuc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_langnghe);
        toolbar = (Toolbar) findViewById(R.id.toolbar_frament);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(CheckConNection.haveNetwordConnection(getApplicationContext())){
            Loaddulieu_Fragment();
            Anhxa();
            HienLRecyclerView();
            Hieuungmay();
            HienSilder();
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                    finish();
                }
            });
        }else {
            CheckConNection.ShowToast_Short(getApplicationContext(),"Mời bạn kiểm tra lại kết nối Internet");
            finish();
        }
    }
    private void Anhxa(){
        viewPager2 = (ViewPager2) findViewById(R.id.viewPageSlider);
        recyclerView = findViewById(R.id.recyclerview_Baiview);
        imgDammayLeft = findViewById(R.id.imgDamMayLeft);
        imgDammayRight =findViewById(R.id.imgDamMayRight);
        imgBack = findViewById(R.id.back);
    }
    private void Hieuungmay(){
        Animation mayBayTrai = AnimationUtils.loadAnimation(this,R.anim.translate_left);
        imgDammayLeft.startAnimation(mayBayTrai);
        Animation mayBayPhai = AnimationUtils.loadAnimation(this,R.anim.translate_right);
        imgDammayRight.startAnimation(mayBayPhai);
    }
    private void HienLRecyclerView(){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        baiviet_adapter = new Baiviet_Adapter(this.getApplicationContext(),dsBaiViet);
        recyclerView.setAdapter(baiviet_adapter);
    }
    private void HienSilder(){
        slider_url_apdapter = new Slider_Adapter(arraySlider,viewPager2);
        viewPager2.setAdapter(slider_url_apdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f+r*0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.setCurrentItem(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHander.removeCallbacks(sliderRunable);
                sliderHander.postDelayed(sliderRunable,3500);
            }
        });

    }
    private Runnable sliderRunable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderHander.removeCallbacks(sliderRunable);
    }
    @Override
    public void onResume() {
        super.onResume();
        sliderHander.postDelayed(sliderRunable,3500);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem search_item = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) search_item.getActionView();
        searchView.setFocusable(false);
        searchView.setBackgroundResource(R.drawable.bg_white_rounded);
        searchView.setQueryHint("Tìm kiếm bài viết");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                baiviet_adapter.getFilter().filter(s);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.search_view:
                return true;
            case R.id.filter_view:
                Fragment_search_langnghe fragment_search_langnghe = new Fragment_search_langnghe();
                Bundle bundle = new Bundle();
                bundle.putSerializable("tenDanhMuc",arrayDanhMuc);
                fragment_search_langnghe.setArguments(bundle);
                fragment_search_langnghe.show(getFragmentManager(),"Bộ Lọc");
                return true;
        }
        return true;
    }
    private void Loaddulieu_Fragment(){
        LoadFilter();
        LoadBaiviet();
        Load_Slider();
    }
    private void LoadBaiviet(){
        dsBaiViet = new ArrayList<>();
        Timkiem_baiviet(Server.url_read_baiviet_langnghe,dsBaiViet,"");
    }
    private void LoadFilter(){
        arrayDanhMuc = new ArrayList<>();
        arrayDanhMuc.add(new DanhMuc(0,"Chọn tất cả"));
        ReadFilter(Server.url_read_capcongnhan,arrayDanhMuc);
    }

    private void ReadFilter(String url_read_data_filter, final ArrayList<DanhMuc> arrayList){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_read_data_filter, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("read");
                    if(success.equals("1")){
                        for(int i = 0; i<jsonArray.length();i++){
                            JSONObject jsonten = jsonArray.getJSONObject(i);
                            Integer id = Integer.parseInt(jsonten.getString("Id"));
                            String tenNhanVat = jsonten.getString("Tencap");
                            arrayList.add(new DanhMuc(id,tenNhanVat));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LangngheActivity.this,"Lỗi",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LangngheActivity.this,"Lỗi",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("key",Server.key);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void Load_Slider(){
        arraySlider = new ArrayList<>();
        Read_anhSlider(Server.url_read_Slider,arraySlider,"Làng nghề");
    }

    private void Read_anhSlider(String url_read_Slider, final ArrayList<Anh_Slider_URL> arrayAnh, final String tenTilte){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_read_Slider, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if(response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("read");
                        if(success.equals("1")){
                            for(int i=0 ; i< jsonArray.length();i++){
                                JSONObject objectAnh = jsonArray.getJSONObject(i);
                                String tenFile = objectAnh.getString("tenFile");
                                arrayAnh.add(new Anh_Slider_URL(tenFile));
                            }
                            slider_url_apdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LangngheActivity.this,"Lỗi load du lieu ảnh",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LangngheActivity.this,"Lỗi load du lieu ảnh",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("tenDanhMuc",tenTilte);
                params.put("key",Server.key);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void Timkiem_baiviet(final String url_read_danhsach_baiviet, final ArrayList<Baiviet> danhSachBaiviet, final String tenDanhmuc){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_read_danhsach_baiviet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if(response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("baiviet");
                        if(success.equals("1")){
                            for(int i = 0; i<jsonArray.length();i++){
                                JSONObject jsonBaiviet = jsonArray.getJSONObject(i);
                                String id = jsonBaiviet.getString("Id");
                                String tenBaiviet = jsonBaiviet.getString("TenBaiViet");
                                String tomtat = jsonBaiviet.getString("TomTat");
                                String anhDaidien = jsonBaiviet.getString("AnhDaiDien");
                                String soLike = jsonBaiviet.getString("SoLike");
                                String ngayDang = jsonBaiviet.getString("NgayDang");
                                String noiDung = jsonBaiviet.getString("Noidung");
                                danhSachBaiviet.add(new Baiviet(id,tenBaiviet,tomtat,soLike,ngayDang,anhDaidien,noiDung));

                            }
                            baiviet_adapter.notifyDataSetChanged();
                            if(danhSachBaiviet.size() ==0){
                                Toast.makeText(LangngheActivity.this,"Không tìm có bài viết nào!",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(LangngheActivity.this,"Có " + danhSachBaiviet.size()+" bài viết!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LangngheActivity.this,"Lỗi load dữ liệu danh sách bài viết",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LangngheActivity.this,"Lỗi load dữ liệu danh sách bài viết",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("tenDanhMuc",tenDanhmuc);
                params.put("key",Server.key);
                return  params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void Timkiem(String tenCapcongnhan) {
        dsBaiViet.clear();
        Timkiem_baiviet(Server.url_read_baiviet_langnghe,dsBaiViet,tenCapcongnhan);
    }
}