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

import org.w3c.dom.Text;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ngodinhthang.apple.R;
import ngodinhthang.apple.retrofit.ApiApple;
import ngodinhthang.apple.retrofit.RetrofitClient;
import ngodinhthang.apple.utils.Utils;

public class DangKyActivity extends AppCompatActivity {
    EditText email, password, repassword, mobile, username;
    AppCompatButton button;
    ApiApple apiApple;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ky);
        initView();
        initControl();
    }

    private void initControl() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangKy();
            }
        });
    }

    private void dangKy() {
        String str_email = email.getText().toString().trim();
        String str_pass = password.getText().toString().trim();
        String str_repass = repassword.getText().toString().trim();
        String str_mobile = mobile.getText().toString().trim();
        String str_username = username.getText().toString().trim();
        if (TextUtils.isEmpty(str_email)){
            Toast.makeText(getApplicationContext(),"Please enter Email", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(str_pass)){
            Toast.makeText(getApplicationContext(),"Please enter Password", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(str_repass)){
            Toast.makeText(getApplicationContext(),"Please enter Password again", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(str_mobile)){
            Toast.makeText(getApplicationContext(),"Please enter Mobile", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(str_username)){
            Toast.makeText(getApplicationContext(),"Please enter Username", Toast.LENGTH_LONG).show();
        } else {
            if (str_pass.equals(str_repass)){
                //post data
                compositeDisposable.add(apiApple.dangKy(str_email, str_pass, str_username, str_mobile)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                userModel -> {
                                    if (userModel.isSuccess()){
                                        Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_LONG).show();
                                        Utils.user_current.setEmail(str_email);
                                        Utils.user_current.setPass(str_pass);
                                        Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),userModel.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                },
                                throwable -> {
                                    Toast.makeText(getApplicationContext(),throwable.getMessage(), Toast.LENGTH_LONG).show();
                                }
                        ));
            } else{
                Toast.makeText(getApplicationContext(),"Password incorrect", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initView() {
        apiApple = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApple.class);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        mobile = findViewById(R.id.mobile);
        username = findViewById(R.id.username);
        button = findViewById(R.id.btndangky);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}