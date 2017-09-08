package cn.liujd.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import cn.liujd.Calculator;

public class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {

	//用于读取半包信息和发送应答
	private AsynchronousSocketChannel channel;
	
	public ReadHandler(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}
	//读取消息后的处理
	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		//flip操作
		attachment.flip();
		//
		byte[] message = new byte[attachment.remaining()];
		attachment.get(message);
		try {
			String expression = new String(message , "UTF-8");
			System.out.println("服务端收到信息:" + expression);
			String calResult = null;
			try {
				calResult = Calculator.cal(expression).toString();
			}catch(Exception e) {
				calResult = "计算错误" + e.getMessage();
			}
			//向客户端发送信息
			doWrite(calResult);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	//发送信息
	private void doWrite(String result) {
		byte[] bytes = result.getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
		writeBuffer.put(bytes);
		writeBuffer.flip();
		//异步写数据 参数与前面的read一样
		channel.write(writeBuffer,writeBuffer,new CompletionHandler<Integer , ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer buffer) {
				//如果没有发送完 就继续发送直到完成
				if(buffer.hasRemaining()) {
					channel.write(buffer, buffer, this);
				} else {
					//创建新的Buffer
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					//异步读  第三个参数为接收消息回调的业务Handler
					channel.read(readBuffer, readBuffer, new ReadHandler(channel));
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				try {
					channel.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
