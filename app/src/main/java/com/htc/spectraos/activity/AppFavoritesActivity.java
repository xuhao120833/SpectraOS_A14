package com.htc.spectraos.activity;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.TextView;

import com.htc.spectraos.MyApplication;
import com.htc.spectraos.R;
import com.htc.spectraos.adapter.AppFavoritesAdapter;
import com.htc.spectraos.entry.AppInfoBean;
import com.htc.spectraos.entry.AppSimpleBean;
import com.htc.spectraos.entry.SpecialApps;
import com.htc.spectraos.receiver.AppCallBack;
import com.htc.spectraos.receiver.AppReceiver;
import com.htc.spectraos.utils.AppUtils;
import com.htc.spectraos.utils.DBUtils;
import com.htc.spectraos.utils.LogUtils;
import com.htc.spectraos.utils.ShareUtil;
import com.htc.spectraos.utils.ToastUtil;
import com.htc.spectraos.widget.GridViewItemOrderUtil;
import com.htc.spectraos.widget.SpacesItemDecoration;

import java.util.ArrayList;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @author 作�?�：hxd
 * @version 创建时间 2020/9/8 下午3:50:51 类说�?
 */
public class AppFavoritesActivity extends BaseActivity implements AppCallBack {

	private String currentPackageName = null;

	private String tag = "AppFavoritesActivity";

	private int selected;

	private ArrayList<AppInfoBean> list = new ArrayList<>();

	private SharedPreferences sp;
	private Editor ed;

	private GridView appfavorites_gridview;
	private RecyclerView appfavorites_rv;
	private AppFavoritesAdapter adapter;
	private TextView select_number_tv;

