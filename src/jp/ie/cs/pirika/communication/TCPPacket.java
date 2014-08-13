package jp.ie.cs.pirika.communication;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class TCPPacket {
	private String name = null;
	private int protocol=0;
	private int remaindLength=0;
	private ByteBuffer contents = null;
	private List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
	private ByteBuffer buf = ByteBuffer.allocate(Handler.BUF_SIZE);
	private ByteBuffer intBuffer = ByteBuffer.allocate(Handler.BUF_SIZE+10);
	private BlockingQueue<TCPPacket> queue;
	private IOHandler parent;
	
	public TCPPacket(BlockingQueue<TCPPacket> queue,IOHandler handler){
		this.queue = queue;
		this.parent = handler;
	}
	
	public String getName(){
		return new String(this.name);
	}
	
	public int getProtocol(){
		return this.protocol;
	}
	
	public ByteBuffer getContents(){
		ByteBuffer buffer = ByteBuffer.allocate(contents.limit());
		buffer.put(contents);
		contents.position(0);
		buffer.flip();
		return buffer;
	}
	
	public void clear(){
		this.name = null;
		this.protocol = 0;
		this.remaindLength = 0;
		this.contents=null;
		this.buffers.clear();
		this.buf.clear();
		this.intBuffer.clear();
	}
	
	public void write(String str){
		this.parent.addWriteBuffer(str);
	}
	
	public void write(ByteBuffer buf){
		this.parent.addWriteBuffer(buf);
	}
	
	public void close(){
		this.parent.close();
	}

	public Boolean setByteBuffer(ByteBuffer buffer){
		if (remaindLength<=0) {
			if (this.intBuffer.position()==0) {
				try{
					remaindLength = buffer.getInt();
					remaindLength = remaindLength-buffer.limit();
					if (this.buf.capacity()-this.buf.position()>buffer.limit()) {
						this.buf.put(buffer);
					}else{
						this.buf.flip();
						this.buffers.add(this.buf);
						this.buf = ByteBuffer.allocate(Handler.BUF_SIZE);
						this.buf.put(buffer);
					}
				}catch(BufferUnderflowException e){
					// 初回の受信バイト数が4バイト以下の場合
					this.intBuffer.put(buffer);
					e.printStackTrace();
					return true;
				}
			}else{
				this.intBuffer.put(buffer);
				this.remaindLength = this.intBuffer.getInt();
				this.buf.put(this.intBuffer);
				remaindLength = remaindLength-intBuffer.limit();
				this.intBuffer.clear();
			}
		}else{
			if (this.buf.capacity()-this.buf.position()>buffer.limit()) {
				this.buf.put(buffer);
			}else{
				this.buf.flip();
				this.buffers.add(this.buf);
				this.buf = ByteBuffer.allocate(Handler.BUF_SIZE);
				this.buf.put(buffer);
			}
			remaindLength = remaindLength-buffer.limit();
		}

		if (remaindLength<=0) {
			this.buf.flip();
			this.buffers.add(this.buf);
			ByteBuffer b2 = ByteBuffer.allocate(Handler.BUF_SIZE*this.buffers.size());
			
			for (int i = 0; i < this.buffers.size(); i++) {
				ByteBuffer buf = this.buffers.get(i);
				b2.put(buf);
			}
			b2.flip();
			this.protocol = b2.getInt(0);
			byte[] sep = new String("$").getBytes();
			b2.position(4);
			for(int i=b2.position();i<b2.limit();i++){
				Byte b = new Byte(b2.get(i));
				if(b.compareTo(sep[0])==0){
					int pos = b2.position();
					byte[] n = new byte[i-pos];
//					b2.get(n,pos,i-pos);
					for (int j = 0; j < n.length; j++) {
						n[j] = b2.get(); 
					}
					b2.position(i+1);
					try {
						this.name = new String(n,"UTF-8");
						break;
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			contents = ByteBuffer.allocate(b2.limit());
			contents.put(b2);
			contents.flip();
			queue.add(this);
			this.buf.clear();
			this.buffers.clear();
		}
		return true;
	}
}
