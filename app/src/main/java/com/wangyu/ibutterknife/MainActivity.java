package com.wangyu.ibutterknife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wangyu.annotation.OnClick;
import com.wangyu.annotation.ViewInject;

public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.fuck)
    TextView fuck;
    @ViewInject(R.id.fuck1)
    TextView getFuck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBind.bind(this);
        fuck.setText("fuck you");
        getFuck.setText("fuck her");
    }
    @OnClick({R.id.fuck,R.id.fuck1})
    public   void  as(View view){
        startActivity(new Intent(this,Main2Activity.class));
        Toast.makeText(this, ((TextView) view).getText().toString(),Toast.LENGTH_SHORT).show();
    }
}
