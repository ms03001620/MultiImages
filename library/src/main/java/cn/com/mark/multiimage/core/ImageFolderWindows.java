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

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class ImageFolderWindows extends PopupWindow {
	private View mMenuView;
	private Context mContext;
	
	public ImageFolderWindows(Activity context, View menuView) {
		super(context);
		mContext = context;
		this.mMenuView = menuView;
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.popwin_anim_style);
		this.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
		this.setContentView(mMenuView);
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}
	
	public void showAtLocation2(View parent, int gravity, int x, int y) {
		showAtLocation(parent, gravity, x, y);
	}
	public void setListSize(int size) {
		float max = 5;
		if(size<=max){
			this.setHeight(LayoutParams.WRAP_CONTENT);
		}else{
			float lineHeight = mContext.getResources().getDimension(R.dimen.image_folder_height);
			int totalHeight = Math.round(lineHeight * max);
			this.setHeight(totalHeight);
		}
	}
}
