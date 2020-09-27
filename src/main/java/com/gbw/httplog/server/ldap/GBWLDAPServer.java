package com.gbw.httplog.server.ldap;

import com.gbw.httplog.utils.GsonUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GBWLDAPServer {


    private static final Logger LOGGER = LoggerFactory.getLogger(GBWLDAPServer.class);

    private static void run(GBWLDAPServerConfig config) throws Exception {

        // Configure SSL.
        SslContext sslCtx = null;
        if (config.isSSL()) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        }

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new GBWLDAPServerInitializer(sslCtx,config));

            Channel ch = b.bind(config.getIp(),config.getPort()).sync().channel();


            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();

            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {


        Options opts = new Options();
        opts.addOption("conf",true,"LDAP server config");
        opts.addOption("help", false, "Print usage");

        CommandLine cliParser = new GnuParser().parse(opts, args);
        if(cliParser.hasOption("help")||!opts.hasOption("conf")){

            new HelpFormatter().printHelp("GBWLDAPServer", opts);
            System.exit(0);
        }

        String conf = cliParser.getOptionValue("conf");

        GBWLDAPServerConfig config = GsonUtils.loadConfigFromJsonFile(conf,GBWLDAPServerConfig.class);

        run(config);

    }
}
