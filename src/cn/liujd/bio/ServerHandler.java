package cn.liujd.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import cn.liujd.Calculator;

/**
 * �ͻ�����Ϣ�����߳�ServerHandlerԴ��
 * �ͻ����߳�
 * ���ڴ���һ���ͻ��˵�Socket��·
 * @author liujd
 *
 */
public class ServerHandler implements Runnable {

	private Socket socket;
	
	public ServerHandler(Socket socket) {
		this.socket = socket;
	}
	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try{
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(),true);
		String expression;
		String result;
		while(true) {
			//ͨ��BufferedReader��ȡһ��
			//����Ѿ���ȡ��������β�� ����null �˳�ѭ��
			//����õ��ǿ�ֵ���ó�ʱ������������
			if(null == (expression = in.readLine()))
				break;
			System.out.println("�������յ���Ϣ" + expression);
			try {
				result = Calculator.cal(expression).toString();
			}catch(Exception e) {
				result = "�������" + e.getMessage();
			}
			out.println(result);
		}
 	}catch(Exception e) {
 		e.printStackTrace();
 	}finally{
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
 			socket = null;
 		}
 	}
	}

}
