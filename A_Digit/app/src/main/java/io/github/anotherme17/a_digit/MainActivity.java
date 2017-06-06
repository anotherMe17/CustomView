package io.github.anotherme17.a_digit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = new ListView(this);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, getData()));
        setContentView(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        Intent intent = new Intent(MainActivity.this, CountDownActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });
    }

    private List<String> getData() {
        List<String> data = new ArrayList<String>();
        data.add("倒计时");
        return data;
    }

   /* @Override
    protected void onResume() {
        super.onResume();
        //digit.sync();
        digit.setChar(2);
        AssetManager mgr=getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "font/ttf.ttf");//根据路径得到Typeface
        digit.setFont(tf);
    }*/
}
