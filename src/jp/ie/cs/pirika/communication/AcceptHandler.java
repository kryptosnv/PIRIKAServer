package jp.ie.cs.pirika.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import jp.ie.cs.pirika.log.AccessLog;

/**
 * Standby new user.
 * @author katsura
 *
 */
public class AcceptHandler implements Handler {
	private BlockingQueue<TCPPacket> queue;
	
	public AcceptHandler(BlockingQueue<TCPPacket> queue){
		this.queue = queue;
	}
	
    public void handle(SelectionKey key) throws ClosedChannelException, IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel)key.channel();
        
        SocketChannel channel = serverChannel.accept();
        InetAddress i = channel.socket().getInetAddress();
        channel.configureBlocking(false);
        
        final Logger logger = AccessLog.getLogger();
        logger.info("Access: "+i.getHostAddress() +"="+i.getCanonicalHostName());
        
        IOHandler io = new IOHandler(queue);
        SelectionKey iokey = channel.register(key.selector(), SelectionKey.OP_READ, io);
        io.setKey(iokey);
    }
}
