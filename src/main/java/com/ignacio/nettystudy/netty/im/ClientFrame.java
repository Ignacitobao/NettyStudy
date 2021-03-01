package com.ignacio.nettystudy.netty.im;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author ：Ignacito
 * @date ：Created on 2021/3/1 at 16:57
 */
public class ClientFrame extends Frame {

    TextArea ta = new TextArea();
    TextField tf = new TextField();

    public ClientFrame(){
        this.setSize(800,600);
        this.setLocation(100,20);
        this.add(ta,BorderLayout.CENTER);
        this.add(tf,BorderLayout.SOUTH);
        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //把字符串发送到服务器
                ta.setText(ta.getText()+tf.getText());
                tf.setText("");
            }
        });
        this.setVisible(true);
    }

    //这是一个Main方法，是程序的入口
    public static void main(String[] args) {
        new ClientFrame();
    }
}
