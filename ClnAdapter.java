package com.example.bluetest3;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClnAdapter extends  RecyclerView.Adapter<ClnAdapter.ClnViewHolder> {
    private ArrayList<Cln_listData> arrayList;
    private Context context;


    // 공기청정기 어댑터 설정
    public ClnAdapter(ArrayList<Cln_listData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClnAdapter.ClnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // 어댑터를 통해 공기청정기의 데이터를 가져옴
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cln_listdata, parent, false);
        ClnAdapter.ClnViewHolder holder = new ClnAdapter.ClnViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClnAdapter.ClnViewHolder holder, int position) {
        // profile 이미지 저장 (자동문 부분에서 구현)

        //        Glide.with(holder.itemView)
        //                .load(arrayList.get(position).getProfile())
        //                .into(holder.iv_profile);
        holder.tv_id.setText(arrayList.get(position).getId());
        holder.tv_option.setText(arrayList.get(position).getFanOption());
//        holder.tv_fanSpeed.setText(arrayList.get(position).getFanSpeed());
        holder.tv_fanPower.setText(arrayList.get(position).getFanPower());
        holder.tv_dust.setText(String.valueOf(arrayList.get(position).getDust()));
        holder.tv_time.setText(arrayList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        // arrayList가 널값이 아니라면 arrayList의 크기를 리턴하고 널값이라면 0을 리턴
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class ClnViewHolder extends RecyclerView.ViewHolder {
//        ImageView iv_profile;
        TextView tv_id; // 사용자 아이디
        TextView tv_option; // 공기청정기 수동/자동 on/off
//        TextView tv_fanSpeed;
        TextView tv_fanPower; // 공기청정기 on/off
        TextView tv_dust; // 미세먼지 농도
        TextView tv_time; // 현재 시간


        public ClnViewHolder(@NonNull View itemView) {
            super(itemView);
            // 공기청정기의 ViewHolder와 레이아웃 요소를 각각 연결
//            this.iv_profile = itemView.findViewById(R.id.iv_profile);
            this.tv_id = itemView.findViewById(R.id.tv_id); // 사용자 아이디
            this.tv_option = itemView.findViewById(R.id.tv_option); // 수동/자동 on/off
//            this.tv_fanSpeed = itemView.findViewById(R.id.tv_fanSpeed);
            this.tv_fanPower = itemView.findViewById(R.id.tv_fanPower); // 공기청정기 on/off
            this.tv_dust = itemView.findViewById(R.id.tv_dust); // 미세먼지 농도
            this.tv_time = itemView.findViewById(R.id.tv_time); // 현재 시간
        }
    }
}