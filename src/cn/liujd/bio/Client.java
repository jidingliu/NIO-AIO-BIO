package cn.liujd.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ͬ������ʽI/O������ClientԴ��
 * @author liujd
 *
 */
public class Client {

	//Ĭ�϶˿ں�
	private static int DEFAULT_PORT = 12345;
	private static String DEFAULT_SERVER_IP = "127.0.0.1";
	public static void send(String expression) {
		send(DEFAULT_PORT,expression);
	}
	private static void send(int port, String expression) {
		System.out.println("�������ʽ��"+expression);
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try{
			socket = new Socket(DEFAULT_SERVER_IP , port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			out.println(expression);
			System.out.println("����ǣ�" + in.readLine());
		}catch(Exception e ) {
			e.printStackTrace();
		}finally {
			//һЩ��Ҫ��������
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
