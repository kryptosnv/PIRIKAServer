package jp.ie.cs.pirika.communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import jp.ie.cs.pirika.log.AccessLog;

/**
 * <p>Communication Main Class.</p>
 * This class handle all communication and stack communication contents.
 * @author Yuki Katsura (katsura.y.ac@m.titech.ac.jp)
 *
 */
public class ComMain implements Runnable{
	private Selector selector;
	private ServerSocketChannel serverSocketChannel = null;
	private Thread thread;

	/**
	 * initialize.
	 * @param queue - task
	 * @param port - port number
	 */
	public ComMain(BlockingQueue<TCPPacket> queue, int port) throws ClosedChannelException, SocketException, IOException{
		selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().setReuseAddress(true);
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, new AcceptHandler(queue));
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Standby arrival from user. 
	 */
	public void run(){
		try {
			while(true) {
				try{
					this.selector.select();
				}catch (IOException e){System.err.println("Connection Error.");}

				try{
					Set<SelectionKey> keys = this.selector.selectedKeys();
					for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext(); ) {
						SelectionKey key = it.next();
						it.remove();
						Handler handler = (Handler)key.attachment();
						try{
							handler.handle(key);
						}catch(IOException e){
							key.cancel();
							try {
								key.channel().close();
							} catch (IOException e1) {
							}
						}
					}
				}catch(Exception e){
					final Logger logger = AccessLog.getLogger();
					logger.severe("Exception: "+e.getMessage());
					e.printStackTrace();
				}

			}
		} catch (ClosedSelectorException e){
			e.printStackTrace();
		} finally {
			try {
				for (SelectionKey key: selector.keys()) {
					key.channel().close();
				}
				if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
					serverSocketChannel.close();
				}
			} catch(ClosedSelectorException e){
				e.printStackTrace();
			} catch(ClosedChannelException e){
				e.printStackTrace();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void shutdown(){
		try {
			thread.interrupt();
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
