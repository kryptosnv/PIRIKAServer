package jp.ie.cs.pirika.communication;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import jp.ie.cs.pirika.log.AccessLog;

/**
 * Input-output processing.
 * @author katsura
 *
 */
public class IOHandler implements Handler {
	private List<ByteBuffer> writeBuffers = new ArrayList<ByteBuffer>();
	private SelectionKey key;
	private TCPPacket packet = null;

	public IOHandler(BlockingQueue<TCPPacket> queue){
		this.packet = new TCPPacket(queue,this);
	}
	
	public void setKey(SelectionKey key){
		if(this.equals(key.attachment())){
			this.key = key;
		}
	}

	/**
	 * Adding the string to the buffer.
	 * @param str - message is written.
	 */
	public void addWriteBuffer(String str){
		Charset charset = Charset.forName("UTF-8");
		ByteBuffer buffer = null;
		if (str!=null) {
			buffer = ByteBuffer.allocate(str.getBytes().length+4);
			buffer.putInt(str.getBytes().length+4);
			buffer.put(charset.encode(str));
		}else{
			buffer = ByteBuffer.allocate("Error".getBytes().length+4);
			buffer.putInt("Error".getBytes().length+4);
			buffer.put(charset.encode("Error"));
		}
		final Logger l = AccessLog.getLogger();
		l.info("Write: " + str);
//		System.out.println(Integer.toHexString(str.getBytes().length)+":"+str.getBytes().length);
		buffer.flip();
		this.writeBuffers.add(buffer);
		this.key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		Selector s = this.key.selector();
		s.wakeup();
	}
	
	/**
	 * Adding the ByteBuffer to write queue.
	 * @param buf
	 */
	public void addWriteBuffer(ByteBuffer buf){
		if(buf!=null){
			buf.position(0);
			ByteBuffer buffer = ByteBuffer.allocate(buf.limit()+4);
			buffer.putInt(buf.limit()+4);
			buffer.put(buf);
			buffer.flip();
			this.writeBuffers.add(buffer);
			this.key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			Selector s = this.key.selector();
			s.wakeup();
		}
	}
	
	
	/**
	 * Socket is closed.
	 */
	public void close(SelectionKey key){
		SocketChannel channel = (SocketChannel)key.channel();
		try {
			channel.close();
			key.cancel();
//			System.out.println("Socket is closed.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Socket is closed.
	 */
	public void close(){
		SocketChannel channel = (SocketChannel)key.channel();
		try {
			channel.close();
			key.cancel();
			
		} catch (IOException e) {
			e.printStackTrace();
//			System.err.println("Socket is closed.");
		}
	}

	public void handle(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel)key.channel();
		try{
			// 読みこみ可であれば、読みこみを行う
			if (key.isReadable()) {
				this.read(key);
			}
//			System.out.println(key.toString());
			// 書きこみ可であれば、書きこみを行う
			if (key.isWritable() && key.isValid()) {
				this.write(key);
			}
		}catch (CancelledKeyException e){
			key.cancel();
		}catch (ClosedChannelException e){
			channel.close();
			key.cancel();
			System.err.println("Socket is closed.");
		}
	}

	/**
	 * Message is read from socket.
	 * @param key
	 * @throws IOException
	 * @throws ClosedChannelException
	 */
	private void read(SelectionKey key) throws IOException, ClosedChannelException {
		SocketChannel channel = (SocketChannel)key.channel();
		// 読みこみ用のバッファの生成
		ByteBuffer buffer = ByteBuffer.allocate(Handler.BUF_SIZE);
		int len = channel.read(buffer);
		if (len==-1) {
			this.close(key);
		}else{
			buffer.flip();
			packet.setByteBuffer(buffer);
		}
	}

	/**
	 * Message is written to the socket.
	 * @param key
	 * @throws IOException
	 * @throws ClosedChannelException
	 */
	private void write(SelectionKey key) throws IOException ,ClosedChannelException{
		SocketChannel channel = (SocketChannel)key.channel();

		if (!writeBuffers.isEmpty()) {
			// リストが空でなければ、先頭のバッファを取り出し書きこみを行う
			ByteBuffer buffer = writeBuffers.get(0);
//			buffer.position(0);
			int len = channel.write(buffer);
			if(len==0){
				writeBuffers.remove(0);
			}
		} else {
			// 書きこむデータがなければ、書きこみ操作の監視をやめる
			key.interestOps(SelectionKey.OP_READ);
		}
	}
}
