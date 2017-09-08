package cn.liujd.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

//作为handler接收客户端连接
//CompletionHandler接口  用于消耗异步I/O操作结果的处理器  泛型第一个参数代表I/O操作的结果类型   第二个参数代表附加到I/O操作的对象类型
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,AsyncServerHandler> {

	//AsynchronousSocketChannel An asynchronous channel for stream-oriented connecting sockets.
	@Override
	public void completed(AsynchronousSocketChannel channel, AsyncServerHandler serverHandler) {
		//继续接收其他客户端的请求
		Server.clientCount ++ ;
		System.out.println("连接的客户端数量：" + Server.clientCount);
		serverHandler.channel.accept(serverHandler , this);
		//创建新的Buffer
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		//异步读 第三个参数为接收消息回调的业务Handler
		channel.read(buffer, buffer, new ReadHandler(channel));
	}

	@Override
	public void failed(Throwable exc, AsyncServerHandler serverHandler) {
		exc.printStackTrace();
		serverHandler.latch.countDown();
	}
}
