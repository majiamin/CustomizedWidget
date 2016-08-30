package com.qianyue.customizedwidget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn;
    InfoView infoView;
    DampHorizontal dampHorizontal;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        infoView = (InfoView) findViewById(R.id.infoView);
        dampHorizontal = (DampHorizontal) findViewById(R.id.damp);
        lv = (ListView) findViewById(R.id.lv_test);
        String[] data = new String[30];
        for (int i = 0; i < 30; i++) {
            data[i] = "内容" + (i + 1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout
                .simple_list_item_1, data);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "点到了第" + (position + 1) + "个条目", Toast
                        .LENGTH_SHORT).show();
            }
        });
        btn = (Button) findViewById(R.id.btn);
        if (btn != null) {
            btn.setOnClickListener(this);
        }
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点到了", Toast.LENGTH_SHORT).show();
            }
        });

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView tv = new TextView(this);
        tv.setText("这是第五个页面");
        tv.setGravity(Gravity.CENTER);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(tv, layoutParams);
        dampHorizontal.addView(linearLayout, layoutParams);

    }


    private int num = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                num += 1;
                updateInfo();

                break;
            default:
                break;
        }
    }

    private void updateInfo() {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            data.add("信息" + (i + 1));
        }
        infoView.setInfos(data);
    }

}
