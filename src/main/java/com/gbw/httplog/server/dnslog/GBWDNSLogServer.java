package com.gbw.httplog.server.dnslog;

import com.gbw.httplog.utils.GsonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.DatagramDnsQueryDecoder;
import io.netty.handler.codec.dns.DatagramDnsResponseEncoder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class GBWDNSLogServer {

    private static void run(GBWDNSLogConfig config) {

        final NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel nioDatagramChannel) throws Exception {
                            nioDatagramChannel.pipeline().addLast(new DatagramDnsQueryDecoder());
                            nioDatagramChannel.pipeline().addLast(new DatagramDnsResponseEncoder());
                            nioDatagramChannel.pipeline().addLast(new GBWDNSLogServerHandler(config));
                        }
                    }).option(ChannelOption.SO_BROADCAST, true);

            ChannelFuture future = bootstrap.bind("0.0.0.0", 53).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{

        Options opts = new Options();
        opts.addOption("conf",true,"dnslog server config");
        opts.addOption("help", false, "Print usage");

        CommandLine cliParser = new GnuParser().parse(opts, args);
        if(cliParser.hasOption("help")||!cliParser.hasOption("conf")){

            new HelpFormatter().printHelp("GBWDNSLogServer", opts);
            System.exit(0);
        }

        String conf = cliParser.getOptionValue("conf");

        GBWDNSLogConfig config = GsonUtils.loadConfigFromJsonFile(conf,GBWDNSLogConfig.class);

        run(config);
    }
}
