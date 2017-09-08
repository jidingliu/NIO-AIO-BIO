package cn.liujd.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import javax.script.ScriptException;

import cn.liujd.Calculator;

/**
 * NIO����˴����߳�
 * @author liujd
 *
 */
public class ServerHandler implements Runnable{

	private Selector selector;
	private ServerSocketChannel serverChannel;
	/**
	 * Volatile���εĳ�Ա������ÿ�α��̷߳���ʱ����ǿ�ȴӹ����ڴ����ض��ó�Ա������ֵ��
	 * ���ң�����Ա���������仯ʱ��ǿ���߳̽��仯ֵ��д�������ڴ档
	 * �������κ�ʱ�̣�������ͬ���߳����ǿ���ĳ����Ա������ͬһ��ֵ�� 
	 */
	private volatile boolean started;
	
	//���췽��  ָ��Ҫ�����Ķ˿ں�
	public ServerHandler(int port) {
		try {
			//����ѡ����
			selector = Selector.open();
			//�򿪷������׽���ͨ��
			serverChannel = ServerSocketChannel.open();
			//���Ϊtrue ���ͨ��������������ģʽ  ���Ϊfalse ���ͨ���������ڷ�����ģʽ
			serverChannel.configureBlocking(false);//����������ģʽ
			
			/**
			 * InetSocketAddress��SocketAddress��ʵ�����ࡣ
			 *	����ʵ�� IP �׽��ֵ�ַ��IP ��ַ + �˿ںţ����������κ�Э�顣
			 *	��ʹ��Socket�����ӷ�����ʱ��򵥵ķ�ʽ����ֱ��ʹ��IP�Ͷ˿ڣ���Socket���е�connect������δ�ṩ���ַ�ʽ������ʹ��SocketAddress������connect�������ݷ�������IP�Ͷ˿ڡ�
			 *	SocketAddressֻ�Ǹ������࣬��������һ��Ĭ�ϵĹ��췽���⣬�����ķ�������abstract�ģ���ˣ����Ǳ���ʹ��SocketAddress������������SocketAddress����Ҳ����Ψһ������InetSocketAddress
			 */
			//��ȡ���ͨ�������ķ������׽���
			ServerSocket serverSocket = serverChannel.socket();
			//bind����  ��һ�������� ��IP���߶˿� �ڶ��������� backlog��Ϊ1024 ��ʾ��������
			serverSocket.bind(new InetSocketAddress(port),1024);
			//�����ͻ�����������
			//��ServerSocketChannelע�ᵽReactor�߳��е�Selector�� ����ACCEPT�¼�
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			//SelectionKey.OP_ACCEPT�����׽��ֽ��ղ����Ĳ�����λ
			//��Ƿ������Ѿ�����
			started = true;
			System.out.println("���������������˿ںţ�" + port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void stop() {
		started = false;
	}
	@Override
	public void run() {
		//ѭ������selector
		while(started) {
			try {
				//�����Ƿ��ж�д�¼�����  selectorÿ��һ�뱻����һ��
				selector.select(1000);
				//���� ֻ�е�����һ��ע���ʱ�䷢����ʱ��Ż����
				//selector.select();
				
				//�������ѡ��������ѡ�����
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				SelectionKey key = null;
				while(it.hasNext()) {
					key = it.next();
					it.remove();
					try{
					handlerInput(key);
					}catch(Exception e) {
						if(null != key) {
							//����ȡ���˼���ͨ������ѡ������ע��
							key.cancel();
							if(null != key.channel()) {
								//channel()���� ����Ϊ֮�����˼���ͨ��
								//�ر�ͨ��
								key.channel().close();
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//selector�رպ���Զ��ͷ�����������Դ
		if(null != selector) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void handlerInput(SelectionKey key) throws IOException{
		//�жϴ˼��Ƿ���Ч
		if(key.isValid()) {
			//isAcceptable()����  ���Դ˼���ͨ���Ƿ���׼���ý����µ��׽�������
			//�����½����������Ϣ
			if(key.isAcceptable()) {
				//channel()��������Ϊ֮�����˼���ͨ����
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				//ͨ��ServerSocketChannel��accept����socketChannelʵ��
				//��ɸò�����ζ�����TCP��������  TCP������·��ʽ����
				SocketChannel sc = ssc.accept();
				//����Ϊ������
				sc.configureBlocking(false);
				//ע��Ϊ��
				sc.register(selector, SelectionKey.OP_READ);
			}
			//isReadable()�������Դ˼���ͨ���Ƿ��Ѿ�׼���ý��ж�ȡ
			//����Ϣ
			if(key.isReadable()) {
				SocketChannel sc = (SocketChannel)key.channel();
				//����ByteBuffer  ���ҿ���һ��1M�Ļ�����
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				//��ȡ��������  ���ض�ȡ�����ֽ���
				int readBytes = sc.read(buffer);
				//��ȡ���ֽ� ���ֽڽ��б����
				if(readBytes > 0) {
					//����������ǰ��limit����Ϊposition=0 ���ں����Ի������Ķ�ȡ��������ת�˻�������
					buffer.flip();
					//���ݻ������ɶ��ֽ��������ֽ����飨remaining()���ص�ǰλ��������֮���Ԫ������
					byte[] bytes = new byte[buffer.remaining()];
					//���������ɶ��ֽ����鸴�Ƶ��½���������
					buffer.get(bytes);
					String expression = new String(bytes,"UTF-8");
					System.out.println("������յ���Ϣ��" + expression);
					//��������
					String result = null;
					
					try {
						result = Calculator.cal(expression).toString();
						System.out.println("����˼�����Ϊ" + result);
					} catch (ScriptException e) {
						result = "�������" + e.getMessage();
					}
					//�յ�Ӧ����Ϣ
					doWrite(sc , result);
				}
				//û�ж�ȡ���ֽ� ʡ��
				//else if (readBytes == 0);
				
				//��·�Ѿ��ر�  �ͷ���Դ
				else if(readBytes < 0) {
					key.cancel();
					sc.close();
				}
			}
		}
	}
	//�첽����Ӧ����Ϣ
	private void doWrite(SocketChannel channel, String response) throws IOException{
		//����Ϣ����Ϊ�ֽ�����
		byte[] bytes = response.getBytes();
		//����������������ByteBuffer
		ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
		//���ֽ����鸴�Ƶ�������
		writeBuffer.put(bytes);
		//flip����
		writeBuffer.flip();
		//���ͻ��������ֽ�����
		channel.write(writeBuffer);
		
	}

}
