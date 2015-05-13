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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PreviewActivity extends ImagesBaseActivity {
    private ViewPager mPager;
    private TextView mTextTitle;
    private TextView mTextInfo;
    private CheckBox mCheckOrigrnal;
    private CheckBox mCheckOn;
    private TextView mTextSend;
    private ArrayList<ImageEntity> datas;
    private boolean isPerViewMode;
    private int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mTextTitle = (TextView) findViewById(R.id.title);
        mTextInfo = (TextView) findViewById(R.id.text_info);
        mTextSend = (TextView) this.findViewById(R.id.send);
        mCheckOrigrnal = (CheckBox) findViewById(R.id.select_original1);
        mCheckOn = (CheckBox) findViewById(R.id.select_on);
        mPager = (ViewPager) findViewById(R.id.pager);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        mTextSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                submit();
            }
        });

        Intent intent = this.getIntent();
        if (intent != null) {
            isPerViewMode = intent.getBooleanExtra("preview", false);
        }
        if (isPerViewMode) {
            datas = new ArrayList<ImageEntity>(sResult);
            current = 0;
        } else {
            datas = intent.getParcelableArrayListExtra("action-data");
            current = sPosotion;
        }

        updateTitle();
        updateCheckStatus();
        updateSendBtn();
        updateOrgrnalSize();
        mCheckOrigrnal.setChecked(isOriginal);

        mCheckOrigrnal.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                isOriginal = isChecked;
                updateOrgrnalSize();
            }
        });

        mPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
        mPager.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int arg0) {
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageSelected(int arg0) {
                current = arg0;
                updateTitle();
                updateCheckStatus();
                updateOrgrnalSize();
            }
        });

        mPager.setCurrentItem(current);
        mCheckOn.setOnCheckedChangeListener(lisChecked);
    }

    OnCheckedChangeListener lisChecked = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton view, boolean isChecked) {
            setResult(RESULT_OK);
            ImageEntity element = datas.get(current);
            if (!element.isCheck()) {
                int size = sResult.size();
                if (size >= ImagesBaseActivity.MAX_SEND) {
                    String format = mContext.getString(R.string.format_warn_max_send);
                    String warn = String.format(format, ImagesBaseActivity.MAX_SEND);
                    Toast.makeText(mContext, warn, Toast.LENGTH_SHORT).show();
                    mCheckOn.setChecked(false);
                    return;
                }
                element.setCheck(true);
                sResult.add(element);
            } else {
                element.setCheck(false);
                sResult.remove(element);
            }
            updateSendBtn();
        }
    };

    private void updateCheckStatus() {
        boolean checked = datas.get(current).isCheck();
        mCheckOn.setOnCheckedChangeListener(null);
        mCheckOn.setChecked(checked);
        mCheckOn.setOnCheckedChangeListener(lisChecked);
    }

    private void updateSendBtn() {
        boolean isEmpyt = sResult.size() == 0;
        mTextSend.setEnabled(!isEmpyt);

        if (isEmpyt) {
            mTextSend.setText(R.string.complete);
        } else {
            String format = getString(R.string.format_send_count);
            String sendCount = String.format(format, sResult.size(), ImagesBaseActivity.MAX_SEND);
            mTextSend.setText(sendCount);
        }
    }

    private void updateOrgrnalSize() {
        if (isOriginal) {
            mTextInfo.setText(getFileSize());
        } else {
            mTextInfo.setText(R.string.original);
        }
    }

    private void updateTitle() {
        int all = datas.size();
        String newtitle = current + 1 + "/" + all;
        mTextTitle.setText(newtitle);
    }

    private String getFileSize() {
        ImageEntity image = datas.get(current);
        String showSize = getString(image.getSize());
        return showSize;
    }

    public String getAllFileSize() {
        double totalbyte = 0;
        if (datas.size() > 0) {
            for (ImageEntity data : sResult) {
                totalbyte += data.getSize();
            }
        }
        return getString(totalbyte);
    }

    private String getString(double totalbyte) {
        if (totalbyte == 0) {
            return "原图";
        } else {
            if (totalbyte > 1024 * 1024) {
                double tms = totalbyte / 1024 / 1024;
                return "原图(" + sizeFormat(tms) + "M)";
            } else {
                double tms = totalbyte / 1024;
                if (tms == 0) {
                    return "原图";
                }
                return "原图(" + sizeFormat(tms) + "K)";
            }
        }
    }

    private String sizeFormat(double value) {
        DecimalFormat fnum = new DecimalFormat("##0.0");
        return fnum.format(value);
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Fragment getItem(int position) {
            ImageEntity image = datas.get(position);
            return ImageDetailFragment.newInstance(image);
        }
    }

}
