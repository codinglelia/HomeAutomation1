package com.example.bluetest3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static android.view.View.*;
import static java.sql.Types.NULL;

public class AirconControl extends AppCompatActivity   {
    public static final int REQUEST_CODE = 3;

    private BluetoothSPP bt;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://airconinfo-a814a-default-rtdb.firebaseio.com/"); // 파이어베이스의 프로젝트와 연동
    private DatabaseReference databaseReferenceAirInfo = firebaseDatabase.getReference().child("에어컨");
    int cnt; // 파이어베이스의 데이터의 순서를 지정

    // 수동, 자동 On/off 제어를 위한 변수
    int manualOn;
    int manualOff;
    int autoOn;
    int autoOff;

    boolean RealOnOff; // default : false
    // 내부적으로 알 수 있는 현재 에어컨의 on off상태. on이면 true, off면 false.
    // 팬 속도조절 강중약 할 때 필요

    //자동 onoff 설정변수
    int ontemp = NULL;
    int offtemp = NULL;

    int fanSpeedCnt; // 팬 속도 
    
    TextView AutoOntemp; // 자동 설정 on 온도 출력
    TextView AutoOfftemp; // 자동 설정 off 온도 출력 
    EditText AutoOnResult; // 사용자가 설정한 자동 on
    EditText AutoOffResult; // 사용자가 설정한 자동 off
    TextView power; // 팬 세기 출력 

    Button airFanLow; // 약 버튼
    Button airFanMid; // 중 버튼
    Button airFanHigh; // 강 버튼
    Button AirOn; // 에어컨 수동 on 버튼
    Button AirOff; // 에어컨 수동 off 버튼
    TextView timeText; // getTime 출력 부분 
    Button AirAutoStart; // 자동 설정 버튼 
    Button AirAutoStop; // 자동 설정 해제 버튼 
    Button getAirBtn; // 에어컨 관리 버튼 
    Button backBtn; // 뒤로가기 버튼 

