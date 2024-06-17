package ngodinhthang.apple.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.UndeclaredThrowableException;
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

public class SearchActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText edtsearch;
    AsusAdapter adapterAsus;
    List<SanPhamMoi> sanPhamMoiList;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiApple apiApple;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        initView();
        ActionToolBar();

    }

    private void initView() {
        sanPhamMoiList = new ArrayList<>();
        apiApple = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApple.class);

        toolbar = findViewById(R.id.toolbar);
        edtsearch = findViewById(R.id.edtsearch);
        recyclerView = findViewById(R.id.recycleview_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    sanPhamMoiList.clear();
                    adapterAsus = new AsusAdapter(getApplicationContext(), sanPhamMoiList);
                    recyclerView.setAdapter(adapterAsus);
                } else {
                    getDataSearch(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getDataSearch(String s) {
        sanPhamMoiList.clear();
        compositeDisposable.add(apiApple.search(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                   sanPhamMoiModel -> {
                       if (sanPhamMoiModel.isSuccess()){
                           sanPhamMoiList = sanPhamMoiModel.getResult();
                           adapterAsus = new AsusAdapter(getApplicationContext(), sanPhamMoiList);
                           recyclerView.setAdapter(adapterAsus);
                       }
                   },
                   throwable -> {
                       Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}