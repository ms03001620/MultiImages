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


import android.graphics.Bitmap;

public class LoadImageTask {
    private String url;
    private AsyncImageLoaderImpl.ImageCallback callBack;
    private Integer defaultRes;
    private ImageEntity entity;

    public LoadImageTask(String url, Integer defaultRes, AsyncImageLoaderImpl.ImageCallback callBack) {
        this.url = url;
        this.callBack = callBack;
        this.defaultRes = defaultRes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AsyncImageLoaderImpl.ImageCallback getCallBack() {
        return callBack;
    }

    public void setCallBack(AsyncImageLoaderImpl.ImageCallback callBack) {
        this.callBack = callBack;
    }

    public Integer getDefaultRes() {
        return defaultRes;
    }

    public void setDefaultRes(Integer defaultRes) {
        this.defaultRes = defaultRes;
    }

    public void back(Bitmap bm) {
        callBack.imageLoaded(bm, url);
    }

    public ImageEntity getEntity() {
        return entity;
    }

    public void setEntity(ImageEntity entity) {
        url = entity.getUrl();
        this.entity = entity;
    }
}