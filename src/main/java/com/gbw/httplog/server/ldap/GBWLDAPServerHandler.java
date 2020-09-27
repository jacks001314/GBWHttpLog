package com.gbw.httplog.server.ldap;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.directory.api.ldap.model.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GBWLDAPServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GBWLDAPServerHandler.class);
    private final GBWLDAPServerConfig config;

    public GBWLDAPServerHandler(GBWLDAPServerConfig config) {
        this.config = config;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        LOGGER.info("enter the LDAPBindHandler.....");

        String channelId = ctx.channel().id().asLongText();
        LOGGER.info(channelId);

        Request request = (Request) msg;

        if (request.getType() != MessageTypeEnum.BIND_REQUEST) {

            //need to control the session validation

            throw new Exception("......");
            //call the next handler
 /*           ctx.fireChannelRead(msg);
            return;*/
        }

        //bind data , create the ldap session
        BindRequest bindRequest = (BindRequest) request;
        LdapResult result = bindRequest.getResultResponse().getLdapResult();

        System.out.println(bindRequest);
        System.out.println(bindRequest.getName());
        //another business logical process
        result.setResultCode(ResultCodeEnum.SUCCESS);
        result.setDiagnosticMessage("OK");
        result.setMatchedDn(bindRequest.getDn());

        ctx.channel().writeAndFlush(bindRequest.getResultResponse());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("catch the error...................");
//        cause.printStackTrace();
        ctx.close();
    }


}
