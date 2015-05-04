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

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Vector;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class AsyncImageLoaderImpl {

    private final static String TAG = "AsyncImageLoaderImpl";

    private HashMap<String, SoftReference<Bitmap>> mImageMap;
    private Vector<String> mTaskList;
    private static AsyncImageLoaderImpl instance;

    public AsyncImageLoaderImpl() {
        mImageMap = new HashMap<String, SoftReference<Bitmap>>();
        mTaskList = new Vector<String>();
    }

    public static AsyncImageLoaderImpl getInstance() {
        if (instance == null) {
            instance = new AsyncImageLoaderImpl();
        }
        return instance;
    }

    public Bitmap loadDrawable(LoadImageTask task) {
        String imageUrl = task.getUrl();
        if (mImageMap.containsKey(imageUrl)) {
            SoftReference<Bitmap> softReference = mImageMap.get(imageUrl);
            Bitmap drawable = softReference.get();
            if (drawable != null) {
                return drawable;
            }
        }
        if (!mTaskList.contains(imageUrl)) {
            mTaskList.add(imageUrl);
            LoadImageAsync aTask = new LoadImageAsync(task);
            aTask.execute(0);
        }
        return null;
    }

    class LoadImageAsync extends AsyncTask<Object, Void, Bitmap> {
        private LoadImageTask task;

        public LoadImageAsync(LoadImageTask task) {
            this.task = task;
        }

        @Override
        protected Bitmap doInBackground(Object... arg0) {
            Bitmap result = null;
            String url = task.getUrl();
            ImageEntity entity = task.getEntity();

            if (entity != null) {
                result = BitmapUtils.getThumbnail(MainApp.getContext(), entity.getId());
            } else {
                result = BitmapUtils.getBitmapFromPath(url);
            }
            if (result != null) {
                // find at local
                return result;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mTaskList.remove(task.getUrl());
            if (result == null) {
                return;
            }
            mImageMap.put(task.getUrl(), new SoftReference<Bitmap>(result));
            task.back(result);
        }
    }

    public interface ImageCallback {
        public void imageLoaded(Bitmap bitmap, String imageUrl);
    }

    public static void loadImageLocal(final ImageView img, ImageEntity entity, int res, final View mListView) {
        String url = entity.getUrl();
        img.setTag(url);
        LoadImageTask task = new LoadImageTask(url, null, new ImageCallback() {
            public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
                if (mListView == null) {
                    img.setImageBitmap(imageDrawable);
                    return;
                }
                ImageView imageViewByTag = (ImageView) mListView.findViewWithTag(imageUrl);
                if (imageViewByTag != null) {
                    img.setScaleType(ScaleType.CENTER_CROP);
                    imageViewByTag.setImageBitmap(imageDrawable);
                }
            }
        });

        task.setEntity(entity);

        Bitmap cachedImage = AsyncImageLoaderImpl.getInstance().loadDrawable(task);
        if (cachedImage == null) {
            if (res == 0) {
                img.setImageBitmap(null);
            } else {
                img.setScaleType(ScaleType.CENTER);
                img.setImageResource(res);
            }
        } else {
            img.setScaleType(ScaleType.CENTER_CROP);
            img.setImageBitmap(cachedImage);
        }
    }
}
