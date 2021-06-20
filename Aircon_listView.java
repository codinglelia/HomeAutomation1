package com.example.bluetest3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Aircon_listView extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Aircon_listData> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    Button backBtn; // 뒤로가기 버튼
    String user; // 사용자 아이디

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aircon_listview);

        Intent getAirIntent = getIntent(); // AirconControl 에서 보내진 인텐트를 받아옴
        user = getAirIntent.getStringExtra("user"); // 로그인 한 사용자 ID (user) 값을 String 변수 user에 저장

        backBtn = findViewById(R.id.backBtn); // 뒤로가기 버튼

        recyclerView = findViewById(R.id.recyclerView); // 리사이클러뷰 연결
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this); // Linear layout 이용
        recyclerView.setLayoutManager(layoutManager); // 리사이클러뷰에 레이아웃 매니저를 설정

        arrayList = new ArrayList<>(); // User 객체를 담을 어레이 리스트 (어댑터쪽으로)

        database = FirebaseDatabase.getInstance("https://airconinfo-a814a-default-rtdb.firebaseio.com/"); // 파이어베이스 데이터베이스 연동

        databaseReference = database.getReference("에어컨"); // 데이터베이스 중 키 값이 에어컨인 데이터와 연결


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); // 기존 배열리스트가 존재하지않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄
                    Aircon_listData Aircon = snapshot.getValue(Aircon_listData.class); // Aircon_listData의 에어컨 1, 에어컨 2 , 에어컨 n 데이터를 가져옴
                    arrayList.add(Aircon); // ArrayList에 가져온 데이터를 출력
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("AirInfo", String.valueOf(databaseError.toException())); // 에러문 출력
            }


        });

        adapter = new AirconAdapter(arrayList, this); // 에어컨 어댑터 생성
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAir = new Intent(); // 인텐트 객체 생성 후
                intentAir.putExtra("user", user); // name 의 값을 부가 데이터로 넣어준 후
                setResult(RESULT_OK, intentAir); // AirconControl 에게 응답 보냄
                finish();
            }
        });
    }


}