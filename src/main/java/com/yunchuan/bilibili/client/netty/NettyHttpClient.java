package com.yunchuan.bilibili.client.netty;






//@Configuration
public class NettyHttpClient {

//    @Bean
//    public ChannelFuture ChannelFactory() throws InterruptedException {
//        NioEventLoopGroup loopGroup = new NioEventLoopGroup(1);
//        Bootstrap bootstrap = new Bootstrap();
//        bootstrap.group(loopGroup)
//                .channel(NioSocketChannel.class)
//
//                .handler(new ChannelInitializer<SocketChannel>() {
//            @Override
//            protected void initChannel(SocketChannel channel) throws Exception {
//                //包含编码器和解码器
//                channel.pipeline().addLast(new HttpClientCodec());
//
//                //聚合
//                channel.pipeline().addLast(new HttpObjectAggregator(1024 * 10 * 1024));
//
//                //解压
//                channel.pipeline().addLast(new HttpContentDecompressor());
//
//                //自定义handler
//                channel.pipeline().addLast(new MyHttpClientHandler());
//            }
//        });
//        // 长连接
//        ChannelFuture channel = bootstrap.connect(new InetSocketAddress("api.bilibili.com", 80)).sync();
//
//        return channel;
//    }



}
