package com.war.desktop.desktopwar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by czy on 2016/12/30.
 */
public class Player {
    private int group = 0;
    private int block_num = 0;
    private int near_block_num = 0;
    private int skill_point = 0;
    private List<Integer> block_order = new ArrayList<>();

    public Player(){
        group = 0;
        block_num = 0;
        near_block_num = 0;
        skill_point = 0;
    }

    public Player(int group, int block_num){
        this.group = group;
        this.block_num = block_num;
    }

    public void setPlayer(int group, int block_num){
        this.group = group;
        this.block_num = block_num;
    }

    public void addBlock(int block) {
        block_order.add(new Integer(block));
    }

    public void removeBlcok(int block) {
        for(int i = 0; i < block_order.size(); i++) {
            if(block_order.get(i).intValue() == block) {
                block_order.remove(i);
                break;
            }
        }
    }

    public int getBlock_order(int i) {
        return block_order.get(i).intValue();
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public void setBlock_num(int block_num) {
        this.block_num = block_num;
    }

    public void setNear_block_num(int near_block_num) {
        this.near_block_num = near_block_num;
    }

    public void setSkill_point(int skill_point) {
        this.skill_point = skill_point;
    }

    public int getGroup() {
        return group;
    }

    public int getBlock_num() {
        return block_num;
    }

    public int getNear_block_num() {
        return near_block_num;
    }

    public int getSkill_point() {
        return skill_point;
    }
}
