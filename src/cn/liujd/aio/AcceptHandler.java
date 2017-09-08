package cn.liujd.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

//��Ϊhandler���տͻ�������
//CompletionHandler�ӿ�  ���������첽I/O��������Ĵ�����  ���͵�һ����������I/O�����Ľ������   �ڶ������������ӵ�I/O�����Ķ�������
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,AsyncServerHandler> {

	//AsynchronousSocketChannel An asynchronous channel for stream-oriented connecting sockets.
	@Override
	public void completed(AsynchronousSocketChannel channel, AsyncServerHandler serverHandler) {
		//�������������ͻ��˵�����
		Server.clientCount ++ ;
		System.out.println("���ӵĿͻ���������" + Server.clientCount);
		serverHandler.channel.accept(serverHandler , this);
		//�����µ�Buffer
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		//�첽�� ����������Ϊ������Ϣ�ص���ҵ��Handler
		channel.read(buffer, buffer, new ReadHandler(channel));
	}

	@Override
	public void failed(Throwable exc, AsyncServerHandler serverHandler) {
		exc.printStackTrace();
		serverHandler.latch.countDown();
	}
}
