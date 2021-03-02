package com.ignacio.nettystudy.netty.im.TankMsg;

/**
 * @author ：Ignacito
 * @date ：Created on 2021/3/2 at 14:30
 */
public class TankMsg {
    public int x, y;

    public TankMsg(int x,int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "TankMsg:" +
                "x=" + x +
                ", y=" + y;
    }
}
