package com.ignacio.nettystudy.netty.demo01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author ：Ignacito
 * @date ：Created on 2021/2/25 at 15:01
 */
public class Client {
    //这是一个Main方法，是程序的入口
    public static void main(String[] args) {
        //线程池
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();//可理解为辅助启动的类

        /*try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer() )
                    .connect("localhost",8889)
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }*/

        try {
            ChannelFuture f = bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer())
                    .connect("localhost", 8889);

            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(!f.isSuccess()){//如果没成功
                        System.out.println("not connected");
                    }else{
                        System.out.println("connected");
                    }
                }
            });
            f.sync();//将异步的netty改为同步
            System.out.println("...");
            f.channel().closeFuture().sync();//阻塞住客户端，保持和Server的channel

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new ClientHandler());
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = null;

                buf = (ByteBuf)msg;
                //System.out.println(buf);
                //System.out.println(buf.refCnt());
                byte[] bytes = new byte[buf.readableBytes()];
                buf.getBytes(buf.readerIndex(),bytes);
                System.out.println(new String(bytes));

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //channel第一次连上可用，写出一个字符串 ，Direct Memory,效率高
        //Netty中，往外写的所有内容都需要转换成Bytebuf
        ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
        ctx.writeAndFlush(buf);
    }
}