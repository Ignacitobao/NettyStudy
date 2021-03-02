package com.ignacio.nettystudy.netty.im;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import io.netty.channel.nio.NioEventLoopGroup;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;



/**
 * @author ：Ignacito
 * @date ：Created on 2021/3/1 at 17:39
 */
public class Server {
    //装client的容器
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //这是一个Main方法，是程序的入口
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//用于“迎客”的线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);//用于"招待客人"的线程池

        ServerBootstrap b = new ServerBootstrap();//辅助启动类
        try {
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IMServerChildHandler());
                        }
                    })
                    .bind(8889)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
            //System.out.println("Server started");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}

class IMServerChildHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.clients.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //把从客户端接收到的Msg转化成 bytebuf类型，再转化成string
        ByteBuf buf = null;

        try {
            buf = (ByteBuf)msg;
            byte[] bytes = new byte[buf.readableBytes()];

            buf.getBytes(buf.readerIndex(), bytes);
            System.out.println(buf);
            System.out.println(bytes);
            System.out.println(new String(bytes));

            Server.clients.writeAndFlush(buf);

        } finally {
            if(buf != null){
                //ReferenceCountUtil.release(buf);
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
