package com.yunchuan.bilibili.client.netty.asyncable;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;

public class HttpClientHandler extends ChannelInboundHandlerAdapter {
    private ByteBufToBytes reader;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            System.out.println("CONTENT_TYPE:"
                    + response.headers().get(HttpHeaderNames.CONTENT_TYPE));
            if (HttpUtil.isContentLengthSet(response)) {
                reader = new ByteBufToBytes(
                        (int) HttpUtil.getContentLength(response));
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();
            reader.reading(content);
            content.release();
            if (reader.isEnd()) {
                String resultStr = new String(reader.readFull());
                System.out.println("Server said:" + resultStr);
                ctx.close();
            }
        }
    }
}
