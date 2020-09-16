package com.gbw.httplog.server.log;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

public class GBWHttpLogServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final GBWHttpLogServerConfig config;
    public GBWHttpLogServerInitializer(SslContext sslCtx,GBWHttpLogServerConfig config)
    {
        this.sslCtx = sslCtx;
        this.config = config;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }

        p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        // p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        // p.addLast(new HttpContentCompressor());
        p.addLast(new GBWHttpLogServerHandler(config));
    }
}