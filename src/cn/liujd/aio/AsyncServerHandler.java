package cn.liujd.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncServerHandler implements Runnable{

	//一个同步辅助类 在完成一组正在其他线程中执行的操作之前  它允许一个或者多个线程一直等待
	public CountDownLatch latch;
	//An asynchronous channel for stream-oriented listening sockets.
	public AsynchronousServerSocketChannel channel;
	
	public AsyncServerHandler(int port) {
		try {
			//创建服务器通道
			channel = AsynchronousServerSocketChannel.open();
			//绑定端口
			channel.bind(new InetSocketAddress(port));
			System.out.println("服务器以启动，端口号：" + port);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		//CountDownLatch初始化
		//它的作用:在完成一组正在执行的操作之前 允许当前的现场一直阻塞
		//此处 让现场在此阻塞  防止服务端执行完成后退出
		//也可以使用while(true) + sleep
		//生成环境就不需要担心这个问题  以为服务端是不会退出的
		latch = new CountDownLatch(1);
		//用于接收客户端连接
		channel.accept(this,new AcceptHandler());
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
