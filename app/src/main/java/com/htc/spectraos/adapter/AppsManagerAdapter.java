package com.htc.spectraos.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.spectraos.R;
import com.htc.spectraos.entry.AppInfoBean;
import com.htc.spectraos.utils.ScrollUtils;
import com.htc.spectraos.widget.AppDetailDialog;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class AppsManagerAdapter extends RecyclerView.Adapter<AppsManagerAdapter.MyViewHolder> {

    Context mContext;
    RecyclerView recyclerView;
    List<AppInfoBean> infoBeans;
    private PackageManager mPm;

    public AppsManagerAdapter(Context mContext,List<AppInfoBean> infoBeans, RecyclerView recyclerView) {
        this.mContext = mContext;
        this.infoBeans = infoBeans;
        this.recyclerView = recyclerView;
        this.mPm = mContext.getPackageManager();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_settings_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final AppInfoBean info = infoBeans.get(i);
        myViewHolder.icon.setImageDrawable(info.getAppicon());
        myViewHolder.name.setText(info.getAppname());
        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDetailDialog detailDialog = new AppDetailDialog(mContext,R.style.DialogTheme);
                detailDialog.setData(info.getApplicationInfo());
                detailDialog.setOnClickCallBack(new AppDetailDialog.OnAppDetailCallBack() {
                    @Override
                    public void onClear_cache(String packageName) {
                        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                        activityManager.clearApplicationUserData(packageName,null);
                    }

                    @Override
                    public void onUninstall(String packageName) {
                        try {
                            Intent intent=new Intent();
                            Uri uri = Uri.parse("package:"+packageName);
                            //获取删除包名的URI
                            intent.setAction(Intent.ACTION_DELETE);
                            //设置我们要执行的卸载动作
                            intent.setData(uri);
                            //设置获取到的
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                });
                detailDialog.show();
            }
        });
        myViewHolder.rl_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (recyclerView==null)
                    return;

                if(b){
                    int[] amount = ScrollUtils.getScrollAmount(recyclerView, view);//计算需要滑动的距离
                    recyclerView.smoothScrollBy(amount[0], amount[1]);
                }
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

    @Override
    public int getItemCount() {
        return infoBeans.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        RelativeLayout rl_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.app_name);
            rl_item = itemView.findViewById(R.id.rl_item);
            icon = itemView.findViewById(R.id.app_icon);
        }
    }
}