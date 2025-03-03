package com.htc.spectraos.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.htc.spectraos.R;
import com.htc.spectraos.activity.AppFavoritesActivity;
import com.htc.spectraos.entry.ShortInfoBean;
import com.htc.spectraos.utils.AppUtils;
import com.htc.spectraos.utils.LogUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class ShortcutsAdapter extends RecyclerView.Adapter<ShortcutsAdapter.MyViewHolder> {

    Context mContext;
    private ArrayList<ShortInfoBean> short_list;
    ItemCallBack itemCallBack;

    public ShortcutsAdapter(Context mContext,ArrayList<ShortInfoBean> short_list) {
        this.mContext = mContext;
        this.short_list = short_list;
    }

    public void setItemCallBack(ItemCallBack itemCallBack) {
        this.itemCallBack = itemCallBack;
    }

    public ItemCallBack getItemCallBack() {
        return itemCallBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shortcuts_item, viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        if (i<short_list.size() && short_list.get(i).getAppicon()!=null){
            myViewHolder.icon.setBackground(short_list.get(i).getAppicon());
            myViewHolder.name.setText(short_list.get(i).getAppname());
        } else if (i<short_list.size()) {
            if (short_list.get(i).getPath()!=null){
                myViewHolder.icon.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(short_list.get(i).getPath())));
                myViewHolder.name.setText(short_list.get(i).getAppname());
            }else {
                myViewHolder.icon.setBackgroundResource(getAppIcon(short_list.get(i).getPackageName()));
                myViewHolder.name.setText(getAppName(short_list.get(i).getPackageName()));
            }
        }

        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCallBack!=null)
                    itemCallBack.onItemClick(i,myViewHolder.name.getText().toString());
            }
        });
        myViewHolder.rl_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AppUtils.startNewActivity(mContext, AppFavoritesActivity.class);
                return true;
            }
        });

        myViewHolder.rl_item.setOnHoverListener(new View.OnHoverListener() {
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

    }


    private String getAppName(String pkg){
        switch (pkg){
            case "com.netflix.ninja":
            case "com.netflix.mediaclient":
                return "Netflix";
            case "com.disney.disneyplus":
                return "Disney+";
            case "com.google.android.youtube.tv":
            case "com.google.android.youtube":
                return "Youtube";
            case "com.chrome.beta":
                return "Chrome";
            case "com.amazon.avod.thirdpartyclient":
                return "prime video";
            case "net.cj.cjhv.gs.tving":
                return "TVing";
            case "com.wbd.stream":
                return "HBO Max";
            case "com.frograms.wplay":
                return "WATCHA";
            case "in.startv.hotstar.dplus":
                return "Hotstar";
            case "com.jio.media.ondemand":
                return "JioCinema";
            case "jp.happyon.android":
                return "Hulu";
            case "tv.abema":
                return "ABEMA";
            case "com.mm.droid.livetv.fili":
                return "Fili TV";
            default:
                return "APK";
        }
    }

    private int getAppIcon(String pkg){
        switch (pkg){
            case "com.netflix.ninja":
            case "com.netflix.mediaclient":
                return R.drawable.netflix;
            case "com.disney.disneyplus":
                return R.drawable.disney2;
            case "com.google.android.youtube.tv":
                return R.drawable.youtube2;
            case "com.chrome.beta":
                return R.drawable.chrome;
            case "com.amazon.avod.thirdpartyclient":
                return R.drawable.primevideo;
            case "net.cj.cjhv.gs.tving":
                return R.drawable.tving;
            case "com.wbd.stream":
                return R.drawable.max;
            case "com.frograms.wplay":
                return R.drawable.watcha;
            case "in.startv.hotstar.dplus":
                return R.drawable.hotstar;
            case "com.jio.media.ondemand":
                return R.drawable.jio_cinema;
            case "jp.happyon.android":
                return R.drawable.hulu;
            case "tv.abema":
                return R.drawable.abema;
            case "com.mm.droid.livetv.fili":
                return R.mipmap.fill_tv;
            default:
                return R.mipmap.ic_launcher_round;
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface ItemCallBack{
        void onItemClick(int i ,String name);
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        RelativeLayout rl_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            rl_item = itemView.findViewById(R.id.rl_item);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
