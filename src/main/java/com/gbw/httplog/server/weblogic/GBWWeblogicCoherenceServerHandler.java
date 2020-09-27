package com.gbw.httplog.server.weblogic;

import com.gbw.httplog.utils.Base64Utils;
import com.gbw.httplog.utils.GsonUtils;
import com.gbw.httplog.utils.TextUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class GBWWeblogicCoherenceServerHandler extends SimpleChannelInboundHandler<Object> {

    private final static Logger log = LoggerFactory.getLogger(GBWWeblogicCoherenceServerHandler.class);

    private final GBWWeblogicCoherenceServerConfig config;
    public GBWWeblogicCoherenceServerHandler(GBWWeblogicCoherenceServerConfig config) throws ExceptionInInitializerError{

        this.config = config;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof HttpRequest) {

            HttpRequest request = (HttpRequest) msg;

            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            String uri = request.uri();
            log.info("Coherence server request uri:"+uri);
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
            Map<String, List<String>> params = queryStringDecoder.parameters();


            try {
                RequestEntry entry = GsonUtils.loadConfigFromJson(Base64Utils.decode(getValue(params,"content")),RequestEntry.class);

                String version = entry.getVersion();
                String cmd = entry.getCmd();

                if(TextUtils.isEmpty(version)||TextUtils.isEmpty(cmd))
                {
                    sendResponse(ctx,"Invalid args:"+uri,INTERNAL_SERVER_ERROR);
                    return;
                }
                GBWWeblogicCoherencePayloadEntry payloadEntry = GBWWeblogicCoherencePayload.makePayload(version,config.getPayloadDir(),cmd);
                sendResponse(ctx,GsonUtils.toJson(payloadEntry,false),OK);

            } catch (Exception e) {
                sendResponse(ctx,"Error:"+e.getMessage(),INTERNAL_SERVER_ERROR);
            }

        }


    }

    private class RequestEntry{

        private String version;
        private String cmd;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }
    }

    private String getValue(Map<String,List<String>> params,String key){

        if(params == null)
            return "";

        List<String> values = params.get(key);
        if(values == null||values.size()==0)
            return "";

        return values.get(0);
    }

    private static void sendResponse(ChannelHandlerContext ctx,String content,HttpResponseStatus status){

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,status, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER);
        ctx.write(response);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        cause.printStackTrace();
        ctx.close();
    }

}