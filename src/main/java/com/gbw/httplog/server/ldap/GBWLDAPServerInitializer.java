package com.gbw.httplog.server.ldap;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public class GBWLDAPServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final GBWLDAPServerConfig config;
    public GBWLDAPServerInitializer(SslContext sslCtx, GBWLDAPServerConfig config)
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

        p.addLast(new GBWLDAPDecoder());
        p.addLast(new GBWLDAPEncoder());

        // Remove the following line if you don't want automatic content compression.
        // p.addLast(new HttpContentCompressor());
        p.addLast(new GBWLDAPServerHandler(config));
    }
}