package jp.ie.cs.pirika.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import jp.ie.cs.pirika.control.Configuration;

public class ConnectSemanticsServer {
	protected volatile String groundedNum = String.valueOf(-1);
	protected volatile String stableNum = String.valueOf(-1);
	protected volatile String preferredNum = String.valueOf(-1);
	protected volatile String completeNum = String.valueOf(-1);

	public String getTaskNum(String attack, int num, String sem) throws IOException{
		Socket socket = null;
		PrintWriter pw = null;
		BufferedReader br = null;
		try {
			socket = new Socket(Configuration.getString("semantics_connection_address"),Configuration.getInt("semantics_connection_port"));
			if (socket.isConnected()) {
				pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
				pw.println(sem);
				pw.println(attack);
				pw.println(String.valueOf(num));
				pw.println("END_OF_MESSAGE");

				br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				socket.setSoTimeout(10000);
				String s = br.readLine();
				br.close();
				socket.close();
				return s;
			}
		} finally{
			try {
				if (socket!=null) {socket.close();}
				if(pw!=null){pw.close();}
				if(br!=null){br.close();}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private String getTaskResult(String taskNum) throws IOException{
		Socket socket = new Socket(Configuration.getString("semantics_connection_address"),Configuration.getInt("semantics_connection_port"));
		if (!socket.isConnected()) {throw new SocketException();}
		PrintWriter pw = null;
		BufferedReader br = null;
		List<String> list = new ArrayList<String>();
		try {
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			pw.println("RESULT");
			pw.println(taskNum);
			pw.println("END_OF_MESSAGE");

			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			socket.setSoTimeout(10000);
			String s;
			while((s=br.readLine())!=null){
				if (!s.equals("END_OF_MESSAGE")) {
					list.add(s);
				}else{break;}
			}
			br.close();
			socket.close();
			return list.toString();
		} finally{
			if (socket!=null) {socket.close();}
			if(pw!=null){pw.close();}
			if(br!=null){br.close();}
		}
	}

	public String getSemanticsResult(String semantics) throws IOException{
		if (semantics.toUpperCase().equals("GROUNDED")) {
			return this.getTaskResult(groundedNum);
		}else if(semantics.toUpperCase().equals("STABLE")){
			return this.getTaskResult(stableNum);
		}else if(semantics.toUpperCase().equals("PREFERRED")){
			return this.getTaskResult(preferredNum);
		}else if(semantics.toUpperCase().equals("COMPLETE")){
			return this.getTaskResult(completeNum);
		}else{
			return null;
		}
	}

	public String getGroundedResult() throws IOException{
		return this.getTaskResult(groundedNum);
	}

	public String getStableResult() throws IOException{
		return this.getTaskResult(stableNum);
	}

	public String getPreferredResult() throws IOException{
		return this.getTaskResult(preferredNum);
	}

	public String getCompleteResult() throws IOException{
		return this.getTaskResult(completeNum);
	}
	/**
	 * タスクをキャンセルする
	 * @param semantics - 意味論を表す記号
	 * @throws IOException
	 */
	public void cancelTask(String semantics) throws IOException{
		if (semantics.toUpperCase().equals("GROUNDED")) {
			this.cancel(groundedNum);
		}else if(semantics.toUpperCase().equals("STABLE")){
			this.cancel(stableNum);
		}else if(semantics.toUpperCase().equals("PREFERRED")){
			this.cancel(preferredNum);
		}else if(semantics.toUpperCase().equals("COMPLETE")){
			this.cancel(completeNum);
		}else{
			return;
		}
	}
	/**
	 * タスク番号が示すタスクをキャンセル.
	 * @param taskNum - 番号
	 * @return
	 * @throws IOException
	 */
	private String cancel(String taskNum) throws IOException{
		Socket socket = null;
		PrintWriter pw = null;
		BufferedReader br = null;
		try {
			socket = new Socket(Configuration.getString("semantics_connection_address"),Configuration.getInt("semantics_connection_port"));
			if (!socket.isConnected()) {throw new SocketException();}
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			pw.println("CANCEL");
			pw.println(taskNum);
			pw.println("END_OF_MESSAGE");

			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			socket.setSoTimeout(10000);
			String s = br.readLine();
			br.close();
			socket.close();
			return s;
		} finally{
			if (socket!=null) {socket.close();}
			if(pw!=null){pw.close();}
			if(br!=null){br.close();}
		}
	}
}

