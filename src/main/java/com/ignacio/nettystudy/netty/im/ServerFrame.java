package com.ignacio.nettystudy.netty.im;

import sun.applet.resources.MsgAppletViewer;

import java.awt.*;
import java.awt.event.*;

/**
 * @author ：Ignacito
 * @date ：Created on 2021/3/2 at 13:44
 */
public class ServerFrame extends Frame {
    public static final ServerFrame INSTANCE = new ServerFrame();
    TextArea taLeft = new TextArea();
    TextArea taRight = new TextArea();
    Server server = new Server();

    private ServerFrame() {
        this.setSize(1080, 720);
        this.setLocation(100, 100);
        Panel panel = new Panel(new GridLayout(1,2));
        panel.add(taLeft);
        panel.add(taRight);
        this.add(panel);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

    }

    public void startServer(){
        server.start();
    }

    public void updateServerMsg(String msg) {
        this.taLeft.setText(taLeft.getText() + System.getProperty("line.separator") + msg);
    }



    //这是一个Main方法，是程序的入口
    public static void main(String[] args) {
        ServerFrame serverFrame = ServerFrame.INSTANCE;
        serverFrame.setVisible(true);
        serverFrame.startServer();
    }

    public void updateClientMsg(String str) {
        this.taRight.setText(taRight.getText() + System.getProperty("line.separator") + str);
    }
}
