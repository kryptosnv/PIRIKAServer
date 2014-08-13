package jp.ie.cs.pirika.control;


import java.util.List;
import java.util.concurrent.Callable;

import jp.ie.cs.pirika.communication.TCPPacket;
import jp.ie.cs.pirika.prolog.ArgumentTree;

import com.igormaznitsa.prol.exceptions.ProlInstantiationErrorException;

public class TreeTask implements Callable<List<String>>{
	private TCPPacket packet;
	private ArgumentTree tree;
	private String subject;
	
	public TreeTask(TCPPacket packet, ArgumentTree tree, String subject){
		this.packet = packet;
		this.tree = tree;
		this.subject = subject;
	}


	@Override
	public List<String> call() throws Exception {
		try{
			List<String>list = tree.calculate(subject);
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < list.size(); i++) {
				if (i!=list.size()-1) {
					sb.append(list.get(i)+"$");
				}else{
					sb.append(list.get(i));
				}
			}
			packet.write(sb.toString());
			return list;
		}catch(ProlInstantiationErrorException e){
			packet.write("TRUTH_VALUE_ERROR");
		}
		return null;
	}
}
