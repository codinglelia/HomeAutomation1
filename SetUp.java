package com.example.bluetest3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Set;


public class SetUp extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;

    private FirebaseAuth firebaseAuth; // 파이어베이스 사용자 인증
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    Button mLoginBtn; // 로그인 버튼 부분
    Button mLoginClrBtn; // 로그인 초기화 부분
    EditText inputId; // 사용자 ID 입력 부분
    EditText inputPw; // 사용자 PW 입력 부분
    String email; // 사용자 이메일(ID)
    String pwd; // 사용자 비밀번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);


        // 레이아웃 버튼과 연결
        inputId = findViewById(R.id.EditId);
        inputPw = findViewById(R.id.EditPw);
        mLoginBtn = findViewById(R.id.Login);
        mLoginClrBtn = findViewById(R.id.LoginClear);
        firebaseAuth = FirebaseAuth.getInstance();

        // 로그인 버튼 클릭시
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputId.getText().toString().trim(); // 스페이스 제외 후 editText에 입력된 사용자 ID를 저장
                pwd = inputPw.getText().toString().trim(); // 스페이스 제외 후 editText에 입력된 사용자 비밀번호를 저장
                if (!email.equals("") && !pwd.equals("")) { // 위에서 가져온 이메일과 암호가 비어있지 않다면
                    loginUser(email, pwd); // 로그인 시켜줌
                    inputId.setText(""); // 로그인 후 ID editText 초기화
                    inputPw.setText(""); // 로그인 후 PW editText 초기화
                } else { // 아무것도 입력되지 않았다면
                    Toast.makeText(SetUp.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // 로그인 성공시
                            Intent intentNavi = new Intent(getApplicationContext(), Navigation.class); // Navigation 으로 이동
                             // EditText 값을 바로 전달할 수 없으므로 String형으로 변환
                            intentNavi.putExtra("user", email); // 사용자의 이메일을 user라는 이름으로 전달
                            startActivityForResult(intentNavi, REQUEST_CODE); // 인텐트 송출
                        } else {
                            // 로그인 실패시
                            Toast.makeText(SetUp.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // startActivityForResult() 후 바로 실행되며 Navigation 으로부터 받은 응답을 처리하는 메서드

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE){

            if (resultCode == RESULT_OK){
                String user = data.getStringExtra("user");
                Toast.makeText(getApplicationContext(), user + "님, 로그아웃 되었습니다.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }
    public void onStart() {
        super.onStart();
    }
}
