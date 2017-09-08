package cn.liujd.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 同步阻塞式I/O创建的Client源码
 * @author liujd
 *
 */
public class Client {

	//默认端口号
	private static int DEFAULT_PORT = 12345;
	private static String DEFAULT_SERVER_IP = "127.0.0.1";
	public static void send(String expression) {
		send(DEFAULT_PORT,expression);
	}
	private static void send(int port, String expression) {
		System.out.println("算数表达式："+expression);
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try{
			socket = new Socket(DEFAULT_SERVER_IP , port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			out.println(expression);
			System.out.println("结果是：" + in.readLine());
		}catch(Exception e ) {
			e.printStackTrace();
		}finally {
			//一些必要的清理工作
			if(null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
			if(null != out) {
				out.close();
				out = null;
			}
			if(null != socket) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
