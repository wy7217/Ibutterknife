package com.wangyu.ibutterknife;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wangyu.annotation.OnClick;
import com.wangyu.annotation.ViewInject;

public class Main2Activity extends AppCompatActivity {
    @ViewInject(R.id.llll)

    LinearLayout linearLayout;
    @ViewInject(R.id.qwqw)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ViewBind.bind(this);
        linearLayout.setGravity(LinearLayout.HORIZONTAL);
        imageView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.qwqw)
    public void setLinearLayout(View linearLayout) {
    }
}
