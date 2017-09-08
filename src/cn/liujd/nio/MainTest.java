package cn.liujd.nio;

import java.util.Scanner;

//测试方法
public class MainTest {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		//运行服务器
		Server.start();
		//避免客户端优先服务端启动前执行代码
		Thread.sleep(100);
		//运行客户端
		Client.start();
		while(Client.sendMsg(new Scanner(System.in).nextLine()));
	}
}
