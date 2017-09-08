package cn.liujd.nio;

/**
 * 创建NIO服务端的主要步骤如下：
 * 打开ServerSocketChannel，监听客户端连接
 * 绑定监听端口，设置连接为非阻塞模式
 * 创建Reactor线程 创建多路复用器并且启动线程
 * 将ServerSocketChannel注册到Reactor线程中的Selector上 监听ACCEPT事件
 * Selector轮询准备就绪的key
 * Selector监听到新的客户端接入 处理新的接入请求 完成TCP三次握手  建立物理链路
 * 设置客户端链路为非阻塞模式
 * 将新接入的客户端连接注册到Reactor线程的Selector上 监听读操作   读取客户端发送的网络信息
 * 异步读取客户端消息到缓冲区
 * 对Buffer编解码 处理半包消息 将解码成功的消息封装成Task
 * 将应答消息编码为Buffer 调用SocketChannel的write将消息异步发送给客户端
 */

public class Server {

	private static int DEAFAULT_PORT = 12345;
	
	private static ServerHandler serverHandler;
	
	public static void start() {
		start(DEAFAULT_PORT);
	}

	public static synchronized void start(int port) {
		if(null != serverHandler)
			serverHandler.stop();
		serverHandler = new ServerHandler(port);
		new Thread(serverHandler,"Server").start();
	}
	
	public static void main(String[] args) {
		start();
	}
	
}
