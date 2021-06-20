package com.example.bluetest3;

public class Led_listData {
//    private String profile; // 사용자 이미지
    private String id; // 사용자 아이디
    private String LEDPower; // 조명 On / Off
    private String selectLED; // 조명 1~3 선택
    private String time; // 시간

    /* DB에서 시간값을 가져오는 것이 아닌 안드로이드(로컬DB)에서 시간값을 찍어줄 경우
    * 발생한 문제점 : 각 버튼이 눌러졌을 때의 시간값이 모두 최초의 버튼이 눌러졌을 때의 시간값과 동일하게 나옴
    * => 다른 정보를 가져오는 것처럼 제어 화면에서 버튼을 클릭했을 때 그때의 시간값을 DB로 전달한 후 어댑터를 통해 가져오는 방식을 이용
    * */
    /*
    long time_ = System.currentTimeMillis();
    Date mDate = new Date(time_);
    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private String getTime = simpleDate.format(mDate);
     */

    public Led_listData(){}

//    public String getProfile() {
//        return profile;
//    }
//
//    public void setProfile(String profile) {
//        this.profile = profile;
//    }
//

    // getter 와 setter를 이용하여 간접적으로 정보를 조회할 수 있도록 함
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLEDPower() {
        return LEDPower;
    }

    public void setLEDPower(String LEDPower) {
        this.LEDPower = LEDPower;
    }

    public String getSelectLED() {
        return selectLED;
    }

    public void setSelectLED(String selectLED) {
        this.selectLED = selectLED;
    }

    public String getTime() { return time;}

    public void setTime(String time) { this.time = time; }
}
