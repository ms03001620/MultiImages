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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ImageFolderAdapter extends BaseAdapter {

	private Context mContext;
	private List<ImageFolderEntity> mData;
	private int checkPosition;
	private ListView mListView;

	public ImageFolderAdapter(Context context) {
		this.mContext = context;
		mData = new ArrayList<ImageFolderEntity>();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public ImageFolderEntity getItem(int arg0) {
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return getItem(arg0).getName().hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ImageFolderEntity element = this.getItem(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.image_item_folder_list, null);
		}
		holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.image_ddr_icon = (ImageView) convertView.findViewById(R.id.image_ddr_icon);
			holder.text_ddr_name = (TextView) convertView.findViewById(R.id.text_ddr_name);
			holder.text_ddr_file_conut = (TextView) convertView.findViewById(R.id.text_ddr_file_conut);
			holder.image_ddr_checked = (ImageView) convertView.findViewById(R.id.image_ddr_checked);
			convertView.setTag(holder);
		}
		
		ImageEntity first = element.getFirst();
		if(first!=null){
			AsyncImageLoaderImpl.loadImageLocal(holder.image_ddr_icon, first, R.drawable.image_empty, getListView());
			holder.text_ddr_name.setText(element.getName());
			String format = mContext.getString(R.string.format_file_count);
			String count = String.format(format, element.getCount());
			holder.text_ddr_file_conut.setText(count);
		}
		
		if(checkPosition == position){
			holder.image_ddr_checked.setVisibility(View.VISIBLE);
		}else{
			holder.image_ddr_checked.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	public ListView getListView() {
		return mListView;
	}

	public void setListView(ListView listView) {
		mListView = listView;
	}

	final class ViewHolder {
		ImageView image_ddr_icon;
		TextView text_ddr_name;
		TextView text_ddr_file_conut;
		ImageView image_ddr_checked;
	}

	public void update(List<ImageFolderEntity> data){
		if(data==null || data.size()==0){
			mData.clear();
		}else{
			if(mData.size()==0){
				mData.addAll(data);
			}
		}
		notifyDataSetChanged();
	}
	
	public void setSelection(int position){
		checkPosition = position;
		notifyDataSetChanged();
	}
}
