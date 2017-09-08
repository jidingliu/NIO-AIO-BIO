package cn.liujd.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncServerHandler implements Runnable{

	//һ��ͬ�������� �����һ�����������߳���ִ�еĲ���֮ǰ  ������һ�����߶���߳�һֱ�ȴ�
	public CountDownLatch latch;
	//An asynchronous channel for stream-oriented listening sockets.
	public AsynchronousServerSocketChannel channel;
	
	public AsyncServerHandler(int port) {
		try {
			//����������ͨ��
			channel = AsynchronousServerSocketChannel.open();
			//�󶨶˿�
			channel.bind(new InetSocketAddress(port));
			System.out.println("���������������˿ںţ�" + port);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		//CountDownLatch��ʼ��
		//��������:�����һ������ִ�еĲ���֮ǰ ����ǰ���ֳ�һֱ����
		//�˴� ���ֳ��ڴ�����  ��ֹ�����ִ����ɺ��˳�
		//Ҳ����ʹ��while(true) + sleep
		//���ɻ����Ͳ���Ҫ�����������  ��Ϊ������ǲ����˳���
		latch = new CountDownLatch(1);
		//���ڽ��տͻ�������
		channel.accept(this,new AcceptHandler());
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
