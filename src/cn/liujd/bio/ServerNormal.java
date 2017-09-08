package cn.liujd.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ͬ������ʽI/O������ServerԴ��
 * BIO�����Դ��
 * @author liujd
 *
 */
public final class ServerNormal {
	//Ĭ�ϵĶ˿ں�
	private static int DEAFAULT_PORT = 12345;
	//������ServerSocket
	private static ServerSocket server;
	//���ݴ���Ĳ������ü����˿� ���û�в����������·�����ʹ��Ĭ��ֵ
	public static void start() throws IOException {
		//ʹ��Ĭ��ֵ
		start(DEAFAULT_PORT);
	}
	//����������ᱻ������������ ��̫��Ҫ����Ч�� ֱ�ӽ��з���ͬ��������
	public synchronized static void start(int port) throws IOException {
		if(null != server) 
			return;
		try{
			//ͨ�����캯������ServerSocket
			//����˿ںϷ��ҿ��� ����˾ͼ����ɹ�
			server = new ServerSocket(port);
			System.out.println("������������,�˿ںţ�" + port);
			Socket socket;
			//ͨ������ѭ�������ͻ�������
			//���û�пͻ��˽��룬��������accept������
			while(true) {
				socket = server.accept();
				//�����µĿͻ��˽���ʱ����ִ���������
				//Ȼ�󴴽�һ���µ��̴߳�������Socket��·
				new Thread(new ServerHandler(socket)).start();
			}
		}finally {
			//һЩ��Ҫ��������
			if(null != server) {
				System.out.println("�������ѹر�");
				server.close();
				server = null;
			}
		}
	}
}
