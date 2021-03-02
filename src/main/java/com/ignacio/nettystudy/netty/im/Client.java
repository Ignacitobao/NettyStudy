package com.ignacio.nettystudy.netty.im;

import com.ignacio.nettystudy.netty.im.TankMsg.TankMsg;
import com.ignacio.nettystudy.netty.im.TankMsg.TankMsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * @author ：Ignacito
 * @date ：Created on 2021/3/1 at 17:38
 */
public class Client {
    private Channel channel = null;



    public void connect(){
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            ChannelFuture f = b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer())
                    .connect("localhost", 8889);

            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(!f.isSuccess()){
                        System.out.println("not connected");
                    }else{
                        System.out.println("connected");
                        channel = f.channel();
                    }
                }
            });

            f.sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    //这是一个Main方法，是程序的入口
    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
    }

    //发送信息
    public void send(String msg){
        ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
        channel.writeAndFlush(buf);
    }

    //通知服务器client准备关闭
    public void closeConnect(){
        this.send("_bye_");
    }
}



class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new TankMsgEncoder())
                .addLast(new ClientHandler());

    }

}
class ClientHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buf = Unpooled.copiedBuffer("Hello".getBytes());
        ctx.writeAndFlush(new TankMsg(5,8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;

        try {
            buf = (ByteBuf)msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(),bytes);
            String msgAccepted = new String(bytes);
            ClientFrame.INSTANCE.updateText(msgAccepted);

            /*System.out.println(new String(bytes));
            ctx.writeAndFlush(buf);*/
        } finally {
            if(buf != null){
                ReferenceCountUtil.release(buf);
            }
        }
    }
}