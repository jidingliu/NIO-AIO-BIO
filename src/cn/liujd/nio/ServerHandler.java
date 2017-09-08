package cn.liujd.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import javax.script.ScriptException;

import cn.liujd.Calculator;

/**
 * NIO服务端处理线程
 * @author liujd
 *
 */
public class ServerHandler implements Runnable{

	private Selector selector;
	private ServerSocketChannel serverChannel;
	/**
	 * Volatile修饰的成员变量在每次被线程访问时，都强迫从共享内存中重读该成员变量的值。
	 * 而且，当成员变量发生变化时，强迫线程将变化值回写到共享内存。
	 * 这样在任何时刻，两个不同的线程总是看到某个成员变量的同一个值。 
	 */
	private volatile boolean started;
	
	//构造方法  指定要监听的端口号
	public ServerHandler(int port) {
		try {
			//创建选择器
			selector = Selector.open();
			//打开服务器套接字通道
			serverChannel = ServerSocketChannel.open();
			//如果为true 则此通道将被置于阻塞模式  如果为false 则此通道将被置于非阻塞模式
			serverChannel.configureBlocking(false);//开启非阻塞模式
			
			/**
			 * InetSocketAddress是SocketAddress的实现子类。
			 *	此类实现 IP 套接字地址（IP 地址 + 端口号），不依赖任何协议。
			 *	在使用Socket来连接服务器时最简单的方式就是直接使用IP和端口，但Socket类中的connect方法并未提供这种方式，而是使用SocketAddress类来向connect方法传递服务器的IP和端口。
			 *	SocketAddress只是个抽象类，它除了有一个默认的构造方法外，其它的方法都是abstract的，因此，我们必须使用SocketAddress的子类来建立SocketAddress对象，也就是唯一的子类InetSocketAddress
			 */
			//获取与此通道关联的服务器套接字
			ServerSocket serverSocket = serverChannel.socket();
			//bind方法  第一个参数： 绑定IP或者端口 第二个参数： backlog设为1024 表示侦听长度
			serverSocket.bind(new InetSocketAddress(port),1024);
			//监听客户端连接请求
			//将ServerSocketChannel注册到Reactor线程中的Selector上 监听ACCEPT事件
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			//SelectionKey.OP_ACCEPT用于套接字接收操作的操作集位
			//标记服务器已经开启
			started = true;
			System.out.println("服务器已启动，端口号：" + port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void stop() {
		started = false;
	}
	@Override
	public void run() {
		//循环遍历selector
		while(started) {
			try {
				//无论是否有读写事件发生  selector每隔一秒被唤醒一次
				selector.select(1000);
				//阻塞 只有当至少一个注册的时间发生的时候才会继续
				//selector.select();
				
				//返回这个选择器的已选择键集
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				SelectionKey key = null;
				while(it.hasNext()) {
					key = it.next();
					it.remove();
					try{
					handlerInput(key);
					}catch(Exception e) {
						if(null != key) {
							//请求取消此键的通道到其选择器的注册
							key.cancel();
							if(null != key.channel()) {
								//channel()方法 返回为之创建此键的通道
								//关闭通道
								key.channel().close();
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//selector关闭后会自动释放里面管理的资源
		if(null != selector) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void handlerInput(SelectionKey key) throws IOException{
		//判断此键是否有效
		if(key.isValid()) {
			//isAcceptable()方法  测试此键的通道是否已准备好接收新的套接字连接
			//处理新接入的请求消息
			if(key.isAcceptable()) {
				//channel()方法返回为之创建此键的通道。
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				//通过ServerSocketChannel的accept创建socketChannel实例
				//完成该操作意味着完成TCP三次握手  TCP物理链路正式建立
				SocketChannel sc = ssc.accept();
				//设置为非阻塞
				sc.configureBlocking(false);
				//注册为读
				sc.register(selector, SelectionKey.OP_READ);
			}
			//isReadable()方法测试此键的通道是否已经准备好进行读取
			//读消息
			if(key.isReadable()) {
				SocketChannel sc = (SocketChannel)key.channel();
				//创建ByteBuffer  并且开辟一个1M的缓冲区
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				//读取请求码流  返回读取到的字节数
				int readBytes = sc.read(buffer);
				//读取到字节 对字节进行编解码
				if(readBytes > 0) {
					//将缓冲区当前的limit设置为position=0 用于后续对缓冲区的读取操作（反转此缓冲区）
					buffer.flip();
					//根据缓冲区可读字节数创建字节数组（remaining()返回当前位置与限制之间的元素数）
					byte[] bytes = new byte[buffer.remaining()];
					//将缓冲区可读字节数组复制到新建的数组中
					buffer.get(bytes);
					String expression = new String(bytes,"UTF-8");
					System.out.println("服务端收到信息：" + expression);
					//处理数据
					String result = null;
					
					try {
						result = Calculator.cal(expression).toString();
						System.out.println("服务端计算结果为" + result);
					} catch (ScriptException e) {
						result = "计算错误：" + e.getMessage();
					}
					//收到应答消息
					doWrite(sc , result);
				}
				//没有读取到字节 省略
				//else if (readBytes == 0);
				
				//链路已经关闭  释放资源
				else if(readBytes < 0) {
					key.cancel();
					sc.close();
				}
			}
		}
	}
	//异步发送应答消息
	private void doWrite(SocketChannel channel, String response) throws IOException{
		//将消息编码为字节数组
		byte[] bytes = response.getBytes();
		//根据数组容量创建ByteBuffer
		ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
		//将字节数组复制到缓冲区
		writeBuffer.put(bytes);
		//flip操作
		writeBuffer.flip();
		//发送缓冲区的字节数组
		channel.write(writeBuffer);
		
	}

}
