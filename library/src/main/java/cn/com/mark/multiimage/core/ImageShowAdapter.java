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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageShowAdapter extends BaseAdapter{
	private ImagePost postCallBack;
	private Context mContext;
	private List<ImageEntity> mData;
	private View mListView;
	private List<ImageEntity> mResult;
	private int checkPosition;
	
	public ImageShowAdapter(Context context, View view, List<ImageEntity> result) {
		this.mContext = context;
		mData = new ArrayList<ImageEntity>();
		mListView = view;
		mResult = result;
	}

	@Override
	public int getCount() {
		return mData.size();
	}
	
	public List<ImageEntity> getData(){
		return mData;
	}

	@Override
	public ImageEntity getItem(int arg0) {
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return getItem(arg0).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ImageEntity element = this.getItem(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.image_item_grid_element,null);
		}
		holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.image = (ImageView)convertView.findViewById(R.id.image_grid);
			holder.imageMark = (ImageView)convertView.findViewById(R.id.image_mark);
			holder.check = (ImageView)convertView.findViewById(R.id.check);
			convertView.setTag(holder);
		}
		
		holder.check.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(!element.isCheck()){
					int size = mResult.size();
					if(size>=ImagesBaseActivity.MAX_SEND){
						String format = mContext.getString(R.string.format_warn_max_send);
						String warn = String.format(format, ImagesBaseActivity.MAX_SEND);
						Toast.makeText(mContext, warn, Toast.LENGTH_SHORT).show();
						return;
					}
					element.setCheck(true);
					mResult.add(element);
				}else{
					element.setCheck(false);
					mResult.remove(element);
				}
				notifyDataSetChanged();
				if(postCallBack!=null){
					postCallBack.onPost();
				}
			}
		});

		if(element!=null) {
			AsyncImageLoaderImpl.loadImageLocal(holder.image, element, R.drawable.image_empty, mListView);
			boolean hasChecked = element.isCheck();
			if (hasChecked) {
				holder.check.setImageResource(R.drawable.image_check_bg_on);
			} else {
				holder.check.setImageResource(R.drawable.image_check_bg);
			}
			holder.imageMark.setVisibility(hasChecked ? View.VISIBLE : View.INVISIBLE);
		}
		return convertView; 
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	public void update(List<ImageEntity> data){
		//checkPosition = position;
		if(data==null || data.size()==0){
			mData.clear();
		}else{
			if(mData.size()==0){
				mData.addAll(data);
			}else{
				mData.clear();
				mData.addAll(data);
			}
		}
		notifyDataSetChanged();
	}
	
	final class ViewHolder {
		ImageView image;
		ImageView imageMark;
		ImageView check;
	}
	
	public ImagePost getPostCallBack() {
		return postCallBack;
	}

	public void setPostCallBack(ImagePost postCallBack) {
		this.postCallBack = postCallBack;
	}

}
