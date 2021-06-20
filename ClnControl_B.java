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

public class ClnControl_B extends AppCompatActivity   {
    public static final int REQUEST_CODE = 4; // 공기청정기에서 쓰이는 엔텐트 코드 설정

    private BluetoothSPP bt;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://airconinfo-a814a-default-rtdb.firebaseio.com/"); // 파이어베이스의 프로젝트와 연동
    private DatabaseReference databaseReferenceclnInfo = firebaseDatabase.getReference().child("공기청정기");
    int cnt; // 파이어베이스의 데이터의 순서를 지정

    // 수동, 자동 On/off 제어를 위한 변수
    int manualOn;
    int manualOff;

    boolean RealOnOff; // default : false
    // 내부적으로 알 수 있는 현재 공기청정기의 on off상태. on이면 true, off면 false.
    // 팬 속도조절 강중약 할 때 필요

    Button clnOn; // 공기청정기 on 버튼
    Button clnOff; // 공기청정기 off 버튼
    TextView timeText; // getTime 출력 부분
    Button getclnBtn; // 공기청정기 관리 버튼

    // DB에 저장할 변수
    String fanPowerState; // 팬 on/off  상태 저장
    String fanOptionState; // 팬 auto/manual 상태 저장
    String fanSpeedState; // 팬세기 약/중/각 저장
    double dust; // 버튼이 눌러졌을 때의 실시간 온도
    String getTime; // 버튼이 눌러졌을 때의 시간
    Button backBtn; // 뒤로가기 버튼
    String user; // 사용자 이메일
    TextView printUserID; // 사용자 ID 출력 부분
    Button btnConnect = findViewById(R.id.bluetoothCnt); //연결시도

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cln_control_b); // activity 와 layout 연결


        Toast.makeText(getApplicationContext(), "공기청정기 기능 제어 화면입니다.\n블루투스를 연결해주세요.",
                Toast.LENGTH_LONG).show();

        bt = new BluetoothSPP(this); //Initializing
        // 레이아웃 버튼과 연결
        TextView dustResult = findViewById(R.id.clndustResult);
        backBtn = findViewById(R.id.backBtn);
        timeText = findViewById(R.id.TimeResultCln);  //몇시에 켜졌는지 꺼졌는지 보여주는 텍스트
        clnOn = findViewById(R.id.clnOn); // 공기청정기 on 버튼
        clnOff = findViewById(R.id.clnOff); // 공기청정기 off 버튼
        getclnBtn = findViewById(R.id.getclnInfo); // 에어컨 관리 버튼 (에어컨 DB확인)


        // 사용자 아이디(이메일)를 가져오는 인텐트 부분
        Intent getLedIntent = getIntent();
        user = getLedIntent.getStringExtra("user");
        printUserID = findViewById(R.id.userID);
        printUserID.setText(user);


        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                String[] array = message.split(",");
                dustResult.setText(array[2].concat("m"));
                checkNaN(array[2]); // NaN값일 경우 어플리케이션 튕김 현상 제어
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() { // 뒤로가기 버튼 눌렀을 때
            @Override
            public void onClick(View v) {
                Intent intentNavi = new Intent(); // 인텐트 객체 생성 후
                intentNavi.putExtra("user", user); // name 의 값을 부가 데이터로 넣어준 후
                setResult(RESULT_OK, intentNavi); // Navigation 에게 응답 보냄
                finish(); // 액티비티 종료
            }
        });
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
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
            }
        }
    }

    // startActivityForResult() 후 바로 실행되며 Cln_listView 로부터 받은 응답을 처리하는 메서드
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
            }
        }
    }

    public void checkNaN(String data) {
        if(!data.equals("nan")){
//            아두이노에서 넘어온 데이터가 NaN값일 경우 string 으로 변환되어 "nan"이 출력되는데
//            equals() 메소드를 이용하여 data가 "nan"일경우 어플튕김 현상을 방지
            setup_2(data);
        }
    }
    public void setup_2(String data) {  // 미세먼지 값이 스트링형으로 넘어옴
        dust = Integer.parseInt(data);  // 넘어온 데이터를 인트형으로 변환  -

        //------------시간
        long time = System.currentTimeMillis();
        Date mDate = new Date(time);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        getTime = simpleDate.format(mDate);


        // 공기청정기 관리 버튼 클릭 시
        getclnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intentClnInfo = new Intent(getApplicationContext(), Cln_listView.class); // 공기청정기 관리 화면으로 이동
                intentClnInfo.putExtra("user",user); // 이동하면서 사용자 정보 전달
                startActivityForResult(intentClnInfo, REQUEST_CODE); // 인텐트 송출 후 onActivityResult() 실행
            }
        });


        // 수동으로 on 버튼을 눌렀을 때
        clnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fanOptionState = "수동";
                manualOn = 1; // 다음 수동 on을 위해 true (켜짐) 로 변경
            }
        });

        // 수동으로 off버튼을 눌렀을 때
        clnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fanOptionState = "수동";
                manualOff = 1; // 다음 수동 off를 위해 false (꺼짐) 로 변경
            }
        });

        if(manualOn == 1) { // 수동으로 On이 되었을 때
            bt.send("n", true);//블루투스를 통해 아두이노로 n 값을 보냄
            RealOnOff = true;
            fanPowerState = "On";
            timeText.setText(getTime + "에 공기청정기가 " + fanOptionState + "으로 켜졌습니다.");
        }
        if(manualOff == 1) { // 수동으로 Off 되었을 때
            bt.send("m", true); //블루투스를 통해 아두이노로 m 값을 보냄
            RealOnOff = false;
            fanPowerState = "Off";
            timeText.setText(getTime + "에 공기청정기가 " + fanOptionState + "으로 꺼졌습니다.");
        }


        // 하단에 있을 경우 조건문의 변수가 0이 되어 실행이 안되므로 위로 보내줌
        // -> 변수의 위치 문제 (전역변수로 선언해주기)
        if(manualOn == 1 || manualOff == 1 ){
            cnt++;
            databaseReferenceclnInfo.child("공기청정기 " + cnt).child("id").setValue(user); // 로그인 한 사용자
            databaseReferenceclnInfo.child("공기청정기 " + cnt).child("fanPower").setValue(fanPowerState); // 공기청정기 On/Off 상태
            databaseReferenceclnInfo.child("공기청정기 " + cnt).child("fanOption").setValue(fanOptionState); // 공기청정기 수동/자동 상태
            //databaseReferenceclnInfo.child("공깅청정기 " + cnt).child("fanSpeed").setValue(fanSpeedState); // 공기청정기 팬 세기
            databaseReferenceclnInfo.child("공기청정기 " + cnt).child("dust").setValue(dust); // 실시간 미세먼지 농도
            databaseReferenceclnInfo.child("공기청정기 " + cnt).child("time").setValue(getTime); // 실시간

            manualOn++; // DB에 데이터를 딱 한번만 찍히게 하기 위한 고의적 증가
            manualOff++; // // DB에 데이터를 딱 한번만 찍히게 하기 위한 고의적 증가

        }
    } // Setup() 끝
}