#include <DHT.h> // 온습도 센서 DHT 11를 위한 라이브러리 
#include <DHT_U.h> // 온습도 센서 DHT 11를 위한 라이브러리
#include <HuemonelabKit.h> // 미세먼지 센서를 포함한 라이브러리
#include <SoftwareSerial.h> // 시리얼, 블루투스 통신을 위한 라이브러리 
#include <Wire.h> // LCD 사용을 위한 라이브러리 
#include <LiquidCrystal_I2C.h> // LCD 사용을 위한 라이브러리 

#define DHTPIN 13     // 온습도 센서 디지털 핀 13번 연결 
#define DHTTYPE DHT11   // 온습도 센서 DHT 11 모델사용 
LiquidCrystal_I2C lcd(0x27,16,2); // LCD 주소 설정 
DHT dht(DHTPIN, DHTTYPE); // 온습도 센서 DHT의 핀과 타입을 13번, DHT11로 설정한 dht객체 생성     
SoftwareSerial bt(2,3); // 블루투스 통신을 위한 bt 설정 (RX: 수신 D2, TX: 송신 D3) 
DustSensor dust(A0,12); // 미세먼지 감지 센서를 아날로그 0번핀과 디지털 12번 핀으로 설정 
LiquidCrystal_I2C lcd1(0x27,16,2); //에어컨 LCD 설정 
//LiquidCrystal_I2C lcd2(0x27,16,2); //공기청정기 LCD 설정 

int ENA = 6; //에어컨 pwm 조절 
int IN1 = 4; //에어컨 
int IN2 = 5; //에어컨 

int ENB = 9; //공기청정기 pwm 조절 
int IN3 = 7; //공기청정기
int IN4 = 8; //공기청정기

//int bulb1 = 9; // 전구 1 (1채널) 
int bulb2 = 10; // 전구 2 (2채널) 
int bulb3 = 11; // 전구 3 (3채널)
char data;  // 블루투스로 받은 값을 저장할 변수

void setup() { // 초기 선언 부분 
  Serial.begin(9600); // 시리얼 통신
  bt.begin(9600); // 블루투스 통신 
  
  lcd1.init(); // LCD 초기화 
  //lcd2.init(); // LCD 초기화 
  dht.begin(); // DHT 통신 시작  

  pinMode(ENA,OUTPUT); // 에어컨 PWM 설정
  pinMode(IN1,OUTPUT); // 에어컨 IN1의 HIGH/LOW 설정
  pinMode(IN2,OUTPUT); // 에어컨 IN2의 HIGH/LOW 설정
  pinMode(ENB,OUTPUT); // 공기청정기 PWM 설정
  pinMode(IN3,OUTPUT); // 공기청정기 IN3의 HIGH/LOW 설정
  pinMode(IN4,OUTPUT); // 공기청정기 IN4의 HIGH/LOW 설정
   //pinMode(bulb1 ,OUTPUT); // 전구 1번 설정 
  pinMode(bulb2 ,OUTPUT); // 전구 2번 설정
  pinMode(bulb3 ,OUTPUT); // 전구 3번 설정 
}

