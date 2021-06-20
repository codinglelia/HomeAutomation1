package com.example.bluetest3;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Cln_listData {
//    private String profile; // 사용자 이미지
    private String id; // 사용자 아이디
    private String fanOption; // 자동 / 수동
    //private String fanSpeed; // 팬세기
    private String fanPower; // On / Off
    private int dust; // (버튼이 눌렸을 때의) 온도
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
    public Cln_listData(){}

//    public String getProfile() {
//        return profile;
//    }
//
//    public void setProfile(String profile) {
//        this.profile = profile;
//    }
//
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFanOption() {
        return fanOption;
    }

    public void setFanOption(String option) {
        this.fanOption = option;
    }

//    public String getFanSpeed() {
//        return fanSpeed;
//    }
//
//    public void setFanSpeed(String fanSpeed) {
//        this.fanSpeed = fanSpeed;
//    }

    public String getFanPower() {
        return fanPower;
    }

    public void setFanPower(String power) {
        this.fanPower = power;
    }

    public int getDust() {
        return dust;
    }

    public void setDust(int dust) {
        this.dust = dust;
    }

    public String getTime() { return time;}

    public void setTime(String time) { this.time = time; }
}
