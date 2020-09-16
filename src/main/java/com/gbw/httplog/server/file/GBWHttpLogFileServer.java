package com.gbw.httplog.server.file;

import com.gbw.httplog.utils.GsonUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GBWHttpLogFileServer {

    private static final Logger log = LoggerFactory.getLogger(GBWHttpLogFileServer.class);

    private static void run(GBWHttpLogFileServerConfig config) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            // Added http decoder
                            .addLast("http-decoder", new HttpRequestDecoder())
                            // If the ObjectAggregator decoder is used to convert multiple messages into a single FullHttpRequest or FullHttpResponse
                            .addLast("http-aggregator", new HttpObjectAggregator(65536))
                            // Add http decoder
                            .addLast("http-encoder", new HttpResponseEncoder())
                            // Added chunked to support asynchronously sent streams (large file transfers), but not take up too much memory and prevent jdk memory overflow
                            .addLast("http-chunked", new ChunkedWriteHandler())
                            // Add custom business server handlers
                            .addLast("fileServerHandler", new GBWHttpLogFileServerHandler(config.getUri(),config.getDownloadDir()));
                }
            });
            ChannelFuture future = b.bind(config.getIp(), config.getPort()).sync();
            System.out.printf("HTTP file directory server starts： http://%s:%d%s\n", config.getIp(),config.getPort() ,config.getUri());
            log.info(String.format("HTTP file directory server starts： http://%s:%d%s\n", config.getIp(),config.getPort() ,config.getUri()));
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {

        Options opts = new Options();
        opts.addOption("conf",true,"http file server config");
        opts.addOption("help", false, "Print usage");

        CommandLine cliParser = new GnuParser().parse(opts, args);
        if(cliParser.hasOption("help")||!opts.hasOption("conf")){

            new HelpFormatter().printHelp("GBWHttpLogFileServer", opts);
            System.exit(0);
        }

        String conf = cliParser.getOptionValue("conf");

        GBWHttpLogFileServerConfig config = GsonUtils.loadConfigFromJsonFile(conf,GBWHttpLogFileServerConfig.class);

        run(config);

    }

}
