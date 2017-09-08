package cn.liujd.bio;

import java.io.IOException;
import java.util.Random;

/**
 * 测试方法
 * @author liujd
 *
 */
public class MainTest {

	public static void main(String[] args) throws InterruptedException {
		//运行服务器
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//ServerNormal.start();
					ServerBetter.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		//避免客户端先于服务器启动前执行代码
		Thread.sleep(1000);
		//运行客户端
		new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				char operators[] = {'+','-','*','/'};
				Random random = new Random(System.currentTimeMillis());
				while(true) {
					//随机产生算数表达式
					String expression = random.nextInt(10)+""+operators[random.nextInt(4)]+""+(random.nextInt(10)+1);
					Client.send(expression);
					try {
						Thread.currentThread().sleep(random.nextInt(1000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
