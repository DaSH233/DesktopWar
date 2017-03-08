package com.war.desktop.desktopwar;

/**
 * Created by czy on 2016/12/29.
 */
public class GameMap {
    private int map_area;
    private int[][] group;
    private int[][] rank;
    private int[][] type;

    public GameMap(int map_area) {
        this.map_area = map_area;
        group = new int[map_area][map_area];
        rank = new int[map_area][map_area];
        type = new int[map_area][map_area];
        int i, j;
        for(i = 0; i < map_area; i++) {
            for (j = 0; j < map_area; j++){
                this.group[i][j] = -1;
                this.rank[i][j] = 0;
                this.type[i][j] = 0;
            }
        }
    }

    public GameMap(int map_area, int[][] group, int[][] rank, int[][] type){
        this.map_area = map_area;
        this.group = new int[map_area][map_area];
        this.rank = new int[map_area][map_area];
        this.type = new int[map_area][map_area];
        int i, j;
        for(i = 0; i < map_area; i++) {
            for (j = 0; j < map_area; j++){
                this.group[i][j] = group[i][j];
                this.rank[i][j] = rank[i][j];
                this.type[i][j] = type[i][j];
            }
        }
    }

    public void setGameMap(int map_area, int[][] group, int[][] rank, int[][] type){
        this.map_area = map_area;
        this.group = new int[map_area][map_area];
        this.rank = new int[map_area][map_area];
        this.type = new int[map_area][map_area];
        int i, j;
        for(i = 0; i < map_area; i++) {
            for (j = 0; j < map_area; j++){
                this.group[i][j] = group[i][j];
                this.rank[i][j] = rank[i][j];
                this.type[i][j] = type[i][j];
            }
        }
    }

    public void setGameMap(GameMap gameMap){
        this.map_area = gameMap.getMap_area();
        this.group = new int[map_area][map_area];
        this.rank = new int[map_area][map_area];
        this.type = new int[map_area][map_area];
        int i, j;
        for(i = 0; i < map_area; i++) {
            for (j = 0; j < map_area; j++){
                this.group[i][j] = gameMap.getGroup()[i][j];
                this.rank[i][j] = gameMap.getRank()[i][j];
                this.type[i][j] = gameMap.getType()[i][j];
            }
        }
    }

    public void setMap_area(int map_area) {
        this.map_area = map_area;
    }

    public void setGroup(int[][] group) {
        int i, j;
        for(i = 0; i < map_area; i++) {
            for (j = 0; j < map_area; j++){
                this.group[i][j] = group[i][j];
            }
        }
    }

    public void setRank(int[][] rank) {
        int i, j;
        for(i = 0; i < map_area; i++) {
            for (j = 0; j < map_area; j++){
                this.rank[i][j] = rank[i][j];
            }
        }
    }

    public void setType(int[][] type) {
        int i, j;
        for(i = 0; i < map_area; i++) {
            for (j = 0; j < map_area; j++){
                this.type[i][j] = type[i][j];
            }
        }
    }

    public int getMap_area() {
        return map_area;
    }

    public int[][] getGroup() {
        return group;
    }

    public int[][] getRank() {
        return rank;
    }

    public int[][] getType() {
        return type;
    }
}
