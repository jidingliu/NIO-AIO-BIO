package cn.liujd.nio;

/**
 * ����NIO����˵���Ҫ�������£�
 * ��ServerSocketChannel�������ͻ�������
 * �󶨼����˿ڣ���������Ϊ������ģʽ
 * ����Reactor�߳� ������·���������������߳�
 * ��ServerSocketChannelע�ᵽReactor�߳��е�Selector�� ����ACCEPT�¼�
 * Selector��ѯ׼��������key
 * Selector�������µĿͻ��˽��� �����µĽ������� ���TCP��������  ����������·
 * ���ÿͻ�����·Ϊ������ģʽ
 * ���½���Ŀͻ�������ע�ᵽReactor�̵߳�Selector�� ����������   ��ȡ�ͻ��˷��͵�������Ϣ
 * �첽��ȡ�ͻ�����Ϣ��������
 * ��Buffer����� ��������Ϣ ������ɹ�����Ϣ��װ��Task
 * ��Ӧ����Ϣ����ΪBuffer ����SocketChannel��write����Ϣ�첽���͸��ͻ���
 */

public class Server {

	private static int DEAFAULT_PORT = 12345;
	
	private static ServerHandler serverHandler;
	
	public static void start() {
		start(DEAFAULT_PORT);
	}

	public static synchronized void start(int port) {
		if(null != serverHandler)
			serverHandler.stop();
		serverHandler = new ServerHandler(port);
		new Thread(serverHandler,"Server").start();
	}
	
	public static void main(String[] args) {
		start();
	}
	
}
