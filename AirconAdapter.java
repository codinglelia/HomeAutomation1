package com.example.bluetest3;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AirconAdapter extends  RecyclerView.Adapter<AirconAdapter.AirconViewHolder> {
    private ArrayList<Aircon_listData> arrayList;
    private Context context;

    // 에어컨 어댑터 설정
    public AirconAdapter(ArrayList<Aircon_listData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AirconAdapter.AirconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 어댑터를 통해 에어컨의 데이터를 가져옴
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_aircon_listdata, parent, false);
        AirconAdapter.AirconViewHolder holder = new AirconAdapter.AirconViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AirconAdapter.AirconViewHolder holder, int position) {
        // profile 이미지 저장 (자동문 부분에서 구현)
        //        Glide.with(holder.itemView)
        //                .load(arrayList.get(position).getProfile())
        //                .into(holder.iv_profile);
        holder.tv_id.setText(arrayList.get(position).getId());
        holder.tv_option.setText(arrayList.get(position).getFanOption());
        holder.tv_fanSpeed.setText(arrayList.get(position).getFanSpeed());
        holder.tv_fanPower.setText(arrayList.get(position).getFanPower());
        holder.tv_temp.setText(String.valueOf(arrayList.get(position).gettemp()));
        holder.tv_time.setText(arrayList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        // arrayList가 널값이 아니라면 arrayList의 크기를 리턴하고 널값이라면 0을 리턴
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class AirconViewHolder extends RecyclerView.ViewHolder {
//        ImageView iv_profile;
        TextView tv_id; // 사용자 아이디
        TextView tv_option; // 에어컨 수동/자동
        TextView tv_fanSpeed; // 에어컨 속도
        TextView tv_fanPower; // 에어컨 on/off
        TextView tv_temp; // 실시간 온도
        TextView tv_time; // 현재 시간


        public AirconViewHolder(@NonNull View itemView) {
            super(itemView);
            // this.iv_profile = itemView.findViewById(R.id.iv_profile);
            // 조명의 ViewHolder와 레이아웃 요소를 각각 연결
            this.tv_id = itemView.findViewById(R.id.tv_id); // 사용자 아이디
            this.tv_option = itemView.findViewById(R.id.tv_option); // 에어컨 수동/자동
            this.tv_fanSpeed = itemView.findViewById(R.id.tv_fanSpeed); // 에어컨 팬속도
            this.tv_fanPower = itemView.findViewById(R.id.tv_fanPower); // 에어컨 on/off
            this.tv_temp = itemView.findViewById(R.id.tv_temp); // 실시간 온도
            this.tv_time = itemView.findViewById(R.id.tv_time); // 현재 시간
        }
    }
}