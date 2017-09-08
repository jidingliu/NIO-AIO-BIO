package cn.liujd.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BIO�����Դ�� α�첽I/O
 * @author liujd
 *
 */
public class ServerBetter {
	//Ĭ�ϵĶ˿ں�
	private static int DEFAULT_PORT = 12345;
	//������ServerSocket
	private static ServerSocket server;
	//�̳߳�  ����ʽ�ĵ���
	private static ExecutorService executorService = Executors.newFixedThreadPool(60);
	//���ݴ���������ü����˿ڣ����û�в�������һ�·�������ʹ��Ĭ��ֵ
	public static void start() throws IOException {
		//ʹ��Ĭ��ֵ
		start(DEFAULT_PORT);
	}
	//����������ᱻ������������ ��̫��Ҫ����Ч�� ֱ�ӽ��з���ͬ��������
	private static void start(int port) throws IOException{
		if(null != server) 
			return;
		try {
			//ͨ�����캯������ServerSocket
			//����˿ںϷ��ҿ��� ����˾ͼ����ɹ�
			server = new ServerSocket(port);
			System.out.println("���������������˿ںţ�" + port);
			Socket socket;
			//ͨ������ѭ�������ͻ�������
			//���û�пͻ��˽��룬��������accept������
			while(true) {
				socket = server.accept();
				//�����µĿͻ��˽���ʱ ��ִ���������
				//Ȼ�󴴽�һ���µ��̴߳�����һ��Socket��·
				executorService.execute(new ServerHandler(socket));
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
