package com.gbw.httplog.server.log;

import com.gbw.httplog.store.GBWHttpLogSearchResult;
import com.gbw.httplog.store.GBWHttpLogStore;
import com.gbw.httplog.store.GBWHttpLogStoreFactory;
import com.gbw.httplog.utils.Base64Utils;
import com.gbw.httplog.utils.GsonUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class GBWHttpLogServerHandler extends SimpleChannelInboundHandler<Object> {

    private final GBWHttpLogServerConfig config;
    private GBWHttpLogStore store;

    public GBWHttpLogServerHandler(GBWHttpLogServerConfig config) throws ExceptionInInitializerError{

        this.config = config;
        this.store = GBWHttpLogStoreFactory.make(config);

        if(store == null)
            throw new ExceptionInInitializerError("Connot create http log store:"+config.getStoreType());


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
            if(uri.startsWith(config.getStoreUri())){

                processStore(ctx,uri);
            }else if(uri.startsWith(config.getSearchUri())){

                processSearch(ctx,uri);
            }else if(uri.startsWith(config.getRemoveUri())){

                processRemove(ctx,uri);
            }else{

                sendResponse(ctx, GsonUtils.toJson(new GBWHttpLogProcessResult(uri, -1, "Error:Invalid store uri", "{}"), false), NOT_FOUND);
            }


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

    private void processSearch(ChannelHandlerContext ctx, String uri) {

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> params = queryStringDecoder.parameters();

        if(!params.containsKey("id")) {
            sendResponse(ctx, GsonUtils.toJson(new GBWHttpLogProcessResult("search", -1, "Error:must provide id", "{}"), false), NOT_FOUND);
            return;
        }

        String id = getValue(params,"id");

        GBWHttpLogSearchResult result = store.search(id);
        String content = GsonUtils.toJson(result,false);
        sendResponse(ctx, GsonUtils.toJson(new GBWHttpLogProcessResult("search", result.getStatus()==200?0:-1, result.getStatus()==200?"OK":"Error", content), false),
                HttpResponseStatus.valueOf(result.getStatus(),result.getMsg()));

    }

    private void processStore(ChannelHandlerContext ctx,String uri){

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> params = queryStringDecoder.parameters();

        if(!params.containsKey("id")||!params.containsKey("json")) {
            sendResponse(ctx, GsonUtils.toJson(new GBWHttpLogProcessResult("store", -1, "Error:Invalid store uri", "{}"), false), NOT_FOUND);

            return;
        }

        try {
            String id = getValue(params,"id");
            String json = Base64Utils.decode(getValue(params,"json"));
            store.store(id,json);
            sendResponse(ctx, GsonUtils.toJson(new GBWHttpLogProcessResult("store",0,"OK",String.format("{\"id:%s\"}",id)),false),OK);

        }catch (Exception e){

            sendResponse(ctx, GsonUtils.toJson(new GBWHttpLogProcessResult("store",-1,"Error:"+e.getMessage(),"{}"),false),INTERNAL_SERVER_ERROR);
        }

    }

    private void processRemove(ChannelHandlerContext ctx, String uri) {

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> params = queryStringDecoder.parameters();

        if(!params.containsKey("id")) {
            sendResponse(ctx, GsonUtils.toJson(new GBWHttpLogProcessResult("remove", -1, "Error:must provide id", "{}"), false), NOT_FOUND);
            return;
        }

        String id = getValue(params,"id");
        store.remove(id);
        sendResponse(ctx, GsonUtils.toJson(new GBWHttpLogProcessResult("remove",0,"OK",String.format("{\"id:%s\"}",id)),false),OK);
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