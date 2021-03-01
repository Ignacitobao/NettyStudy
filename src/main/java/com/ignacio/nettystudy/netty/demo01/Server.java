package com.ignacio.nettystudy.netty.demo01;

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
 * @date ：Created on 2021/2/26 at 21:51
 */
public class Server {//BIO 的server，测试client

    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //这是一个Main方法，是程序的入口
    public static void main(String[] args) throws Exception{
        //BIO实现的server
       /* ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress(8889));

        Socket s = ss.accept();
        System.out.println("a client is connected");*/

       //Netty实现的Server


        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//负责与客户端的链接
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);

        try {
            ServerBootstrap b = new ServerBootstrap();//辅助启动的类
            ChannelFuture f = b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //System.out.println(socketChannel);
                            //服务端接收数据
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ServerChildHandler());
                        }
                    })
                    .bind(8889)
                    .sync();

            System.out.println("server started");

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


}

class ServerChildHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.clients.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ctx.writeAndFlush(msg);
        ByteBuf buf = null;

        try {
            buf = (ByteBuf)msg;
            //System.out.println(buf);
            //System.out.println(buf.refCnt());
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(),bytes);
            System.out.println(new String(bytes));

            Server.clients.writeAndFlush(msg);

        } finally {
            if(buf != null){
                //ReferenceCountUtil.release(buf);//只读不写的话需要手动释放内存引用，如果既读又写，系统已经自动释放过一回，不需要再次释放
                //System.out.println(buf.refCnt());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}