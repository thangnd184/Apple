package ngodinhthang.apple.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ngodinhthang.apple.R;
import ngodinhthang.apple.retrofit.ApiApple;
import ngodinhthang.apple.retrofit.RetrofitClient;
import ngodinhthang.apple.utils.Utils;

public class DangNhapActivity extends AppCompatActivity {
    TextView txtdangky, txtresetpass;
    EditText email, password;
    AppCompatButton btndangnhap;
    ApiApple apiApple;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);
        initView();
        initControl();
    }

    private void initControl() {
        txtdangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DangKyActivity.class);
                startActivity(intent);
            }
        });

        txtresetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString().trim();
                String str_pass = password.getText().toString().trim();
                if (TextUtils.isEmpty(str_email)){
                    Toast.makeText(getApplicationContext(),"Please enter Email", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(str_pass)){
                    Toast.makeText(getApplicationContext(),"Please enter Password", Toast.LENGTH_LONG).show();
                } else {
                    //Save
                    Paper.book().write("email", str_email);
                    Paper.book().write("pass", str_pass);
                    compositeDisposable.add(apiApple.dangNhap(str_email, str_pass)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                               userModel -> {
                                   if (userModel.isSuccess()){
                                       Utils.user_current = userModel.getResult().get(0);
                                       Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                       startActivity(intent);
                                       finish();
                                   }
                               },
                               throwable -> {
                                   Toast.makeText(getApplicationContext(),throwable.getMessage(), Toast.LENGTH_LONG).show();
                               }
                            ));
                }
            }
        });

    }

    private void initView() {
        Paper.init(this);
        apiApple = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApple.class);
        txtdangky = findViewById(R.id.txtdangki);
        txtresetpass = findViewById(R.id.txtresetpass);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btndangnhap = findViewById(R.id.btndangnhap);

        //Read data
        if (Paper.book().read("email") != null && Paper.book().read("pass") != null){
            email.setText(Paper.book().read("email"));
            password.setText(Paper.book().read("pass"));
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        if(Utils.user_current.getEmail() != null && Utils.user_current.getPass() != null){
            email.setText(Utils.user_current.getEmail());
            password.setText(Utils.user_current.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}