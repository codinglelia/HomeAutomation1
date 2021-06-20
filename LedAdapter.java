package com.example.bluetest3;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LedAdapter extends  RecyclerView.Adapter<LedAdapter.LedViewHolder> {
    private ArrayList<Led_listData> arrayList;
    private Context context;

    // 조명 어댑터 설정
    public LedAdapter(ArrayList<Led_listData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public LedAdapter.LedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 어댑터를 통해 조명의 데이터를 가져옴
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_led_listdata, parent, false);
        LedAdapter.LedViewHolder holder = new LedAdapter.LedViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LedAdapter.LedViewHolder holder, int position) {
        // profile 이미지 저장 (자동문 부분에서 구현)
        //        Glide.with(holder.itemView)
        //                .load(arrayList.get(position).getProfile())
        //                .into(holder.iv_profile);
        holder.tv_id.setText(arrayList.get(position).getId());
        holder.tv_LEDPower.setText(arrayList.get(position).getLEDPower());
        holder.tv_selectLED.setText(arrayList.get(position).getSelectLED());
        holder.tv_time.setText(arrayList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        // arrayList가 널값이 아니라면 arrayList의 크기를 리턴하고 널값이라면 0을 리턴
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class LedViewHolder extends RecyclerView.ViewHolder {
        // ImageView iv_profile;
        TextView tv_id; // 사용자 아이디
        TextView tv_LEDPower; // 조명 on/off
        TextView tv_selectLED; // 조명 1~3
        TextView tv_time; // 현재 시간

        public LedViewHolder(@NonNull View itemView) {
            super(itemView);
            // this.iv_profile = itemView.findViewById(R.id.iv_profile);
            // 조명의 ViewHolder와 레이아웃 요소를 각각 연결
            this.tv_id = itemView.findViewById(R.id.tv_id);  // 사용자 아이디
            this.tv_LEDPower = itemView.findViewById(R.id.tv_LEDPower); // 조명 on/off
            this.tv_selectLED = itemView.findViewById(R.id.tv_selectLED); // 조명 1~3
            this.tv_time = itemView.findViewById(R.id.tv_time); // 현재 시간
        }
    }
}