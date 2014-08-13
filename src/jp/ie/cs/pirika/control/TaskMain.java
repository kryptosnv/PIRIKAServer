package jp.ie.cs.pirika.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import jp.ie.cs.pirika.communication.MessageType;
import jp.ie.cs.pirika.communication.TCPPacket;
import jp.ie.cs.pirika.log.AccessLog;
import jp.ie.cs.pirika.main.ServerMain;
import jp.ie.cs.pirika.prolog.ArgumentTree;
import jp.ie.cs.pirika.prolog.ProlMain;
import jp.ie.cs.pirika.room.ArgumentRoomRoot;

import com.igormaznitsa.prol.exceptions.ParserException;

/**
 * 命令を処理するクラス.
 * 
 * @author Yuki Katsura (katsura.y.ac@m.titech.ac.jp)
 */
public class TaskMain extends Basis implements MessageType, Runnable {
	private final BlockingQueue<TCPPacket> queue;
	private Thread taskThread = new Thread(this);
	private ExecutorService task = Executors.newFixedThreadPool(200);


	public TaskMain(BlockingQueue<TCPPacket> queue) {
		this.queue = queue;

		new CommandLine();

		this.taskThread.start();
	}

	public void run() {
		while (true) {
			try{
				this.classify(queue.take());
			}catch (InterruptedException intEx){
				break;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * classify message.
	 * @param list - message
	 */
	private void classify(TCPPacket packet){
		try{
			int type = packet.getProtocol();
			String name = packet.getName();
			Charset charset = Charset.forName("UTF-8");
			String contents = charset.decode(packet.getContents()).toString();

			if (Configuration.getBoolean("debug")) {
				System.out.println("Debug(Protocol) : " + type);
				System.out.println("Debug(UserName) : " + name);
				System.out.println("Debug(Messege)  : "+contents);
			}
			
			final Logger l = AccessLog.getLogger();
			l.info("Protocol: " + type);
			l.info("UserName: " +name);
			if (type!=1001) {
				l.info("Message: "+ contents);
			}
			
			if (type == MessageType.REQ_FOR_ID) {

			}else if(type == MessageType.UPDATE_PICTURE){
				try {
					this.writeImage(packet.getContents(), name);
					packet.write("COMPLETE");
				} catch (IOException e) {
					packet.write("FAILURE");
					l.warning("IOException: " + e.getMessage());
				}
			}else if(type == MessageType.GET_PICTURE){
				String[]strings = contents.split("\\$",0);
				ByteBuffer img;
				try {
					img = this.readImage(strings[0]);
					packet.write(img);
				} catch (IOException e) {
					l.warning("IOException: " + e.getMessage());
					packet.write("");
				}
				
			}else if(type == MessageType.GET_EALP){
				/*
				 * strings[0] = truthValue
				 * strings[1] = fileName 
				 */
				String[]strings = contents.split("\\$",0);
				if (strings.length>1) {
					try{
						String string = this.readEALP(name,strings[0],strings[1]);
						packet.write(string);
					}catch(FileNotFoundException e){
						packet.write("");
						l.warning("FileNotFoundException: " + e.getMessage());
					} catch (IOException e) {
						packet.write("");
						l.warning("IOException: " + e.getMessage());
					}
				}
			}else if(type == MessageType.GET_EALP_FILE_LIST){
				String[]strings = contents.split("\\$",0);
				if (strings.length>0) {
					String[] list = this.ealpFileList(name, strings[0]);
					StringBuffer sb = new StringBuffer();
					String s = new String();
					for (int i = 0; i < list.length; i++) {
						if (!list[i].startsWith(".")) {
							sb.append(list[i]+"$");	
						}
					}
					if (sb.length()!=0) {
						s = sb.substring(0, sb.length()-1);
					}
					packet.write(s);
				}
			}else if(type == MessageType.SEND_TRUTH_VALUE_FILE){
				/* strings[0] = truth value file name.
				 * strings[1] = file
				 */
				String[]strings = contents.split("\\$",0);
				if (strings.length>1) {
					try{
						ProlMain.checkPrologGrammer(strings[1]);
						this.saveTruthValueFile(name, strings[0], strings[1]);
						packet.write("ACCEPT");
					} catch(ParserException e){
						packet.write(String.valueOf(e.getLine()));
					} catch (IOException e) {
						l.warning("IOException: " + e.getMessage());
						packet.write("");
					}
				}
			}else if(type == MessageType.GET_TRUTH_VALUE_FILE){
				String[]strings = contents.split("\\$",0);
				if (strings.length>0) {
					try{
					String string = this.readTruthValue(strings[0]);
					packet.write(string);
					}catch(FileNotFoundException e){
						packet.write("FILE_NOT_FOUND");
					}catch (IOException e) {
						packet.write("Error");
						l.warning("IOException: " + e.getMessage());
					}
				}
			}else if(type == MessageType.GET_TRUTH_VALUE_FILE_LIST){
				String[] list = this.truthValueFileList();
				StringBuffer sb = new StringBuffer();
				String s = new String();
				for (int i = 0; i < list.length; i++) {
					if (!list[i].startsWith(".")) {
						sb.append(list[i]+"$");	
					}
				}
				if (sb.length()!=0) {
					s = sb.substring(0, sb.length()-1);
				}
				packet.write(s);
			}else if(type == MessageType.CHECK_EALP){
				try{
					ProlMain.checkEALPGrammer(contents);
					packet.write("ACCEPT");
				} catch(ParserException e){
					e.printStackTrace();
					packet.write(String.valueOf(e.getLine()));
				}
			}else if(type == MessageType.CALCULATE_01VALUE){
				this.setTreeTask(packet, "01value");
			}else if(type == MessageType.CALCULATE_2VALUE){
				this.setTreeTask(packet, "2value");
			}else if(type == MessageType.CALCULATE_4VALUE){
				this.setTreeTask(packet, "4value");
			}else if(type == MessageType.CALCULATE_7VALUE){
				this.setTreeTask(packet, "7value");
			}else if(type == MessageType.CALCULATE_INTVALUE){
				this.setTreeTask(packet, "intvalue");
			}else if(type == MessageType.CALCULATE_USER_DEFINE){
				/*
				 * strings[0] = ealp file name
				 * strings[1] = subject
				 * strings[2] = contents
				 * strings[3] = truth value file name
				 */
				String[]strings = contents.split("\\$",0);
				ArgumentTree tree = new ArgumentTree();
				Map<String, String> map = new HashMap<String,String>();
				
				if (strings.length>3) {
					String filePath;
					try {
						filePath = this.saveEalpFile(name, strings[3], strings[0], Arrays.asList(strings[2].split("\n")));
						map.put(name, filePath);
						tree.init(strings[3], map);
						this.task.submit(new TreeTask(packet, tree, strings[1]));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else if(type == MessageType.CREATE_ROOM){
				String[]strings = contents.split("\\$",0);
				if (strings.length>1) {
					ArgumentRoomRoot.addArgumentRoom(strings[0], strings[1], name);
					packet.write("COMPLETE");
				}
			}else if(type == MessageType.UPDATE_ROOM){
				/*
				 * strings[0] = subject
				 * strings[1] = fileName
				 * strings[2] = ealp
				 */
				String[]strings = contents.split("\\$",0);
				if (strings.length>2) {
					try{
						if (ProlMain.checkEALPGrammer(strings[2])) {
							String filePath = this.saveEalpFile(name, ArgumentRoomRoot.getTruthValue(strings[0]),
									strings[1], Arrays.asList(strings[2].split("\n")));
							ArgumentRoomRoot.updateInfo(strings[0], name, filePath);
							packet.write("ACCEPT");
						}
					}catch (ParserException e){
						packet.write(String.valueOf(e.getLine()));
					}catch (IllegalArgumentException e) {
						packet.write("ROOM_NOT_FOUND");
					} catch (IOException e) {
						l.warning("IOException: " + e.getMessage());
						packet.write("");
					}
				}
			}else if(type == MessageType.GET_ROOM_RESULT){
				String[]strings = contents.split("\\$",0);
				if (strings.length>0) {
					try{
						List<String> list = ArgumentRoomRoot.getRoomResult(strings[0]);
						StringBuffer sb = new StringBuffer();
						String s = new String();
						for (int i = 0; i < list.size(); i++) {
							sb.append(list.get(i)+"$");					
						}
						if (sb.length()!=0) {
							s = sb.substring(0, sb.length()-1);
						}
						packet.write(s);
					}catch(IllegalArgumentException e){
						packet.write("ROOM_NOT_FOUND");
					}
				}
			}else if(type == MessageType.GET_ROOM_LIST){
				String[]strings = contents.split("\\$",0);
				if (strings.length>0) {
					List<String> list = ArgumentRoomRoot.getRoomList(strings[0]);
					StringBuffer sb = new StringBuffer();
					String s = new String();
					for (int i = 0; i < list.size(); i++) {
						sb.append(list.get(i)+"$");					
					}
					if (sb.length()!=0) {
						s = sb.substring(0, sb.length()-1);
					}
					packet.write(s);
				}
			}else if(type == MessageType.GET_ROOM_MEMBER_LIST){
				String[]strings = contents.split("\\$",0);
				if (strings.length>0) {
					try{
						List<String> list = ArgumentRoomRoot.getRoomMember(strings[0]);
						StringBuffer sb = new StringBuffer();
						String s = new String();
						for (int i = 0; i < list.size(); i++) {
							sb.append(list.get(i)+"$");
							sb.append(this.getFileModified(list.get(i))+"$");
						}
						if (sb.length()!=0) {
							s = sb.substring(0, sb.length()-1);
						}
						packet.write(s);
					}catch(IllegalArgumentException e){
						packet.write("ROOM_NOT_FOUND");
					}
					
				}
			}else if(type == MessageType.GET_ROOM_NODE_INFO){
				String[]strings = contents.split("\\$",0);
				if (strings.length>0) {
					try{
						List<String> list = ArgumentRoomRoot.getNodeInfo(strings[0]);
						StringBuffer sb = new StringBuffer();
						String s = new String();
						for (int i = 0; i < list.size(); i++) {
							sb.append(list.get(i)+"$");
						}
						if (sb.length()!=0) {
							s = sb.substring(0, sb.length()-1);
						}
						packet.write(s);
					}catch(IllegalArgumentException e){
						packet.write("ROOM_NOT_FOUND");
					}
				}
			}else if(type == MessageType.DELETE_ROOM){
				String[]strings = contents.split("\\$",0);
				if (strings.length>0) {
					try{
						if (ArgumentRoomRoot.removeArgumentRoom(strings[0],name)) {
							packet.write("COMPLETE");
						}else{
							packet.write("NOT_SUPER_USER");
						}
					}catch(IllegalArgumentException e){packet.write("");}
				}
			}else if(type == MessageType.GET_GROUNDED){
				String[]strings = contents.split("\\$",0);
				if (strings.length>0) {
					try{
						packet.write(ArgumentRoomRoot.getGrounded(strings[0]));
					}catch(ConnectException e){
						packet.write("");
					}catch (IllegalArgumentException e) {
						packet.write("ROOM_NOT_FOUND");
					}catch (IOException e) {
						packet.write("");
					}
				}
			}else if(type == MessageType.GET_STABLE){
				String[]strings = contents.split("\\$",0);
				if (strings.length>0) {
					try{
						packet.write(ArgumentRoomRoot.getStable(strings[0]));
					}catch(ConnectException e){
						packet.write("");
					}catch(IllegalArgumentException e){
						packet.write("ROOM_NOT_FOUND");
					}catch (IOException e) {
						packet.write("");
					}
				}
			}else if(type == MessageType.GET_PREFERRED){
				String[]strings = contents.split("\\$",0);
				if (strings.length>0) {
					try{
						packet.write(ArgumentRoomRoot.getPreferred(strings[0]));
					}catch(ConnectException e){
						packet.write("");
					}catch (IllegalArgumentException e) {
						packet.write("ROOM_NOT_FOUND");
					}catch (IOException e) {
						packet.write("");
					}
				}
			}else if(type == MessageType.GET_COMPLETE){
				String[]strings = contents.split("\\$",0);
				if (strings.length>0) {
					try{
						packet.write(ArgumentRoomRoot.getComplete(strings[0]));
					}catch(ConnectException e){
						packet.write("");
					}catch (IllegalArgumentException e) {
						packet.write("ROOM_NOT_FOUND");
					}catch (IOException e) {
						packet.write("");
					}
				}
			}else{
				packet.close();
			}
		}catch (Exception e){
			e.printStackTrace();
			final Logger l = AccessLog.getLogger();
			l.severe("Exception: " + e.getMessage());
			packet.close();
			return;
		}
	}

	private Boolean setTreeTask(TCPPacket packet, String truthvalue){
		String name = packet.getName();
		Charset charset = Charset.forName("UTF-8");
		String contents = charset.decode(packet.getContents()).toString();

		ArgumentTree tree = new ArgumentTree();
		Map<String, String> map = new HashMap<String,String>();

		String[]strings = contents.split("\\$",0);
		/*
		 * strings[0] = filename
		 * strings[1] = subject
		 * strings[2] = EALP
		 */
		if (strings.length>2) {
			String filePath;
			try {
				filePath = this.saveEalpFile(name, truthvalue, strings[0], Arrays.asList(strings[2].split("\n")));
				map.put(name, filePath);
				tree.init(truthvalue, map);
				this.task.submit(new TreeTask(packet, tree, strings[1]));
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void shutdown(){
		this.taskThread.interrupt();
		this.task.shutdownNow();
		ArgumentRoomRoot.close();
	}


	/**
	 * Entry standby from command line.
	 * @author katsura
	 *
	 */
	private class CommandLine extends Basis implements Runnable,CommandType{
		private Boolean cfrag = true;


		private CommandLine(){
			Thread thread = new Thread(this);
			thread.start();
		}

		public void run() {

			BufferedReader stdReader = new BufferedReader(new InputStreamReader(System.in));

			String line;

			while (cfrag) {
				try {
					System.out.print("$ ");
					line = stdReader.readLine();
					if (line==null) {continue;}
					String[] l = line.split(" ");
					switch (this.getCommandType(l[0])) {
					case HALT:
						cfrag = false;
						System.out.println();
						System.out.println("Shutdown...");
						stdReader.close();
						ServerMain.shutdown();
						break;
					case ROOM_LIST:
						if (l.length>0) {
							List<String> list = ArgumentRoomRoot.getRoomList();
							StringBuffer sb = new StringBuffer();
							sb.append("      Room Name      :      Truth Value      \n");
							sb.append("---------------------------------------------\n");
							for (int i = 0; i < list.size(); i++) {
								sb.append(String.format("%20s : ", list.get(i)));
								sb.append(String.format("%20s\n", ArgumentRoomRoot.getTruthValue(list.get(i))));
							}
							sb.append("---------------------------------------------\n");
							System.out.println(sb.toString());
						}
						break;
					case ROOM_MEMBER:
						if (l.length>1) {
							try{
								List<String> list = ArgumentRoomRoot.getRoomMember(l[1]);
								StringBuffer sb = new StringBuffer();
								for (int i = 0; i < list.size(); i++) {
									sb.append(list.get(i)+"\n");
								}
								System.out.println(sb.toString());
							}catch(IllegalArgumentException e){
								System.err.println(e.getMessage());
							}
						}else{
							System.out.println("roomMember ROOMNAME(SUBJECT)");
						}
						break;
					case READ_EALP:
						if (l.length>1) {
							try{
								String sep = File.separator;
								StringBuffer sb = new StringBuffer();
								
								File f = new File("." + sep + "EALP");
								File[] namelist = f.listFiles();
								for(int i=0;i<namelist.length;i++){
									if (!namelist[i].getName().startsWith(".") && namelist[i].isDirectory()) {
										File[] tvlist = namelist[i].listFiles();
										for (int j = 0; j < tvlist.length; j++) {
											if (!tvlist[j].getName().startsWith(".") && tvlist[j].isDirectory()) {
												String[] filename = tvlist[j].list();
												for (int k = 0; k < filename.length; k++) {
													if (!filename[k].startsWith(".") && filename[k].equals(String.format("%s.ealp", l[1]))) {
														String s = this.readEALP(namelist[i].getName(),tvlist[j].getName(),filename[k]);
														sb.append(String.format("%10s%10s\n", namelist[i].getName(),tvlist[j].getName()));
														sb.append("--------------------------------------------------------------------\n");
														sb.append(s);
														sb.append("--------------------------------------------------------------------\n");
													}
												}
											}
										}
									}
								}
								
								System.out.println(sb.toString());
							}catch (FileNotFoundException e) {
								System.out.println(e.getMessage());
							}
						}else{
							System.out.println("readEalp ROOMNAME(SUBJECT)");
						}
						break;
					case VIEW_TREE:
						if (l.length>1) {
							try{
								ArgumentRoomRoot.displayArgumentTree(l[1]);
							}catch (IllegalArgumentException e){
								System.out.println(e.getMessage());
							}
						}else{
							System.out.println("viewTree ROOMNAME(SUBJECT");
						}
						break;
					case REMOVE_ROOM:
						if (l.length>1) {
							try{
								ArgumentRoomRoot.removeArgumentRoom(l[1]);
							}catch (IllegalArgumentException e){
								System.out.println(e.getMessage());
							}
						}else{
							System.out.println("removeRoom ROOMNAME(SUBJECT");
						}
						break;
					case SAVE_ROOM:
						System.out.println("The argument room is retained ...");
						ArgumentRoomRoot.saveRoom();
						System.out.println("Complete!");
						break;
					case LOAD_ROOM:
						System.out.println("The argument room is reconstructed ...");
						ArgumentRoomRoot.loadRoom();
						System.out.println("Complete!");
						break;
					case VIEW_FRAME:
						if (l.length>1) {
							try{
								ArgumentRoomRoot.displayArgumentFramework(l[1]);
							}catch (IllegalArgumentException e){
								System.out.println(e.getMessage());
							}
						}else{
							System.out.println("viewFramework ROOMNAME(SUBJECT");
						}
						break;
					case RELOAD:
						System.out.println("Re-loading configuration file.");
						Configuration.reload();
						System.out.println("Complete.");
						break;
					case HELP:
						System.out.println("halt - It ends this program.");
						System.out.println("roomList -  We acquired the room list.");
						System.out.println("roomMember - We acquired the room member list.");
						System.out.println("readEalp - We acquired all ealp file inside the room.");
						System.out.println("viewTree - The argument tree of the room is displayed.");
						System.out.println("removeRoom - We deleted the room.");
						System.out.println("save - The argument room is retained.");
						System.out.println("load - The argument room is reconstructed.");
						System.out.println("reload - The configure file is reloaded.");
						break;
					default:
						System.out.println("Command not found. Please see help or command \"help\".");
						break;
					}
				} catch (Exception e) {
//					e.printStackTrace();
				}
			}
		}

		public int getCommandType(String commandType) {
			commandType = commandType.toLowerCase();
			if (commandType.equals("halt")) 						return HALT;
			else if (commandType.equals("roomlist"))				return ROOM_LIST;
			else if (commandType.equals("roommember"))				return ROOM_MEMBER;
			else if (commandType.equals("readealp"))				return READ_EALP;
			else if (commandType.equals("viewtree"))				return VIEW_TREE;
			else if (commandType.equals("removeroom"))				return REMOVE_ROOM;
			else if (commandType.equals("save"))					return SAVE_ROOM;
			else if (commandType.equals("load"))					return LOAD_ROOM;
			else if (commandType.equals("viewframework"))			return VIEW_FRAME;
			else if (commandType.equals("reload"))					return RELOAD;
			else if (commandType.equals("help"))					return HELP;	
			else return -1;
		}
	}
}
