package com.gbw.httplog.server.ldap;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.ldap.codec.api.LdapApiService;
import org.apache.directory.api.ldap.codec.api.LdapEncoder;
import org.apache.directory.api.ldap.codec.osgi.DefaultLdapCodecService;
import org.apache.directory.api.ldap.model.message.Message;

import java.nio.ByteBuffer;

public class GBWLDAPEncoder  extends MessageToByteEncoder {


    private byte[] encode2Byte(Message message) throws EncoderException {

        LdapApiService ldapCodecService = new DefaultLdapCodecService();
        LdapEncoder ldapEncoder = new LdapEncoder(ldapCodecService);

        ByteBuffer byteBuffer = ldapEncoder.encodeMessage(message);
        byte[] array = byteBuffer.array();

        return array;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {



        Message responseMessage = (Message) o;

        byte[] array = encode2Byte(responseMessage);

        byteBuf.writeBytes(array);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
