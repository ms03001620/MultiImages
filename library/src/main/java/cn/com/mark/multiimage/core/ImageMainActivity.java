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

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageMainActivity extends ImagesBaseActivity {

	private GridView mGridView;
	private ImageShowAdapter mGridAdapter;
	private TextView mTextSend;
	private TextView mTextPreview;
	private TextView mTextFolder;
	private ImageFolderWindows mMenuWindows;
	private ImageFolderAdapter mFolderAdapter;
	private static int REQUESTCODE = 10010;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_main);
		isOriginal = this.getIntent().getBooleanExtra("action-original", false);

		mTextSend = (TextView)this.findViewById(R.id.send);
		mTextPreview = (TextView)this.findViewById(R.id.preview);
		mTextFolder = (TextView)this.findViewById(R.id.text_folder);
		mGridView = (GridView)this.findViewById(R.id.grid);
		mGridAdapter = new ImageShowAdapter(this, mGridView, sResult);
		mFolderAdapter = new ImageFolderAdapter(this);

		mGridView.setAdapter(mGridAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int postition, long id) {
				ImageShowAdapter adapter = (ImageShowAdapter) mGridView.getAdapter();
				Intent intent = new Intent(ImageMainActivity.this, PreviewActivity.class);
				intent.putParcelableArrayListExtra("action-data", adapter.getData());
				sPosotion = postition;
				startActivityForResult(intent, REQUESTCODE);
			}
		});
		
		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishAndClear();
			}
		});
		
		mGridAdapter.setPostCallBack(new ImagePost() {
			@Override
			public void onPost() {
				updateBtn();
			}
		});
		
		mTextSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		});
		mTextPreview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ImageMainActivity.this, PreviewActivity.class);
				intent.putExtra("preview", true);
				startActivityForResult(intent, REQUESTCODE);
			}
		});

		RelativeLayout layout = (RelativeLayout) View.inflate(this, R.layout.image_layout_window_ddr, null);
		final View maskView = findViewById(R.id.view_mask);
		
		mTextFolder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				maskView.postDelayed(new Runnable() {
					@Override
					public void run() {
						maskView.setVisibility(View.VISIBLE);
					}
				}, 500);
				mMenuWindows.showAtLocation2(view, Gravity.LEFT | Gravity.BOTTOM, 0, getResources().getDimensionPixelSize(R.dimen.image_head_height));
			}
		});
		
		ListView folderlist = (ListView) layout.findViewById(R.id.lv_dialog);
		
		folderlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				ImageFolderEntity entity = mFolderAdapter.getItem(position);

				mGridAdapter.update(entity.getList());
				mFolderAdapter.setSelection(position);
				mTextFolder.setText(entity.getName());
				view.postDelayed(new Runnable() {
					@Override
					public void run() {
						mMenuWindows.dismiss();
					}
				}, 200);
			}
		});

		mFolderAdapter.setListView(folderlist);
		folderlist.setAdapter(mFolderAdapter);
		mMenuWindows = new ImageFolderWindows(this, layout);
		mMenuWindows.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				maskView.setVisibility(View.INVISIBLE);
			}
		});

		loadForTask(0);
	}
	
	public void updateBtn(){
		boolean isEmpty = sResult.size()==0;
		mTextSend.setEnabled(!isEmpty);
		mTextPreview.setEnabled(!isEmpty);
		if(isEmpty){
			mTextSend.setText(R.string.complete);
			mTextPreview.setText(R.string.preview);
		}else{
			String format = getString(R.string.format_send_count);
			String sendCount = String.format(format, sResult.size(), ImagesBaseActivity.MAX_SEND);
			mTextSend.setText(sendCount);
			
			String formatpre = getString(R.string.format_preview);
			String preCount = String.format(formatpre, sResult.size());
			mTextPreview.setText(preCount);
		}
	}

	public void loadForTask(int type){
		HashMap<String, ImageFolderEntity> mDataFolder = new HashMap<String, ImageFolderEntity>();
		Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ImageEntity.STORE_IMAGES);
        if (cursor != null) { 
            cursor.moveToFirst(); 
            while (cursor.moveToNext()) { 
                ImageEntity entity = new ImageEntity();
    			long id = cursor.getLong(0);
    			String name = cursor.getString(1);
    			if(TextUtils.isEmpty(name)){
    				continue;
    			}
    			String path = cursor.getString(2);
    			long size = cursor.getLong(3);
				long timeAdd = cursor.getLong(4);
				long timeModify = cursor.getLong(5);
				long timeToken = cursor.getLong(6);

                entity.setId(id);
                entity.setName(name);
                entity.setUrl(path);
                entity.setSize(size);
				entity.setTimeAdd(timeAdd);
				entity.setTimeModify(timeModify);
				entity.setTimeToken(timeToken);
                
                String folderName = getFolderName(path);
                ImageFolderEntity folder = mDataFolder.get(folderName);
                if(folder!=null){
                	folder.addFile(entity);
                }else{
                	ImageFolderEntity newFolder = new ImageFolderEntity();
                	newFolder.setPath(path.replace(name, ""));
                	newFolder.setName(folderName);
                	newFolder.addFile(entity);
                	mDataFolder.put(folderName, newFolder);
                }
            } 
            cursor.close();
        }

		List<ImageFolderEntity> mDataDDR = new ArrayList<ImageFolderEntity>();
        if(!mDataFolder.isEmpty()){
            mDataDDR.addAll(mDataFolder.values());

            mGridAdapter.update(new ArrayList<ImageEntity>(mDataDDR.get(0).getList()));
            mFolderAdapter.update(mDataDDR);
            mFolderAdapter.setSelection(0);
			mTextFolder.setText(mDataDDR.get(0).getName());
            mMenuWindows.setListSize(mDataDDR.size());
        }
	}

	
	private static String getFolderName(String path){
		String folderName = "";
		String folders[] =path.split("/");
		if(folders!=null){
			int size = folders.length;
			
			if(size==1){
				folderName = folders[0];
			}else if(size>1){
				folderName = folders[size-2];
			}else{
				folderName = "";
			}
		}
		return folderName;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUESTCODE){
			if(resultCode==RESULT_OK){
				mGridAdapter.notifyDataSetChanged();
				updateBtn();
				
				if(data!=null){
					setResult(RESULT_OK, data);
					finishAndClear();
				}
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finishAndClear();
	}
	
}
