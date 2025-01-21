package com.htc.spectraos.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.htc.spectraos.R;
import com.htc.spectraos.entry.AppInfoBean;
import com.htc.spectraos.utils.AppUtils;
import com.htc.spectraos.utils.ScrollUtils;

import java.util.ArrayList;


/**
 * @author 作�?�：hxd
 * @version 创建时间 2020/9/8 下午3:50:51 类说�?
 */
public class AppFavoritesAdapter extends RecyclerView.Adapter<AppFavoritesAdapter.MyViewHolder>{
	
	private Context mContext;
	private ArrayList<AppInfoBean> mList = new ArrayList<AppInfoBean>();
	private LayoutInflater inflater;
	private RecyclerView recyclerView;
	private onItemClickCallBack onItemClickCallBack;

	public AppFavoritesAdapter(Context context, ArrayList<AppInfoBean> list){
		this.mContext=context;
		this.mList=list;
		this.inflater= LayoutInflater.from(context);
	}

	public void setOnItemClickCallBack(AppFavoritesAdapter.onItemClickCallBack onItemClickCallBack) {
		this.onItemClickCallBack = onItemClickCallBack;
	}

	public void setRecyclerView(RecyclerView recyclerView) {
		this.recyclerView = recyclerView;
	}

	@NonNull
	@Override
	public AppFavoritesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		return new AppFavoritesAdapter.MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_appfavorites_gridview_item, null));
	}

	@Override
	public void onBindViewHolder(@NonNull AppFavoritesAdapter.MyViewHolder myViewHolder, final int i) {
		final AppInfoBean info = mList.get(i);
		myViewHolder.icon.setImageDrawable(info.getAppicon());
		myViewHolder.name.setText(info.getAppname());
		myViewHolder.status.setVisibility(info.isCheck()?View.VISIBLE:View.GONE);


		myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onItemClickCallBack!=null)
					onItemClickCallBack.onItemClick(i);
			}
		});

		myViewHolder.rl_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (recyclerView==null)
					return;

				if(b){
					if (onItemClickCallBack!=null)
						onItemClickCallBack.onItemFocus(i);
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
		return mList.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class MyViewHolder extends RecyclerView.ViewHolder {
		TextView name;
		ImageView icon;
		ImageView status;
		RelativeLayout rl_item;

		public MyViewHolder(@NonNull View itemView) {
			super(itemView);
			name = itemView.findViewById(R.id.app_name);
			rl_item = itemView.findViewById(R.id.rl_item);
			icon = itemView.findViewById(R.id.app_icon);
			status = itemView.findViewById(R.id.status);
		}
	}


	public interface onItemClickCallBack{
		void onItemClick(int position);
		void onItemFocus(int position);
	}
}
