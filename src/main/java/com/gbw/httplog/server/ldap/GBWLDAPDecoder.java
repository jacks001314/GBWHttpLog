package com.gbw.httplog.server.ldap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.ldap.codec.api.LdapApiService;
import org.apache.directory.api.ldap.codec.api.LdapDecoder;
import org.apache.directory.api.ldap.codec.api.LdapMessageContainer;
import org.apache.directory.api.ldap.codec.api.MessageDecorator;
import org.apache.directory.api.ldap.codec.osgi.DefaultLdapCodecService;
import org.apache.directory.api.ldap.model.message.Message;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class GBWLDAPDecoder extends ByteToMessageDecoder {


    private static final Logger LOGGER = LoggerFactory.getLogger(GBWLDAPDecoder.class);

    public static Message decode2Message(InputStream is) throws DecoderException {

        LdapApiService ldapCodecService = new DefaultLdapCodecService();
        LdapDecoder ldapDecoder = new LdapDecoder();

        LdapMessageContainer container = new LdapMessageContainer<MessageDecorator<? extends Message>>(ldapCodecService);

        ldapDecoder.decode(is, container);
        Message msg = container.getMessage().getDecorated();

        return msg;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {


        //the first reading, begin the data head
        int size = byteBuf.readableBytes();

        InputStream bis = null;
        try {
            byte[] bytes = byteBuf.hasArray()
                    ? byteBuf.array() :
                    Unpooled.buffer(size).writeBytes(byteBuf).array();
            bis = new ByteArrayInputStream(bytes);


            Message msg = decode2Message(bis);

            if (msg == null) {
                return;
            }
            list.add(msg);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.info("continue to read data more..");
            return;
        } finally {
            bis.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
