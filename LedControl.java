package com.example.bluetest3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static java.sql.Types.NULL;

public class LedControl extends AppCompatActivity {
    public static final int REQUEST_CODE = 5; // 조명에서 쓰이는 엔텐트 코드 설정 
    
    private BluetoothSPP bt; // 블루투스 연동 
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://airconinfo-a814a-default-rtdb.firebaseio.com/"); // 파이어베이스의 프로젝트와 연동
    private DatabaseReference databaseReferenceLedInfo = firebaseDatabase.getReference("조명"); // LED (테이블)과 연동
    int cnt; // 파이어베이스에 데이터의 순서를 지정
 
    TextView printUserID; // 사용자 ID 출력 부분  
    Button getLedInfoBtn; // 조명 관리 버튼 
    Button backBtn; // 뒤로가기 버튼 
    String selectLED; // 조명 1,2,3 
    String ledOnOffStateInfo; // On/Off 
    String getTime; // 사용자가 조명 on/off 버튼을 눌렀을 때의 시간
    TextView timeText; // getTime 출력 부분 
    String user; // 사용자 이메일
    
    int ledOnOffState = 4; //조명 on/off  1 : on, 0 : off
    // 조명 초기화
    int led1 = 4;
    int led2 = 4;
    int led3 = 4;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);

        bt = new BluetoothSPP(this); // 블루투스 초기화

        Toast.makeText(getApplicationContext(), "조명 기능 제어 화면입니다.\n블루투스를 연결해주세요.",
                Toast.LENGTH_LONG).show();
        // 사용자 아이디(이메일)를 가져오는 인텐트 부분
        Intent getLedIntent = getIntent();
        user = getLedIntent.getStringExtra("user");
        printUserID = findViewById(R.id.userID);
        printUserID.setText(user);


        // 레이아웃 버튼과 연결
        timeText = findViewById(R.id.TimeResultCln);  // 몇시에 켜졌는지 꺼졌는지 보여주는 텍스트
        backBtn = findViewById(R.id.backBtn); // 뒤로가기 버튼
        getLedInfoBtn = findViewById(R.id.getLedInfo); // 조명 관리 버튼(DB정보 확인)

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        // 아두이노와 블루투스 통신시 데이터 수신 부분
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                setup();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //블루투스 연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { // 블루투스 연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //블루투스 연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnConnect = findViewById(R.id.bluetoothCnt); //블루투스 연결시도
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect(); // 블루투스 연동된 상태에서 다시 블루투스 버튼을 클릭하면 블루투스 연결 해제
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class); // 블루투스로 페어링 된 기기목록 표시
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });


        // 뒤로가기 버튼 클릭시
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNavi = new Intent(); // 인텐트 객체 생성 후
                intentNavi.putExtra("user", user); // name 의 값을 부가 데이터로 넣어준 후
                setResult(RESULT_OK, intentNavi); // Navigation 에게 응답 보냄
                finish(); // 액티비티 종료
            }
        });

        // 조명 관리 버튼 클릭시
        getLedInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intentLedInfo = new Intent(getApplicationContext(), Led_listView.class); // 조명 제어 화면으로 이동
                intentLedInfo.putExtra("user",user); // 이동하면서 사용자 정보 전달
                startActivityForResult(intentLedInfo, REQUEST_CODE); // 인텐트 송출 후 onActivityResult() 실행
            }
        });
    }

    // startActivityForResult() 후 바로 실행되며 Led_listView 로부터 받은 응답을 처리하는 메서드
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
                // 액티비티가 이동하면서 끊기는 블루투스 문제를 여기서 해결할 수 있어 보임
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리 연결
            }
        }
    }

    // 조명 제어 부분
    public void setup() {

        //------------시간
        long time = System.currentTimeMillis();
        Date mDate = new Date(time);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        getTime = simpleDate.format(mDate);

        // ------------------- LED 1
        Button LedAOnButton = findViewById(R.id.LedAOn); //On버튼 눌렀을 때
        LedAOnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("a",true);  //블루투스를 통해 아두이노로 a 값을 보냄
                ledOnOffState = 1;
                led1 = 1;
            }
        });


        Button LedAOffButton = findViewById(R.id.LedAOff); //Off버튼 눌렀을 때
        LedAOffButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("b",true);    //블루투스를 통해 아두이노로 b 값을 보냄
                ledOnOffState = 0;
                led1 = 0;
            }
        });

        // ------------------- LED 2
        Button LedBOnButton = findViewById(R.id.LedBOn); //On버튼 눌렀을 때
        LedBOnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("c",true);  //블루투스를 통해 아두이노로 c 값을 보냄
                ledOnOffState = 1;
                led2 = 1;
            }
        });


        Button LedBOffButton = findViewById(R.id.LedBOff); //Off버튼 눌렀을 때
        LedBOffButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("d",true);    //블루투스를 통해 아두이노로 d 값을 보냄
                ledOnOffState = 0;
                led2 = 0;
            }

            });

        // ------------------- LED 3
        Button LedCOnButton = findViewById(R.id.LedCOn); //On버튼 눌렀을 때
        LedCOnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("e",true);  //블루투스를 통해 아두이노로 e 값을 보냄
                ledOnOffState = 1;
                led3 = 1;
            }
        });


        Button LedCOffButton = findViewById(R.id.LedCOff); //Off버튼 눌렀을 때
        LedCOffButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("f",true);    //블루투스를 통해 아두이노로 f 값을 보냄
                ledOnOffState = 0;
                led3 = 0;
            }
        });

      // ------------------- LED ALL
        Button LedAllOnButton = findViewById(R.id.LedAllOn); //On버튼 눌렀을 때
        LedAllOnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("g",true);  //블루투스를 통해 아두이노로 g 값을 보냄
                ledOnOffState = 1;
                led1 = 1 ; led2 = 1; led3 = 1;
            }
        });


        Button LedAllOffButton = findViewById(R.id.LedAllOff); // Off버튼 눌렀을 때
        LedAllOffButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("h",true);    //블루투스를 통해 아두이노로 h 값을 보냄
                ledOnOffState = 0;
                led1 = 0 ; led2 = 0; led3 = 0;
            }
        });

        if(ledOnOffState == 1) { // 수동으로 On이 되었을 때
            ledOnOffStateInfo = "On";
            if(led1 == 1 && led2 == 1 && led3==1) {
                selectLED = "전체";
            }
            else if(led1 == 1) {
                System.out.println("LED A ON");
                selectLED = "1";
                led1++;
            }
            else if(led2 == 1) {
                System.out.println("LED B ON");
                selectLED = "2";
                led2++;
            }
            else if(led3 == 1) {
                System.out.println("LED C ON");
                selectLED = "3";
                led3++;
            }
        }

        if(ledOnOffState == 0) { // 수동으로 On이 되었을 때
            ledOnOffStateInfo = "Off";
            if(led1 == 0 && led2 == 0 && led3==0) {
                selectLED = "전체";
            }
            else if(led1 == 0) {
                System.out.println("LED A OFF");
                selectLED = "1";
            }
            else if(led2 == 0) {
                System.out.println("LED B OFF");
                selectLED = "2";
            }
            else if(led3 == 0) {
                System.out.println("LED C OFF");
                selectLED = "3";
            }

        }

        // 조명이 on/off 될 때마다 파이어베이스로 데이터 전달
        if(ledOnOffState == 1 || ledOnOffState == 0) {
            cnt++; // 데이터베이스의 키값 1씩 증가하여 저장
            databaseReferenceLedInfo.child("조명정보 " + cnt).child("id").setValue(user); // 로그인 한 사용자
            databaseReferenceLedInfo.child("조명정보 " + cnt).child("LEDPower").setValue(ledOnOffStateInfo); // 조명 on/off
            databaseReferenceLedInfo.child("조명정보 " + cnt).child("selectLED").setValue(selectLED); // 어떤 조명 선택
            databaseReferenceLedInfo.child("조명정보 " + cnt).child("time").setValue(getTime); // 현재 시간

            if(ledOnOffState == 1) {
                timeText.setText(getTime + "에 조명 " + selectLED +" (이)가 켜졌습니다.");
            } else if(ledOnOffState == 0) {
                timeText.setText(getTime + "에 조명 " + selectLED +" (이)가 꺼졌습니다.");
            }
            ledOnOffState = 2;  // 한 번 찍히게 하기 위해서 2로 초기화
        }
    }
}