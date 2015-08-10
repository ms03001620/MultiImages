package cn.com.mark.multiimage.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtils {

	private final static String DEF_STRING = "";
	private final static int DEF_NUM = Integer.MIN_VALUE;
	private final static long DEF_LONG = Long.MIN_VALUE; 
	private final static float DEF_REAL = Float.MAX_VALUE;

	private static PreferencesUtils instance;

	private SharedPreferences sharedPreferences;

	public static void init(Context context, String name) {
		if (instance == null) {
			instance = new PreferencesUtils(context, name);
		}
	}

	private PreferencesUtils(Context context, String name) {
		sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	public static String getString(String key) {
		return getString(key, DEF_STRING);
	}

	public static long getLong(String key) {
		return getLong(key, DEF_LONG);
	}

	public static String getString(String key, String defValue) {
		return instance.sharedPreferences.getString(key, defValue);
	}

	public static void putBoolean(String key, boolean value) {
		Editor editor = instance.sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static boolean getBoolean(String key, boolean defValue){
		return instance.sharedPreferences.getBoolean(key, defValue);
	}
	
	public static void putString(String key, String defValue) {
		Editor editor = instance.sharedPreferences.edit();
		editor.putString(key, defValue);
		editor.commit();
	}

	public static long getLong(String key, long defValue) {
		return instance.sharedPreferences.getLong(key, defValue);
	}

	public static void setLong(String key, long defValue) {
		Editor editor = instance.sharedPreferences.edit();
		editor.putLong(key, defValue);
		editor.commit();
	}
}
