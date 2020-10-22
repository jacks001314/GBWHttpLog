package com.gbw.httplog.server.dnslog;

import com.gbw.httplog.store.GBWHttpLogStoreException;
import com.gbw.httplog.store.redis.GBWHttpLogStoreRedis;
import com.gbw.httplog.utils.GsonUtils;
import com.gbw.httplog.utils.TextUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GBWDNSLogServerHandler extends SimpleChannelInboundHandler<DatagramDnsQuery> {

    private static final Logger log = LoggerFactory.getLogger(GBWDNSLogServerHandler.class);

    private GBWDNSLogConfig config;
    private GBWHttpLogStoreRedis storeRedis;

    public GBWDNSLogServerHandler(GBWDNSLogConfig config) throws GBWHttpLogStoreException {

        this.config = config;
        this.storeRedis = new GBWHttpLogStoreRedis();
        this.storeRedis.open(config.getRedisConfig());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramDnsQuery query) throws Exception {

        DefaultDnsQuestion dnsQuestion = query.recordAt(DnsSection.QUESTION);

        if(TextUtils.isEmpty(dnsQuestion.name()))
            return;

        String srcIP = query.sender().getAddress().toString().split("/")[1];
        String domain = getDomain(dnsQuestion.name());

        if(domain.endsWith(config.getDomain())){

            String resIP = getResponseIP(domain);

            log.info(String.format("Receive a dns question,srcIP:%s,domain:%s,responseIP:%s",srcIP,domain,resIP));

            GBWDNSLogEntry entry = new GBWDNSLogEntry(srcIP,resIP,System.currentTimeMillis());
            storeRedis.store(domain, GsonUtils.toJson(entry,false));

            DatagramDnsResponse response = new DatagramDnsResponse(query.recipient(), query.sender(), query.id());

            response.addRecord(DnsSection.QUESTION, dnsQuestion);
            DefaultDnsRawRecord queryAnswer = new DefaultDnsRawRecord(dnsQuestion.name(), DnsRecordType.A, 0,getAnserIP(resIP));
            response.addRecord(DnsSection.ANSWER, queryAnswer);
            ctx.writeAndFlush(response);
        }

    }

    private ByteBuf getAnserIP(String ip) {
        List<Byte> byteIP = Arrays.asList(ip.split("\\.")).stream().map(x -> (byte) Integer.parseInt(x)).collect(Collectors.toList());
        return Unpooled.wrappedBuffer(new byte[]{byteIP.get(0), byteIP.get(1), byteIP.get(2), byteIP.get(3)});
    }

    private String getDomain(String name){

        return name.endsWith(".")?name.substring(0,name.length()-1):name;
    }

    private String getResponseIP(String domain){

        String ip = config.getBindMap().get(domain);

        if(TextUtils.isEmpty(ip))
            ip = config.getDefaultResponseIP();

        return ip;
    }

}
