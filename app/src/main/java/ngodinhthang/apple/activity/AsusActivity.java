package ngodinhthang.apple.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ngodinhthang.apple.R;
import ngodinhthang.apple.adapter.AsusAdapter;
import ngodinhthang.apple.model.SanPhamMoi;
import ngodinhthang.apple.retrofit.ApiApple;
import ngodinhthang.apple.retrofit.RetrofitClient;
import ngodinhthang.apple.utils.Utils;

public class AsusActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiApple apiApple;
    int page = 1;
    int loai;
    AsusAdapter adapterAsus;
    List<SanPhamMoi> sanPhamMoiList;
    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asus);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiApple = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApple.class);
        loai = getIntent().getIntExtra("loai", 1);

        Anhxa();
        ActionToolBar();
        getData(page);
        addEventLoad();
    }

    private void addEventLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading == false){
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == sanPhamMoiList.size()-1){
                        isLoading = true;
                        LoadMore();
                    }
                }
            }
        });
    }

    private void LoadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //add null
                sanPhamMoiList.add(null);
                adapterAsus.notifyItemInserted(sanPhamMoiList.size()-1);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //remove null
                sanPhamMoiList.remove(sanPhamMoiList.size()-1);
                adapterAsus.notifyItemRemoved(sanPhamMoiList.size());
                page = page + 1;
                getData(page);
                adapterAsus.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

    private void getData(int page) {
        compositeDisposable.add(apiApple.getSanPham(page, loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                   sanPhamMoiModel -> {
                       if (sanPhamMoiModel.isSuccess()){
                           if (adapterAsus == null){
                               sanPhamMoiList = sanPhamMoiModel.getResult();
                               adapterAsus = new AsusAdapter(getApplicationContext(), sanPhamMoiList);
                               recyclerView.setAdapter(adapterAsus);
                           } else {
                               int vitri = sanPhamMoiList.size()-1;
                               int soluongadd = sanPhamMoiModel.getResult().size();
                               for (int i = 0; i < soluongadd; i++){
                                   sanPhamMoiList.add(sanPhamMoiModel.getResult().get(i));
                               }
                               adapterAsus.notifyItemRangeInserted(vitri,soluongadd);
                           }

                       } else {
                           Toast.makeText(getApplicationContext(), "Out of product", Toast.LENGTH_LONG).show();
                           isLoading = true;
                       }
                   },
                   throwable -> {
                       Toast.makeText(getApplicationContext(),"Cannot connect with Server", Toast.LENGTH_LONG).show();
                   }
                ));
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Anhxa() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycleview_asus);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        sanPhamMoiList = new ArrayList<>();

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}