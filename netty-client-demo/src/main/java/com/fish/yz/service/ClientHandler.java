package com.fish.yz.service;

import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import com.fish.yz.protobuf.Protocol;


public class ClientHandler extends SimpleChannelInboundHandler<Protocol.Request> {
	public int count = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Protocol.Request request) throws Exception {
        System.out.println("channelRead0 " + request);
        switch (request.getCmdId()){
            case GGMessage:
                dealGGMessage(channelHandlerContext, request);
                break;
            case EntityMessage:
                entityMessage(channelHandlerContext, request);
                break;
        }

    }

    public void dealGGMessage(ChannelHandlerContext ctx, Protocol.Request request){
        System.out.println("dealGGMessage " + request);
    }

    public void entityMessage(ChannelHandlerContext ctx, Protocol.Request request){
        System.out.println("entityMessage " + request);

        if (count != 0)
        	return;
        count += 1;

        Protocol.EntityMessage.Builder emb = Protocol.EntityMessage.newBuilder();
        emb.setId(request.getExtension(Protocol.EntityMessage.request).getId());
        emb.setMethod(ByteString.copyFromUtf8("login"));
        emb.setParameters(ByteString.copyFromUtf8("{'account': 'fish', 'password': '123456'}"));

        Protocol.Request.Builder rb = Protocol.Request.newBuilder();
        rb.setCmdId(Protocol.Request.CmdIdType.EntityMessage);
        rb.setExtension(Protocol.EntityMessage.request, emb.build());
        ctx.channel().writeAndFlush(rb.build());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("exceptionCaught in client service " + cause.getMessage());
	}
}