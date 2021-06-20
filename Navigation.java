package com.example.bluetest3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class Navigation extends AppCompatActivity {
    public static final int REQUEST_CODE = 2; // Navigation 에서 쓰이는 엔텐트 코드 설정

    Button OnLedControl; // 조명 제어
    Button OnAirconControl; // 에어컨 제어
    Button OnClnControl; // 공기청정기 제어
//    Button OnUserInfo; // 회원 정보

    TextView printUserID; // 로그인한 사용자의 ID를 출력
    Button LogOut; // 로그아웃 버튼
    String user; // 인텐트로 받아온 사용자의 이메일을 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // 레이아웃 버튼과 연결
        OnLedControl = findViewById(R.id.LedControlBtn);
        OnAirconControl = findViewById(R.id.airconControlBtn);
        OnClnControl = findViewById(R.id.clnControlBtn);
//        OnUserInfo = findViewById(R.id.OnUserInfo);
        LogOut = findViewById(R.id.LogOut);

        // 인텐트로 넘어온 사용자 아이디를 처리
        printUserID = findViewById(R.id.userID);
        Intent secondIntent = getIntent();
//        secondIntent.getStringExtra("name");
        user = secondIntent.getStringExtra("user");  // 이름이 name 인 데이터를 가져옴
        printUserID.setText(user); // 텍스트뷰에 전달받은 name을 띄워줌
        Toast.makeText(getApplicationContext(), user + "님, 기능을 제어하기 위해 버튼을 눌러주세요.",
                Toast.LENGTH_LONG).show();


        // 조명 버튼 클릭 시
        OnLedControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLed = new Intent(getApplicationContext(), LedControl.class); // 조명 제어 화면으로 이동
                intentLed.putExtra("user", user); // 사용자 아이디 전달
                startActivityForResult(intentLed, REQUEST_CODE);   // 인텐트 송출 후 onActivityResult() 실행
            }
        });

        OnAirconControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAir = new Intent(getApplicationContext(), AirconControl.class); // 에어컨 제어 화면으로 이동
                intentAir.putExtra("user", user); // 사용자 아이디 전달
                startActivityForResult(intentAir, REQUEST_CODE);  // 인텐트 송출 후 onActivityResult() 실행
            }
        });

        OnClnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCln = new Intent(getApplicationContext(), ClnControl_B.class); // 공기청정기 제어 화면으로 이동
                intentCln.putExtra("user", user); // 사용자 아이디 전달
                startActivityForResult(intentCln, REQUEST_CODE);  // 인텐트 송출 후 onActivityResult() 실행
            }
        });

/*
        OnUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
*/
        // 로그아웃 버튼 클릭 시
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin = new Intent(); // 인텐트 객체 생성 후
                intentLogin.putExtra("user", user); // name 의 값을 부가 데이터로 넣어준 후
                setResult(RESULT_OK, intentLogin); // SetUP 로 응답
                finish(); // 액티비티 종료
            }
        });
    }

    // startActivityForResult() 후 바로 실행되며 조명, 에어컨, 공기청정기 제어 액티비티로부터 받은 응답을 처리하는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
//                String user = data.getStringExtra("user");
                Toast.makeText(getApplicationContext(), "원하는 기능을 선택하세요.",
                        Toast.LENGTH_LONG).show();
            }
        }

    }
}
