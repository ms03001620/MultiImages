/*
 * Copyright (C) 2015 zhenjin ma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.mark.multiimage.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ImagesBaseActivity extends FragmentActivity{

	protected static int sPosotion;
	protected static final int MAX_SEND = 5;
	protected static List<ImageEntity> sResult = new ArrayList<ImageEntity>();
	protected static boolean isOriginal;
	
	protected Context mContext;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mContext = this;
		if(MainApp.getContext()==null);{
			MainApp.setsContext(mContext);
		}
	}
	
	public void submit(){
		ArrayList<Uri> respUris = new ArrayList<Uri>();
		if(isOriginal){
			for(int i=0;i<sResult.size();i++){
				ImageEntity data = sResult.get(i);
				Uri uri = Uri.parse("file:///"+data.getUrl());
				respUris.add(uri);
			}
		}else{
			for(int i=0;i<sResult.size();i++){
				ImageEntity data = sResult.get(i);
				Bitmap bm = BitmapUtils.getThumbnail(this, data.getId());
				String path = getCachePath();
				if(saveCacheDir(path, bm)){
					Uri uri = Uri.parse("file:///"+path);
					respUris.add(uri);
				}
			}
		}
		sResult.clear();
		
		if(respUris!=null && respUris.size()>0){
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra("result", respUris);
			setResult(RESULT_OK, intent);
		}else{
			setResult(RESULT_CANCELED);
		}
		finish();
	}
	
	public boolean saveCacheDir(String filename, Bitmap bitmap) {
		File f = new File(filename);
		try {
			if(f.exists()){
				f.deleteOnExit();
			}
			f.createNewFile();
			
		    FileOutputStream out = new FileOutputStream(f);
		    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		    out.flush();
		    out.close();
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private String getCachePath(){
		String fileName = String.valueOf(System.currentTimeMillis())+".jpg";
		String folder = this.getCacheDir().getPath();
		String fullPath = folder+"/"+fileName;
		return fullPath;
	}
	
	protected void finishAndClear(){
		//isOriginal = false;
		sPosotion = 0;
		sResult.clear();
		finish();
	}
}
