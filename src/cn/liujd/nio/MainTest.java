package cn.liujd.nio;

import java.util.Scanner;

//���Է���
public class MainTest {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		//���з�����
		Server.start();
		//����ͻ������ȷ��������ǰִ�д���
		Thread.sleep(100);
		//���пͻ���
		Client.start();
		while(Client.sendMsg(new Scanner(System.in).nextLine()));
	}
}
