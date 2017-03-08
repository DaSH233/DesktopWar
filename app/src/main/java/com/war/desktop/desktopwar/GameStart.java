package com.war.desktop.desktopwar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameStart extends Activity {

    private Button btn_atk, btn_up, btn_end, btn_cancel;
    private int player_num;
    private int[] player_group;
    private int map_area;
    private GameView gameView;
    private GameMap gameMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_start);
        Intent i = getIntent();
        int num = i.getIntExtra("mapNum", 0);
        if(num > 0) {
            loadMap(num);
        }
        else {
            map_area = i.getIntExtra("map_area", 7);
            player_num = i.getIntExtra("player_num", 4);
            player_group = i.getIntArrayExtra("player_group");
            gameMap = new GameMap(map_area);
            createMap();
        }
        initButton();
        gameView = (GameView) findViewById(R.id.GameView);
        gameView.addButton(btn_atk);
        gameView.addButton(btn_up);
        gameView.addButton(btn_end);
        gameView.addButton(btn_cancel);
        gameView.setMap(gameMap);
        gameView.setCtrl(1);
    }

    public void initButton() {
        btn_atk = (Button) findViewById(R.id.btn_attack);
        btn_up = (Button) findViewById(R.id.btn_levelup);
        btn_end = (Button) findViewById(R.id.btn_end);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_up.setScaleX(0);
        btn_up.setScaleY(0);
        btn_up.setEnabled(false);
        btn_atk.setScaleX(0);
        btn_atk.setScaleY(0);
        btn_atk.setEnabled(false);
        btn_end.setRotation(90);
        btn_cancel.setScaleX(0);
        btn_cancel.setScaleY(0);
        btn_cancel.setEnabled(false);
    }

    public void loadMap(int num) {
        try {
            String fileName = new String("map" + String.valueOf(num) + ".txt");
            InputStream is = getAssets().open(fileName);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            is.close();
            String result = new String(buffer, "utf8");
            int i, j;
            int head_size;
            map_area = result.charAt(0) - '0';
            player_num = result.charAt(3) - '0';
            player_group = new int[player_num];
            for(i = 0; i < player_num; i++)
                player_group[i] = result.charAt((i + 2) * 3) - '0';
            head_size = (player_num + 2) * 3 + 2;
            gameMap = new GameMap(map_area);
            for (j = 0; j < map_area; j++) {
                for (i = 0; i < map_area; i++) {
                    if(result.charAt(head_size + (j * map_area + i) * 3) == 'n') {
                        gameMap.getGroup()[j][i] = -1;
                    }
                    else {
                        gameMap.getGroup()[j][i] = result.charAt(head_size + (j * map_area + i) * 3) - '0';
                    }
                    gameMap.getRank()[j][i] = result.charAt(head_size + ((j + map_area) * map_area + i) * 3 + 2) - '0';
                    gameMap.getType()[j][i] = result.charAt(head_size + ((j + 2 * map_area) * map_area + i) * 3 + 4) - '0';
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createMap() {
        int i, j;
        List<Integer> list = new ArrayList<>();
        for(j = -2; j < player_num; j++) {
            for(i = 0; i < map_area * map_area / (player_num + 2) + 1; i++) {
                if(j < 0)
                    list.add(j + 1);
                else
                    list.add(player_group[j]);
            }
        }
        Collections.shuffle(list);
        for(j = 0; j < map_area; j++) {
            for(i = 0; i < map_area; i++) {
                gameMap.getGroup()[j][i] = list.get(j * map_area + i).intValue();
                if(gameMap.getGroup()[j][i] > 0) {
                    gameMap.getRank()[j][i] = 1;
                    gameMap.getType()[j][i] = 1;
                }
                else {
                    gameMap.getRank()[j][i] = 0;
                    gameMap.getType()[j][i] = 0;
                }
            }
        }
    }
}