    // DB에 저장할 변수
    String fanPowerState; // 팬 on/off  상태 저장
    String fanOptionState; // 팬 auto/manual 상태 저장
    String fanSpeedState; // 팬세기 약/중/각 저장
    double temp; // 버튼이 눌러졌을 때의 실시간 온도
    String getTime; // 버튼이 눌러졌을 때의 시간
    String user; // 사용자 이메일
    TextView printUserID; // 사용자 ID 출력 부분

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aircon_control); // activity 와 layout 연결

        Toast.makeText(getApplicationContext(), "에어컨 기능 제어 화면입니다.\n블루투스를 연결해주세요.",
                Toast.LENGTH_LONG).show();

        bt = new BluetoothSPP(this); //Initializing

        // 레이아웃 버튼과 연결
        AutoOntemp = findViewById(R.id.AirAutoOntemp); // 자동 설정 on 온도 출력
        AutoOfftemp = findViewById(R.id.AirAutoOfftemp); // 자동 설정 off 온도 출력
        AutoOnResult = findViewById(R.id.AirAutoOnResult); // 자동 설정 on 온도
        AutoOffResult = findViewById(R.id.AirAutoOffResult); // 자동 설정 off 온도
        power = findViewById(R.id.AirPowerResult);     // 사용자에게 보여줄 속도
        backBtn = findViewById(R.id.backBtn); // 뒤로가기 버튼
        TextView tempResult = findViewById(R.id.AirtempResult); // 실시간 온도 출력
        Button btnConnect = findViewById(R.id.bluetoothCnt); //연결시도

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
                String[] array = message.split(","); // 아두이노에서 받아온 데이터를 , 로 나누어 배열로 저장
                tempResult.setText(array[0].concat("C")); // tempResult에 배열의 첫번째 요소인 온도와 'C'를 함께 출력함
                checkNaN(array[0]); // String 형의 온도가 nan값인 지 확인
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

        // 블루투스 연결
        btnConnect.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {  // 뒤로가기 버튼 눌렀을 때
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

    // startActivityForResult() 후 바로 실행되며 Aircon_listView 로부터 받은 응답을 처리하는 메서드
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

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
//                String user = data.getStringExtra("user");
                Toast.makeText(getApplicationContext(), "에어컨을 제어하기 위해 버튼을 선택하세요.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // 자동으로 설정한 온도를 취소한 후 text 초기화
    public void cancelAutotemp() {
        Toast.makeText(getApplicationContext(), "자동 설정이 취소되었습니다.", Toast.LENGTH_SHORT).show();
        AutoOnResult.setText("");
        AutoOffResult.setText("");
        AutoOntemp.setText("");
        AutoOfftemp.setText("");
        power.setText("");
        ontemp = NULL;
        offtemp = NULL;

    }


    public void checkNaN(String data) {
        if(!data.equals("nan")){
//            아두이노에서 넘어온 데이터가 NaN값일 경우 string 으로 변환되어 "nan"이 출력되는데
//            equals() 메소드를 이용하여 data가 "nan"일경우 어플튕김 현상을 방지
            setup_2(data);
        }
    }

    public void setup_2(String data) {  // 온도 값이 스트링형으로 넘어옴
        temp = Double.parseDouble(data); // 넘어온 데이터를 더블형으로 변환

        //------------시간
        long time = System.currentTimeMillis();
        Date mDate = new Date(time);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        getTime = simpleDate.format(mDate);

        // 자동 설정 부분 (지역변수로 선언)
        AirAutoStart = findViewById(R.id.AirAutoYes); //  자동 설정 버튼
        AirAutoStop = findViewById(R.id.AirAutoNo); // 자동 설정 안함 버튼
        timeText = findViewById(R.id.TimeResultCln);  //몇시에 켜졌는지 꺼졌는지 보여주는 텍스트
        AirOn = findViewById(R.id.AirOn); // 에어컨 on 버튼
        AirOff = findViewById(R.id.AirOff); // 에어컨 off 버튼
        airFanLow = findViewById(R.id.AirLow);    //약
        airFanMid = findViewById(R.id.AirMid);    //중
        airFanHigh = findViewById(R.id.AirHigh);  //강

        getAirBtn = findViewById(R.id.getAirInfo); // 에어컨 관리 버튼 (에어컨 DB확인)
        getAirBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intentAirInfo = new Intent(getApplicationContext(), Aircon_listView.class); // 에어컨 관리 화면으로 이동
                intentAirInfo.putExtra("user",user); // 이동하면서 사용자 정보 전달
                startActivityForResult(intentAirInfo, REQUEST_CODE); // 인텐트 송출 후 onActivityResult() 실행
            }
        });


        // 수동으로 on 버튼을 눌렀을 때
        AirOn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수동으로 켜져있는 상태에서 사용자가 On 버튼을 눌렀을 때
                // 다시 on 인 값을 DB에 저장할 필요가 있고 켜져있는 에어컨을 다시 수동으로 on 시킬필요가 있다.
                if(ontemp != NULL && offtemp != NULL) { // 자동 on/off 설정 시 설정 해제 후 On 실행
                    cancelAutotemp(); // 자동 설정 값 초기화
                }
                fanOptionState = "수동";
                autoOff = 0; // 다음 자동 off를 위해 false (꺼짐) 로 변경
                autoOn = 0; // 다음 자동 on를 위해 false (꺼짐) 로 변경
                manualOn = 1; // 다음 수동 on을 위해 true (켜짐) 로 변경
            }
        });

        // 수동으로 off버튼을 눌렀을 때
        AirOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 자동으로 꺼져있는 상태에서 사용자가 Off 버튼을 눌렀을 때
                // 다시 off 인 값을 DB에 저장할 필요가 없고 꺼져있는 에어컨을 다시 수동으로 off 시킬필요가 없다.
                if(autoOff == 0) { // 자동으로 설정이 안되어있을 때
                    // off 버튼을 누르면 에어컨 off
                    bt.send("p", true);      //블루투스를 통해 아두이노로 p 값을 보냄
                    fanOptionState = "수동";
                }
                cancelAutotemp(); // power.setText("")
                autoOff = 0; // 다음 자동 off를 위해 false (꺼짐) 로 변경
                autoOn = 0; // 다음 자동 on를 위해 false (꺼짐) 로 변경
                manualOff = 1; // 다음 수동 off를 위해 false (꺼짐) 로 변경
            }
        });

        if(manualOn == 1) { // 수동으로 On이 되었을 때
            bt.send("q", true); //블루투스를 통해 아두이노로 q 값을 보냄
            RealOnOff = true;
        }
        if(manualOff == 1) { // 수동으로 On이 되었을 때
            bt.send("p", true); //블루투스를 통해 아두이노로 p 값을 보냄
            RealOnOff = false;
        }

        // 실시간 온도를 통한 에어컨 자동 on/off 설정 부분
        if (ontemp != NULL && offtemp != NULL) { // ontemp 와 offtemp 값이 NULL 이 아닐 경우
            // 자동으로 On 설정
            if (temp >= ontemp) {    //현재 온도가 on설정값보다 높거나 같으면 실행
                autoOn++; // 수동 On 을 한번 실행시키기 위한 고의적 증가
                fanOptionState = "자동";
                if (autoOn == 1) { // 그 중 autoOn이 1일 때만
                    bt.send("q", true); //블루투스를 통해 아두이노로 q 값을 보냄
                    autoOff = 0; // 다음 자동 Off 를 위해 false (꺼짐) 로 변경
                    RealOnOff = true; // 에어컨 속도 제어를 위한 On 상태로 변경, 속도 제어 가능
                }
            }
            // 자동으로 Off 설정
            if (temp <= offtemp) {   // 현재 온도가 off 설정값보다 낮거나 같으면 실행
                autoOff++;  // 수동 Off를 한번 실행시키기 위한 고의적 증가
                fanOptionState = "자동";
                if (autoOff == 1) { // 그 중 autoOff가 1일 때만
                    power.setText(""); // 사용자에게 보여줄 속도 초기화
                    bt.send("p", true);  //블루투스를 통해 아두이노로 p 값을 보냄
                    autoOn = 0; // 다음 자동 On 을 위해 true (켜짐) 로 변경
                    RealOnOff = false; // 에어컨 속도 제어를 위한 Off 상태로 변경, 속도 제어 불가능
                }
            }
        }
