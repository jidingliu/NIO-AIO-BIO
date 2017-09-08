package cn.liujd.nio;

import java.util.Scanner;

public class Client {
	private static String DEFAULT_HOST = "127.0.0.1";
	private static int DEFAULT_PORT = 12345;
	private static ClientHandler clientHandler;
	@SuppressWarnings("resource")
	public static void start() {
		start(DEFAULT_HOST,DEFAULT_PORT);
		try {
			while(Client.sendMsg(new Scanner(System.in).nextLine()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static synchronized void start(String ip, int port) {
		if(null != clientHandler) 
			clientHandler.stop();
		clientHandler = new ClientHandler(ip, port);
		new Thread(clientHandler,"Client").start();
	}
	
	//向服务器发送消息
	public static boolean sendMsg(String msg) throws Exception {
		if("q".equals(msg))
			return false;
		clientHandler.sendMsg(msg);
		return true;
	}
	public static void main(String[] args) {
		start();
	}
}
