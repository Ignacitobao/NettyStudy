package com.ignacio.nettystudy.netty.im.TankMsg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author ：Ignacito
 * @date ：Created on 2021/3/2 at 14:41
 */
public class TankMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < 8){
            return;//解决了TCP的拆包和粘包的问题
        }

        int x = byteBuf.readInt();
        int y = byteBuf.readInt();

        list.add(new TankMsg(x,y));
    }
}
