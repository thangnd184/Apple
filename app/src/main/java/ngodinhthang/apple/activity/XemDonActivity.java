package ngodinhthang.apple.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ngodinhthang.apple.R;
import ngodinhthang.apple.adapter.DonHangAdapter;
import ngodinhthang.apple.retrofit.ApiApple;
import ngodinhthang.apple.retrofit.RetrofitClient;
import ngodinhthang.apple.utils.Utils;

public class XemDonActivity extends AppCompatActivity {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiApple apiApple;
    RecyclerView redonhang;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_xem_don);
        initView();
        InitToolbar();
        getOrder();



    }

    private void getOrder() {
        compositeDisposable.add(apiApple.xemDonHang(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            DonHangAdapter adapter = new DonHangAdapter(getApplicationContext(), donHangModel.getResult());
                            redonhang.setAdapter(adapter);

                        },
                        throwable -> {

                        }
                ));
    }

    private void InitToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        redonhang = findViewById(R.id.recycleview_donhang);
        toolbar = findViewById(R.id.toolbar);
        apiApple = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApple.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        redonhang.setLayoutManager(layoutManager);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}