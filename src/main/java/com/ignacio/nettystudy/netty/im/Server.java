package com.ignacio.nettystudy.netty.im;

import com.ignacio.nettystudy.netty.im.TankMsg.TankMsg;
import com.ignacio.nettystudy.netty.im.TankMsg.TankMsgDecoder;
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


    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//用于“迎客”的线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);//用于"招待客人"的线程池

        ServerBootstrap b = new ServerBootstrap();//辅助启动类
        try {
            ChannelFuture f = b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline
                                    .addLast(new TankMsgDecoder())
                                    .addLast(new IMServerChildHandler());
                        }
                    })
                    .bind(8889)
                    .sync();

            ServerFrame.INSTANCE.updateServerMsg("server started");
            f.channel()
                    .closeFuture()
                    .sync();

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
            TankMsg tankMsg = (TankMsg)msg;
            System.out.println(tankMsg);

            //buf = (ByteBuf)msg;
            /*byte[] bytes = new byte[buf.readableBytes()];

            buf.getBytes(buf.readerIndex(), bytes);
            String str = new String(bytes);
            if(str.equals("_bye_")){
                ServerFrame.INSTANCE.updateServerMsg("Client requests to quit");
                Server.clients.remove(ctx.channel());
                ctx.close();
            }
            ServerFrame.INSTANCE.updateClientMsg(str);
            Server.clients.writeAndFlush(buf);*/

        } finally {
            if(buf != null){
                //ReferenceCountUtil.release(buf);
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Server.clients.remove(ctx.channel());//在客户端出现异常的时候，将其从channelgroup中移除
        ctx.close();
    }
}
