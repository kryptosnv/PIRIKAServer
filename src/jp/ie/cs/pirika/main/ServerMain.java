package jp.ie.cs.pirika.main;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import jp.ie.cs.pirika.communication.ComMain;
import jp.ie.cs.pirika.communication.TCPPacket;
import jp.ie.cs.pirika.control.Configuration;
import jp.ie.cs.pirika.control.TaskMain;

public final class ServerMain {
	private static ComMain cMain;
	private static TaskMain tMain;
	
	/**
	 * Main メソッド．
	 * @param args
	 */
	public static void main(String[] args) {
		BlockingQueue<TCPPacket> queue = new ArrayBlockingQueue<TCPPacket>(2000, true);
		try {
			cMain = new ComMain(queue,Configuration.getInt("port"));
			tMain = new TaskMain(queue);
		} catch (ClosedChannelException ex) {
            System.err.println("Socket is closed.");
        } catch (SocketException ex) {
            System.err.println("Socket error.");
        } catch (IOException ex) {
            System.err.println("IOException.");
        }
	}
	
	public static void shutdown(){
		cMain.shutdown();
		tMain.shutdown();
	}
}