/*
실시간 온도보다 설정한 on온도값이 높을 때 , 실시간 온도보다 설정한 off온도값이 낮을 때
항상 getTime을 받아오는 문제점 발생

해결방법
1. for문과 count 변수를 이용해 count가 1일 경우에만 찍음
2. on,off가 실행됐을 때의 가장 처음 시간 값을 변수에 저장한 후 setText로 그 변수값만 보여줌(계속 찍히지만 동일한 값이므로 상관x)

 */
        //온도 설정 후 자동 설정 버튼을 눌렀을 때
        AirAutoStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AutoOnResult.length()>0 && AutoOffResult.length()>0){    //사용자가 값을 입력했다면 (에디트에 값이 있다면)
                    String onT = AutoOnResult.getText().toString();     // on설정값을 ont로 저장
                    String offT = AutoOffResult.getText().toString();   // off설정값을 offt로 저장
                    ontemp = Integer.parseInt(onT); // string 을 int 형으로 변환
                    offtemp = Integer.parseInt(offT);   //ontemp,offtemp에 값을 넣어 인트형으로 바꿔줌

                    if(ontemp>offtemp) {     // on설정값과 off설정값 비교. on보다 off 수가 작으면 자동설정가능
                        AutoOntemp.setText(onT);
                        AutoOfftemp.setText(offT);    //설정 값 출력
                        AutoOnResult.setText("");
                        AutoOffResult.setText("");      //입력하는 곳 초기화
                        Toast.makeText(getApplicationContext(), "설정되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else if (ontemp<=offtemp){   //사용자가 값을 잘못입력했다면 (on값이 off보다 낮거나 같다면)
                        AutoOnResult.setText("");
                        AutoOffResult.setText("");
                        Toast.makeText(getApplicationContext(), "Off 보다 On의 온도가 낮습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{ //사용자가 값을 입력하지 않았다면 (on과 off 설정값이 없다면, 둘 중 하나의 값이 없어도 실행)
                    AutoOnResult.setText("");
                    AutoOffResult.setText("");
                    Toast.makeText(getApplicationContext(), "설정할 값을 입력해주세요.", Toast.LENGTH_SHORT).show();   //다시입력해주세요
                }
            }
        });


        //설정 안함을 누를 때
        AirAutoStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAutotemp();
            }
        });

        airFanLow.setOnClickListener(new OnClickListener() {    //약버튼
            @Override
            public void onClick(View v) {
                if (RealOnOff == true) {    //에어컨이 켜져있을 경우
                    fanSpeedState = "약";
                    fanSpeedCnt = 1;
                    bt.send("z", true);      // 블루투스를 통해 아두이노로 z 값을 보냄
                    power.setText(fanSpeedState);     // 팬 속도 출력
                } else {        //에어컨이 꺼져있으면(값이 펄스면) 토스트알림
                    Toast.makeText(getApplicationContext(), "에어컨을 켜주세요.", Toast.LENGTH_SHORT).show();
                    power.setText("");      //사용자에게 보여줄 텍스트 초기화
                }
            }
        });


        airFanMid.setOnClickListener(new OnClickListener(){     //중버튼
            @Override
            public void onClick(View v) {
                if (RealOnOff == true) { // 에어컨이 켜져있을 경우
                    fanSpeedState = "중";
                    fanSpeedCnt = 1;
                    bt.send("x",true);      //블루투스를 통해 아두이노로 x 값을 보냄
                    power.setText(fanSpeedState);       // 팬 속도 출력
                } else {
                    Toast.makeText(getApplicationContext(), "에어컨을 켜주세요.", Toast.LENGTH_SHORT).show();
                    power.setText("");      //사용자에게 보여줄 텍스트 초기화
                }
            }
        });

        airFanHigh.setOnClickListener(new OnClickListener(){     //강버튼
            @Override
            public void onClick(View v) {
                if (RealOnOff == true) {   // 에어컨이 켜져있을 경우
                    fanSpeedState = "강";
                    fanSpeedCnt = 1;
                    bt.send("y", true);      //블루투스를 통해 아두이노로 y 값을 보냄
                    power.setText(fanSpeedState);       // 팬 속도 출력
                } else {
                    Toast.makeText(getApplicationContext(), "에어컨을 켜주세요.", Toast.LENGTH_SHORT).show();
                    power.setText("");      //사용자에게 보여줄 텍스트 초기화
                }
            }
        });

        if (autoOn == 1 || manualOn == 1) { // 자동으로 On이 되거나 수동으로 on이 되었을 때
            fanPowerState = "On"; // 팬 파워 On 설정
            fanSpeedState = "약"; // On의 기본 세기 약 설정
            timeText.setText(getTime + "에 에어컨이 " + fanOptionState + "으로 켜졌습니다.");
        }

        if (autoOff == 1 || manualOff == 1) { // 수동으로 Off가 되거나 수동으로 off가 되었을 때
            // 자동 off 에서 수동 off 로 이동할때만 메시지 생략하기
            // autoOff 가 1인 상태에서 manualOff 1로 변경될때
            // temp <= offtemp 인 상태에서 offtemp != null 인 상태로 off버튼 누를때
            //                            cancelAutotemp 실행될 때
            fanPowerState = "Off";
            timeText.setText(getTime + "에 에어컨이 " + fanOptionState + "으로 꺼졌습니다.");
        }

        // 하단에 있을 경우 조건문의 변수가 0이 되어 실행이 안되므로 위로 보내줌
        // -> 변수의 위치 문제 (전역변수로 선언해주기)
        if(autoOn == 1 || autoOff == 1 || manualOn == 1 || manualOff == 1 || fanSpeedCnt == 1){
            cnt++;
            databaseReferenceAirInfo.child("에어컨 " + cnt).child("id").setValue(user); // 로그인 한 사용자
            databaseReferenceAirInfo.child("에어컨 " + cnt).child("fanPower").setValue(fanPowerState); // 에어컨 On/Off 상태
            databaseReferenceAirInfo.child("에어컨 " + cnt).child("fanOption").setValue(fanOptionState); // 에어컨 수동/자동 상태
            databaseReferenceAirInfo.child("에어컨 " + cnt).child("fanSpeed").setValue(fanSpeedState); // 에어컨 팬 세기
            databaseReferenceAirInfo.child("에어컨 " + cnt).child("temp").setValue(temp); // 실시간 온도
            databaseReferenceAirInfo.child("에어컨 " + cnt).child("time").setValue(getTime); // 실시간
            fanSpeedCnt = 0;
            manualOn++; // DB에 데이터를 딱 한번만 찍히게 하기 위한 고의적 증가
            manualOff++; // // DB에 데이터를 딱 한번만 찍히게 하기 위한 고의적 증가

        }
    } // Setup() 끝
}