void led() {
// 조명 조절
//    if (data=='a') { 
//      digitalWrite(bulb1 ,LOW); // LOW를 줘서 전구1 on
//      Serial.println(data); 
//    }
//    if (data=='b') {
//      digitalWrite(bulb1 ,HIGH); // HIGH 를 줘서 전구1 off
//      Serial.println(data); 
//    }
    if (data=='c') {
      digitalWrite(bulb2 ,LOW); // LOW를 줘서 전구2 on
      Serial.println(data); 
    }
    if (data=='d') {
      digitalWrite(bulb2 ,HIGH); // HIGH 를 줘서 전구2 off
      Serial.println(data); 
    }
    if (data=='e') {
      digitalWrite(bulb3 ,LOW); // LOW를 줘서 전구3 on
      Serial.println(data); 
    }
    if (data=='f') {
      digitalWrite(bulb3 ,HIGH); // HIGH 를 줘서 전구3 off
      Serial.println(data);  
    }

    // 전체 조명 제어 
    if (data=='g') {
    //digitalWrite(bulb1 ,LOW); // LOW를 줘서 전구1 on
      digitalWrite(bulb2 ,LOW); // LOW를 줘서 전구2 on
      digitalWrite(bulb3 ,LOW); // LOW를 줘서 전구3 on
      Serial.println(data); 
    }
    if (data=='h') {
      //digitalWrite(bulb1 ,HIGH); // HIGH를 줘서 전구1 off
      digitalWrite(bulb2 ,HIGH); // HIGH를 줘서 전구2 off
      digitalWrite(bulb3 ,HIGH); // HIGH를 줘서 전구3 off
      Serial.println(data);  
    }
   
}
void aircon() {
  // 에어컨 조절 
    // 팬세기 약 = 기본 세기 
    if(data=='q' || data=='z'){  // 블루투스로 q값(on) 또는 z값(팬세기 약)을 받으면
      // analogWrite(pin, value) : 지정한 pin의 값을 value(0~255)로 지정하여 조절
      
      // 90으로 바로 변환하기를 주었을 때 전력이 충분히 공급되지 않아 
      // 헛도는 현상을 방지하기 위해 최대 전력으로 1초동안 공급 
      analogWrite(ENA, 255); 
      delay(1000);
      
      analogWrite(ENA, 90); // 에어컨의 PWM을 90으로 변환 
      Serial.println(data);
    }
    if(data=='x'){  // 블루투스로 x값(팬세기 중)을 받으면
      analogWrite(ENA, 150); // 에어컨의 PWM을 150으로 변환 
      Serial.println(data);
    }
    if(data=='y'){   // 블루투스로 y값(팬세기 강)을 받으면
      analogWrite(ENA, 255); // 에어컨의 PWM을 255으로 변환 
      Serial.println(data);    
    }
    if(data=='p'){    // 블루투스로 p값(off)을 받으면
      analogWrite(ENA, 0); // 에어컨의 PWM을 0으로 변환  
      Serial.println(data); 
    }
    
}
void airCln() {
  // 공기청정기 조절 
    if(data=='n'){    // 블루투스로 q값(on)을 받으면
      analogWrite(ENB, 255); // 공기청정기의 PWM을 255으로 변환 
      Serial.println(data); 
    }
    if(data=='m'){    // 블루투스로 p값(off)을 받으면
      analogWrite(ENB,0); // 공기청정기의 PWM을 0으로 변환 
      Serial.println(data); 
    }
}
void loop() { // 반복 부분  
  float humiValue = dht.readHumidity(); // dht로부터 습도 측정 
  float tempValue = dht.readTemperature(); // dht로부터 섭씨 온도 측정
  int dustValue = dust.read(); // dust로부터 미세먼지 농도 측정 
  
  digitalWrite(IN1, HIGH); // HIGH : 입력전압 3v 이상
  digitalWrite(IN2, LOW); // Low : 입력 전압 1.5v 이하 
  digitalWrite(IN3, HIGH); // HIGH : 입력전압 3v 이상
  digitalWrite(IN4, LOW); // Low : 입력 전압 1.5v 이하 
  
  if(tempValue != NULL || humiValue != NULL || dustValue != NULL){ // 온도, 습도, 미세먼지의 값이 NULL이 아닐 경우 
    bt.print(tempValue); // float형 tempValue 값을 블루투스 통신을 이용해 출력함 
    bt.print(","); // 안드로이드에서 값을 받을 때 , 로 온습도와 미세먼지를 구분하므로 ,를 출력함 
    bt.print(humiValue); // float형 humiValue 값을 블루투스 통신을 이용해 출력함 
    bt.print(","); // 안드로이드에서 값을 받을 때 , 로 온습도를 구분하므로 ,를 출력함 
    bt.print(dustValue); // int형 dustValue 값을 블루투스 통신을 이용해 출력함 
    bt.println(); // 줄바꿈 

   // 시리얼 창에 보여줌 
    Serial.print("습도: ");
    Serial.print(humiValue);
    Serial.print(" 온도: ");
    Serial.print(tempValue);
    Serial.println();
    Serial.print("미세먼지 농도: ");
    Serial.print(dustValue);
    Serial.println();

    lcd1.backlight(); // 에어컨 LCD 백라이트   
    lcd1.display(); // 에어컨 LCD 표시내용 보기  
    lcd1.print("TEMP:     "); // LCD에 "TEMP:" 출력 
    lcd1.print(tempValue); // 이어서 tempValue 출력 
    lcd1.setCursor(0,1); // LCD 아래칸으로 이동 
    lcd1.print("HUMIDITY: "); // LCD에 "HUMIDITY:" 출력 
    lcd1.print(humiValue); // 이어서 humiValue 출력 

    //lcd2.backlight();
    //lcd2.display();
    //lcd2.print("DUST:     ");
    //lcd2.print(dustValue);
    delay(1000); // 1초 간격으로  
    lcd1.clear(); // LCD 의 모든 내용을 삭제한 후에 커서(0,0) 이동  
    //lcd2.clear();
    
  } 
  /*
  else if(tempValue == NAN || humiValue== NAN || dustValue == NAN) {
    Serial.print("NAN 값 입니다. 습도: ");
    Serial.print(humiValue);
    Serial.print("NAN 값 입니다.  온도: ");
    Serial.print(tempValue);
    Serial.println();
    Serial.print("NAN 값 입니다. 미세먼지 농도: ");
    Serial.print(dustValue);
    Serial.println();
  }
  */

  //블루투스랑 연결이 되었다면,
  if(bt.available()){ 
     data = bt.read();   //연결하여 받은 값을 data에 저장
     aircon();
     airCln();
     led();
  }
  Serial.println("테스트중");
  
}
