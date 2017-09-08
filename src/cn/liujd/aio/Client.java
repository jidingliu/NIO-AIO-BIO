package cn.liujd.aio;

import java.util.Scanner;

public class Client {
	private static String DEFAULT_HOST = "127.0.0.1";
	private static int DEFAULT_PORT = 12345;
	private static AsyncClientHandler clientHandler;
	
	public static void start() {
		start(DEFAULT_HOST , DEFAULT_PORT);
	}

	public static synchronized void start(String ip, int port) {
		if(null != clientHandler)
			return;
		clientHandler = new AsyncClientHandler(ip,port);
		new Thread(clientHandler , "client").start();
	}
	
	//向服务器发送消息
	public static boolean sendMsg(String msg) throws Exception {
		if("q".equals(msg))
			return false;
		clientHandler.sendMsg(msg);
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		Client.start();
		System.out.println("请输入请求信息:");
		Scanner scanner = new Scanner(System.in);
		while(Client.sendMsg(scanner.nextLine()));
	}
}
