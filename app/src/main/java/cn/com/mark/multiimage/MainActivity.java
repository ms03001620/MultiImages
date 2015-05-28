package cn.com.mark.multiimage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;

import cn.com.mark.multiimage.core.ImageMainActivity;


public class MainActivity extends ActionBarActivity {
    private TextView mTextView;
    private final static int REQUESTCODE = 10010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView)findViewById(R.id.text);
    }

    public void onBtnGetClick(View view){
        Intent intent = new Intent(MainActivity.this, ImageMainActivity.class);
        intent.putExtra("action-original", true);
        startActivityForResult(intent, REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode == REQUESTCODE){
                String paths = "";
                ArrayList<Uri> images = data.getParcelableArrayListExtra("result");
                for(int i=0;i<images.size();i++){
                    paths += images.get(i).getPath()+"\n\n";
                }
                mTextView.setText(paths);
            }
        }
    }
}
