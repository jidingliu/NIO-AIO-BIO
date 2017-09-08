package cn.liujd.aio;

/**
 * AIO·þÎñ¶Ë
 * @author liujd
 *
 */
public class Server {
	private static int DEFAULT_PORT = 12345;
	private static AsyncServerHandler serverHandler;
	public volatile static long clientCount = 0;
	
	public static void start() {
		start(DEFAULT_PORT);
	}

	private static synchronized void start(int port) {
		if(null != serverHandler) {
			return;
		}
		serverHandler = new AsyncServerHandler(port);
		new Thread(serverHandler , "Server").start();
	}
	
	public static void main(String[] args) {
		//Server.start();
		
	}
	
	public void test() throws Exception{
		throw new Exception("eee","message");
	}
}
