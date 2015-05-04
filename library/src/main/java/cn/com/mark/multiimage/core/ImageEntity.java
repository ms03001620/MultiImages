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

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class ImageEntity implements Parcelable {

    public static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE
    };

    private long id;
    private String url;
    private String name;
    private long size;
    private boolean check;

    public ImageEntity(){}

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeLong(id);
        parcel.writeString(url);
        parcel.writeString(name);
        parcel.writeLong(size);
        parcel.writeInt(check ? 1 : 0);
    }

    public static final Parcelable.Creator<ImageEntity> CREATOR = new Parcelable.Creator<ImageEntity>() {
        public ImageEntity createFromParcel(Parcel in) {
            return new ImageEntity(in);
        }

        public ImageEntity[] newArray(int size) {
            return new ImageEntity[size];
        }
    };

    private ImageEntity(Parcel in) {
        id = in.readLong();
        url = in.readString();
        name = in.readString();
        size = in.readLong();
        check = in.readInt() == 0 ? false : true;
    }
}
