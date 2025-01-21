package com.htc.spectraos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.htc.spectraos.R;
import com.htc.spectraos.activity.MainActivity;

import java.util.Arrays;
import java.util.List;

public class SignalAdapter extends RecyclerView.Adapter<SignalAdapter.MyViewHolder> {


    List<String> nameList;
    List<String> idList;

    Context context;

    MainActivity.SignalItemCallBack signalItemCallBack;

    public SignalAdapter(Context context,String[] nameList, String[] idList) {
        this.context = context;
        this.nameList = Arrays.asList(nameList);
        this.idList = Arrays.asList(idList);
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setSignalItemCallBack(MainActivity.SignalItemCallBack signalItemCallBack) {
        this.signalItemCallBack = signalItemCallBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SignalAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.signal_item, parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
        if (idList.get(position).contains("HDMI")) {
            holder.icon.setBackgroundResource(R.drawable.home_hdmi_bg);
        }else if (idList.get(position).contains("CVBS")) {
            holder.icon.setBackgroundResource(R.drawable.home_av_bg);
        } else if (idList.get(position).contains("USB")) {
            holder.icon.setBackgroundResource(R.drawable.home_usb);
        } else if (idList.get(position).contains("SCREEN")) {
            holder.icon.setBackgroundResource(R.drawable.home_screen_bg);
        }else if (idList.get(position).contains("MANUAL")) {
            holder.icon.setBackgroundResource(R.drawable.manual_bg);
        }

        if (position % 4 ==0){
            holder.right.setVisibility(View.VISIBLE);
            holder.bottom.setVisibility(View.VISIBLE);

            holder.top.setVisibility(View.GONE);
            holder.left.setVisibility(View.GONE);
        }else if (position % 4 ==1){
            holder.right.setVisibility(View.GONE);
            holder.bottom.setVisibility(View.VISIBLE);

            holder.top.setVisibility(View.GONE);
            holder.left.setVisibility(View.VISIBLE);
        }else if (position % 4 ==2){
            holder.right.setVisibility(View.VISIBLE);
            holder.bottom.setVisibility(View.GONE);

            holder.top.setVisibility(View.VISIBLE);
            holder.left.setVisibility(View.GONE);
        }else if (position % 4 ==3){
            holder.right.setVisibility(View.GONE);
            holder.bottom.setVisibility(View.GONE);

            holder.top.setVisibility(View.VISIBLE);
            holder.left.setVisibility(View.VISIBLE);
        }

        if (position==idList.size()-1 && position % 2 ==0){
            holder.right.setVisibility(View.GONE);
            holder.bottom.setVisibility(View.GONE);

            holder.top.setVisibility(View.VISIBLE);
            holder.left.setVisibility(View.GONE);

            ViewGroup.LayoutParams params = holder.rl_item.getLayoutParams();
            params.width = (int) (context.getResources().getDisplayMetrics().widthPixels*0.265); // 自定义方法，根据数据动态计算宽度
            holder.itemView.setLayoutParams(params);
        }

        if (position==0 && idList.size()==1){
            holder.right.setVisibility(View.GONE);
            holder.bottom.setVisibility(View.GONE);

            holder.top.setVisibility(View.GONE);
            holder.left.setVisibility(View.GONE);
        }

        if (idList.size()==2){
                if (position==0){
                    holder.right.setVisibility(View.GONE);
                    holder.bottom.setVisibility(View.VISIBLE);

                    holder.top.setVisibility(View.GONE);
                    holder.left.setVisibility(View.GONE);

                    ViewGroup.LayoutParams params = holder.rl_item.getLayoutParams();
                    params.width = (int) (context.getResources().getDisplayMetrics().widthPixels*0.265); // 自定义方法，根据数据动态计算宽度
                    holder.itemView.setLayoutParams(params);
                }else {
                    holder.right.setVisibility(View.GONE);
                    holder.bottom.setVisibility(View.GONE);

                    holder.top.setVisibility(View.VISIBLE);
                    holder.left.setVisibility(View.GONE);

                    ViewGroup.LayoutParams params = holder.rl_item.getLayoutParams();
                    params.width = (int) (context.getResources().getDisplayMetrics().widthPixels*0.265); // 自定义方法，根据数据动态计算宽度
                    holder.itemView.setLayoutParams(params);
                }
        }

        holder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signalItemCallBack!=null)
                    signalItemCallBack.onItemClick(idList.get(position));
            }
        });
        holder.rl_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (signalItemCallBack!=null)
                    signalItemCallBack.onItemFocus(v,hasFocus);
            }
        });

        holder.rl_item.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                int what = event.getAction();
                switch (what) {
                    case MotionEvent.ACTION_HOVER_ENTER: // 鼠标进入view
                        v.requestFocus();
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE: // 鼠标在view上
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT: // 鼠标离开view
                        break;
                }
                return false;
            }
        });

        setBackground(holder,position);
    }

    private void setBackground(MyViewHolder myViewHolder,int position){
        switch (idList.size()){
            case 1:
                //默认四个圆角背景
                break;
            case 2:
                switch (position){
                    case 0:
                        myViewHolder.rl_item.setBackgroundResource(R.drawable.home_sign_tl_tr_bg);
                        break;
                    case 1:
                        myViewHolder.rl_item.setBackgroundResource(R.drawable.home_sign_bl_br_bg);
                        break;
                }
                break;
            case 3:
                switch (position){
                    case 0:
                        myViewHolder.rl_item.setBackgroundResource(R.drawable.home_sign_tl_bg);
                        break;
                    case 1:
                        myViewHolder.rl_item.setBackgroundResource(R.drawable.home_sign_tr_bg);
                        break;
                    case 2:
                        myViewHolder.rl_item.setBackgroundResource(R.drawable.home_sign_bl_br_bg);
                        break;

                }
                break;
            case 4:
                switch (position){
                    case 0:
                        myViewHolder.rl_item.setBackgroundResource(R.drawable.home_sign_tl_bg);
                        break;
                    case 1:
                        myViewHolder.rl_item.setBackgroundResource(R.drawable.home_sign_tr_bg);
                        break;
                    case 2:
                        myViewHolder.rl_item.setBackgroundResource(R.drawable.home_sign_bl_bg);
                        break;
                    case 3:
                        myViewHolder.rl_item.setBackgroundResource(R.drawable.home_sign_br_bg);
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return idList.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        View left;
        View right;
        View top;
        View bottom;
        RelativeLayout rl_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            left = itemView.findViewById(R.id.left);
            right = itemView.findViewById(R.id.right);
            top = itemView.findViewById(R.id.top);
            bottom = itemView.findViewById(R.id.bottom);
            name = itemView.findViewById(R.id.name);
            icon = itemView.findViewById(R.id.icon);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }

}
