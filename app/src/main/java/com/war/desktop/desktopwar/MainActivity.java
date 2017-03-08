package com.war.desktop.desktopwar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button btn_story, btn_random, btn_diy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_story = (Button) findViewById(R.id.btn_story);
        btn_random = (Button) findViewById(R.id.btn_random);
        btn_diy = (Button) findViewById(R.id.btn_diy);

        final int[] player_group = {1, 2, 3, 4};

        btn_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GameStart.class);
                i.putExtra("mapNum", 1);
                startActivity(i);
            }
        });

        btn_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GameStart.class);
                i.putExtra("mapNum", 0);
                i.putExtra("map_area", 7);
                i.putExtra("player_num", 4);
                i.putExtra("player_group", player_group);
                startActivity(i);
            }
        });

        btn_diy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GameStart.class);
                i.putExtra("mapNum", 2);
                startActivity(i);
            }
        });
    }
}