	private IntentFilter appFilter = new IntentFilter();
	private AppReceiver appReceiver = null;
	private final int Handler_update = 10000;
	String resident = "";
	int residentSize = 0;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_appfovorites_activity);
		sp = ShareUtil.getInstans(this);
		ed = sp.edit();
		resident =sp.getString("resident","");
		residentSize =sp.getInt("residentSize",0);
		initView();
		initData();
	}

	public void initView() {

		appfavorites_gridview =  findViewById(R.id.appfavorites_gridview);
		appfavorites_rv =  findViewById(R.id.appfavorites_rv);
		select_number_tv =  findViewById(R.id.select_number_tv);

		GridLayoutManager gridLayoutManager = new GridLayoutManager(this,6);
		appfavorites_rv.setLayoutManager(gridLayoutManager);
		appfavorites_rv.addItemDecoration(new SpacesItemDecoration(SpacesItemDecoration.px2dp(10),SpacesItemDecoration.px2dp(10),SpacesItemDecoration.px2dp(10),SpacesItemDecoration.px2dp(10)));
		appfavorites_rv.setItemAnimator(null);
	}


	public void initData() {
		initReceiver();
		loadDataApp();
		
	}


	public void onclick(View view) {

	}

	@Override
	protected void onResume() {
		super.onResume();


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		destoryReceiver();
	}

	/**
	 * 注销广播
	 */
	private void destoryReceiver() {


		if (appReceiver != null) {
			unregisterReceiver(appReceiver);
		}


	}

	private void initReceiver() {

		// app
		appFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		appFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		appFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		appFilter.addDataScheme("package");
		appReceiver = new AppReceiver(this);
		registerReceiver(appReceiver, appFilter);

	}


	private void loadAllApp() {
		adapter = new AppFavoritesAdapter(AppFavoritesActivity.this, list);
		adapter.setRecyclerView(appfavorites_rv);
		adapter.setHasStableIds(true);
		adapter.setOnItemClickCallBack(new AppFavoritesAdapter.onItemClickCallBack() {
			@Override
			public void onItemClick(int position) {
				if (resident.contains(list.get(position).getApppackagename()) || isSpecialApps(list.get(position).getApppackagename())) {
					ToastUtil.showShortToast(AppFavoritesActivity.this,
							getString(R.string.resident_app));
					return;
				}



				list.get(position).setCheck(!list.get(position).isCheck());

				select_number_tv.setText((position + 1) + "/" + list.size());

				int count = residentSize;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).isCheck() && !resident.contains(list.get(i).getApppackagename()) ) {
						count += 1;
					}
				}
				if (count > 6) {
					list.get(position)
							.setCheck(!list.get(position).isCheck());
					ToastUtil.showShortToast(AppFavoritesActivity.this,
							getString(R.string.short_max_tips));
				} else {
					boolean isCheck = list.get(position).isCheck();
					if (isCheck) {
						if (!DBUtils.getInstance(AppFavoritesActivity.this)
								.isExistData(
										list.get(position).getApppackagename())) {
							DBUtils.getInstance(AppFavoritesActivity.this)
									.addFavorites(
											list.get(position)
													.getApppackagename());
							adapter.notifyDataSetChanged();
						}
					} else {
						DBUtils.getInstance(AppFavoritesActivity.this)
								.deleteFavorites(
										list.get(position).getApppackagename());
						adapter.notifyDataSetChanged();
					}

				}
			}

			@Override
			public void onItemFocus(int position) {
				selected = position;
				select_number_tv.setText((selected + 1) + "/"
						+ list.size());
			}
		});
		if (list.size() > 0) {
			select_number_tv.setText((selected + 1) + "/" + list.size());
		} else {
			select_number_tv.setText("0/0");
		}

		appfavorites_rv.setAdapter(adapter);
	}


	private boolean isSpecialApps(String pck){
		if (MyApplication.config.specialApps==null || MyApplication.config.specialApps.size()==0)
			return false;

		for (SpecialApps specialApps : MyApplication.config.specialApps){
			if (specialApps.getPackageName().equals(pck))
				return true;
		}
		return false;
	}

	private void loadDataApp() {
		// AppTask task = new AppTask();
		// task.execute();
		ArrayList<AppInfoBean> mList = AppUtils
				.getApplicationMsg(AppFavoritesActivity.this);
		ArrayList<AppSimpleBean> simpleList = DBUtils.getInstance(
				AppFavoritesActivity.this).getFavorites();

		String country_code = Settings.System.getString(getContentResolver(),"ip_country_code");
		if (country_code!=null){
			String[] continent_countryCode = country_code.split(",");
			if (continent_countryCode.length>=2 && MyApplication.config.specialApps !=null && MyApplication.config.specialApps.size()>0) {

				for (SpecialApps specialApps : MyApplication.config.specialApps){

					if (specialApps.getContinent()!=null && !specialApps.getContinent().equals("")){
						if (specialApps.getContinent().contains("!")){
							if (specialApps.getContinent().replace("!","").equals(continent_countryCode[0]))
								continue;
						}else {
							if (!specialApps.getContinent().equals(continent_countryCode[0]))
								continue;
						}
					}

					if (specialApps.getCountryCode()!=null && !specialApps.getCountryCode().equals("")){
						if (specialApps.getCountryCode().contains("!")){
							if (specialApps.getCountryCode().replace("!","").equals(continent_countryCode[1]))
								continue;
						}else {
							if (!specialApps.getCountryCode().equals(continent_countryCode[1]))
								continue;
						}
					}

					AppSimpleBean simpleBean = new AppSimpleBean();
					simpleBean.setPackagename(specialApps.getPackageName());
					simpleBean.setPath(specialApps.getIconPath());
					simpleBean.setAppName(specialApps.getAppName());
					simpleList.add(0,simpleBean);
					residentSize+=1;
				}

                /*if (continent_countryCode[1].equals("BR")) {
                    //巴西IP
                    AppSimpleBean simpleBean = new AppSimpleBean();
                    simpleBean.setPackagename("GTV");
                    appSimpleBeans.add(0,simpleBean);
                } else if (continent_countryCode[1].equals("PH")) {
                    //菲律宾IP
                    AppSimpleBean simpleBean = new AppSimpleBean();
                    simpleBean.setPackagename("com.mm.droid.livetv.fili");
                    appSimpleBeans.add(0,simpleBean);

                } else if (continent_countryCode[0].equals("南美洲")) {
                    //南美非巴西IP
                    AppSimpleBean simpleBean = new AppSimpleBean();
                    simpleBean.setPackagename("com.mm.droid.livetv.tvees");
                    appSimpleBeans.add(0,simpleBean);
                }*/


			}

		}

		for (int i = 0; i < simpleList.size(); i++) {
			for (int j = 0; j < mList.size(); j++) {
				if (simpleList.get(i).getPackagename()
						.equals(mList.get(j).getApppackagename())) {
					mList.get(j).setCheck(true);
				}
			}
		}

		if (mList != null) {
			list = mList;
			loadAllApp();
		}

	}



	private class AppTask extends AsyncTask<Void, Void, Object> {

		@Override
		protected Object doInBackground(Void... arg0) {
			

			ArrayList<AppInfoBean> list = AppUtils
					.getApplicationMsg(AppFavoritesActivity.this);
			ArrayList<AppSimpleBean> simpleList = DBUtils.getInstance(
					AppFavoritesActivity.this).getFavorites();

			for (int i = 0; i < simpleList.size(); i++) {
				for (int j = 0; j < list.size(); j++) {
					if (simpleList.get(i).getPackagename()
							.equals(list.get(j).getApppackagename())) {
						list.get(j).setCheck(true);
					}
				}
			}

			return list;
		}

		@Override
		protected void onPostExecute(Object result) {
			
			super.onPostExecute(result);
			if (result != null) {
				list = (ArrayList<AppInfoBean>) result;
				mHandler.sendEmptyMessage(Handler_update);
			}
		}

	}

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case Handler_update:
					loadAllApp();
					break;

				default:
					break;
			}
			return false;
		}
	});


	@Override
	public void appChange(String packageName) {
		

		if (!DBUtils.getInstance(AppFavoritesActivity.this).isExistData(
				packageName)
				&& currentPackageName != null
				&& currentPackageName.equals(packageName)) {
			DBUtils.getInstance(AppFavoritesActivity.this).addFavorites(
					packageName);
			currentPackageName = null;
		}

		loadDataApp();
	}

	@Override
	public void appUnInstall(String packageName) {
		if (resident.contains(packageName))
			return;
		
		int code = DBUtils.getInstance(AppFavoritesActivity.this)
				.deleteFavorites(packageName);

		if (code > 0) {
			currentPackageName = packageName;
		} else {
			currentPackageName = null;
		}

		loadDataApp();
	}

	@Override
	public void appInstall(String packageName) {
		
		loadDataApp();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}


}
