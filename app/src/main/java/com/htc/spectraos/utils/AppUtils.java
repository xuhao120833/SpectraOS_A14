package com.htc.spectraos.utils;

/**
 * @author  作者：zgr
 * @version 创建时间：2016年11月3日 下午5:50:52
 * 类说明
 */

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.htc.spectraos.MyApplication;
import com.htc.spectraos.entry.AppInfoBean;
import com.htc.spectraos.entry.SpecialApps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppUtils {

	/**
	 * 获取全部应用程序的信息
	 *
	 * @param context
	 * @return
	 */
	public static ArrayList<AppInfoBean> getApplicationMsg(Context context) {
		ArrayList<AppInfoBean> list = new ArrayList<AppInfoBean>();
		PackageManager pm = context.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.GET_UNINSTALLED_PACKAGES);
		if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP){
			List<ResolveInfo> resolveInfos_lb = leanbackActivitiesIn(pm);
			for (ResolveInfo resolveInfo_lb:resolveInfos_lb) {
				boolean has_flag = false;
				for (ResolveInfo resolveInfo: resolveInfos) {
					if (resolveInfo_lb.activityInfo.packageName.equals(resolveInfo.activityInfo.packageName)){
						has_flag = true;
						break;
					}
				}

				if (has_flag)
					continue;

				resolveInfos.add(resolveInfo_lb);

			}
		}

		String[] filterApps = MyApplication.config.filterApps.split(";");
		List<String> stringList = Arrays.asList(filterApps);

		String country_code = Settings.System.getString(context.getContentResolver(),"ip_country_code");
		country_code = country_code==null?"亚洲,CN":country_code;
		String[] continent_countryCode = country_code.split(",");

		// 调用系统排序 ， 根据name排序
		// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));
		for (ResolveInfo reInfo : resolveInfos) {
			String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
			String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
			if(stringList.contains(pkgName) || "com.htc.launcher".equals(pkgName))
				continue;

			if (continent_countryCode.length>=2 && MyApplication.config.specialApps !=null && MyApplication.config.specialApps.size()>0) {

				boolean filter = false;
				for (SpecialApps specialApps : MyApplication.config.specialApps){

					if (!specialApps.getPackageName().equals(pkgName))
						continue;

					if (specialApps.getContinent()!=null && !specialApps.getContinent().equals("")){
						if (specialApps.getContinent().contains("!")){
							if (specialApps.getContinent().replace("!","").equals(continent_countryCode[0])) {
								filter=true;
								break;
							}
						}else {
							if (!specialApps.getContinent().equals(continent_countryCode[0])) {
								filter=true;
								break;
							}
						}
					}

					if (specialApps.getCountryCode()!=null && !specialApps.getCountryCode().equals("")){
						if (specialApps.getCountryCode().contains("!")){
							if (specialApps.getCountryCode().replace("!","").equals(continent_countryCode[1])) {
								filter=true;
								break;
							}
						}else {
							if (!specialApps.getCountryCode().equals(continent_countryCode[1])) {
								filter=true;
								break;
							}
						}
					}
					break;
				}

				if (filter)
					continue;
			}

			String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
			Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
			// 创建一个AppInfo对象，并赋值
			AppInfoBean appInfo = new AppInfoBean();
			appInfo.setAppname(appLabel);
			appInfo.setApppackagename(pkgName);
			appInfo.setApplicationInfo(reInfo.activityInfo.applicationInfo);
			appInfo.setAppicon(icon);
			appInfo.setMname(activityName);

			list.add(appInfo); // 添加至列表中

		}
		return list;
	}

	public static ArrayList<AppInfoBean> getApplicationManagerMsg(Context context) {
		ArrayList<AppInfoBean> list = new ArrayList<AppInfoBean>();
		PackageManager pm = context.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.GET_UNINSTALLED_PACKAGES);
		if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP){
			List<ResolveInfo> resolveInfos_lb = leanbackActivitiesIn(pm);
			for (ResolveInfo resolveInfo_lb:resolveInfos_lb) {
				boolean has_flag = false;
				for (ResolveInfo resolveInfo: resolveInfos) {
					if (resolveInfo_lb.activityInfo.packageName.equals(resolveInfo.activityInfo.packageName)){
						has_flag = true;
						break;
					}
				}

				if (has_flag)
					continue;

				resolveInfos.add(resolveInfo_lb);

			}
		}
		String[] filterApps = MyApplication.config.managerFilterApps.split(";");
		List<String> stringList = Arrays.asList(filterApps);
		// 调用系统排序 ， 根据name排序
		// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));
		for (ResolveInfo reInfo : resolveInfos) {
			String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
			String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
			if(stringList.contains(pkgName)|| "com.htc.launcher".equals(pkgName))
				continue;

			String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
			Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
			// 创建一个AppInfo对象，并赋值
			AppInfoBean appInfo = new AppInfoBean();
			appInfo.setAppname(appLabel);
			appInfo.setApppackagename(pkgName);
			appInfo.setAppicon(icon);
			appInfo.setApplicationInfo(reInfo.activityInfo.applicationInfo);
			appInfo.setMname(activityName);

			list.add(appInfo); // 添加至列表中


		}
		return list;
	}

	private static List<ResolveInfo> getResolveInfos(PackageManager packageManager, Intent intent) {
		return packageManager.queryIntentActivities(intent, PackageManager.GET_UNINSTALLED_PACKAGES);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static List<ResolveInfo> leanbackActivitiesIn(PackageManager packageManager) {
		Intent intent = new Intent()
				.setAction(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
		return getResolveInfos(packageManager, intent);
	}

	/**
	 * 根据应用包名获取应用信息
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static AppInfoBean getApplicationMsg_Package(Context context,
                                                        String packageName) {
		AppInfoBean appInfo = null;
		PackageManager pm = context.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.GET_UNINSTALLED_PACKAGES);
		// 调用系统排序 ， 根据name排序
		// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));
		for (ResolveInfo reInfo : resolveInfos) {
			String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
			String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
			String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
			Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
			// 为应用程序的启动Activity 准备Intent
			// 创建一个AppInfo对象，并赋值
			if (pkgName.equals(packageName)) {
				appInfo = new AppInfoBean();
				appInfo.setAppname(appLabel);
				appInfo.setApppackagename(pkgName);
				appInfo.setAppicon(icon);
				appInfo.setMname(activityName);
			}
		}
		return appInfo;
	}

	/**
	 * 根据包和类启动
	 * 
	 * @param context
	 * @param packageName
	 * @param className
	 */
	public static void startNewApp(Context context, String packageName,
                                   String className) {
		if (checkPackage(context, packageName)) {
			try {
				Intent intent = new Intent();
				ComponentName component = new ComponentName(packageName, className);
				intent.setComponent(component);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			 }catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 判断包是否存在
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean checkPackage(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 根据包启动APK
	 * 
	 * @param context
	 * @param packageName
	 */
	public static boolean startNewApp(Context context, String packageName) {
		try {
			PackageManager packageManager = context.getPackageManager();
			Intent intent = packageManager.getLaunchIntentForPackage(packageName);
			if (intent != null) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				return true;
			}
			intent = packageManager.getLeanbackLaunchIntentForPackage(packageName);
			if (intent != null) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void startNewActivity(Context context,Class<?> cls){
		Intent intent = new Intent(context,cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}
