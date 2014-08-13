package jp.ie.cs.pirika.communication;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

public interface Handler {
	public final static int BUF_SIZE=1024*4;
    public void handle(SelectionKey key) throws ClosedChannelException, IOException;
}
