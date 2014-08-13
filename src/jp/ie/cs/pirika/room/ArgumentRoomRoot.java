package jp.ie.cs.pirika.room;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 複数の議論ルームを管理する親クラス.
 * @author katsura
 *
 */
public class ArgumentRoomRoot{
	/**
	 * key - subject</br>
	 * value - ArgumentRoom
	 */
	private static Map<String,ArgumentRoom> room = new HashMap<String,ArgumentRoom>();
	
	/**
	 * room を追加.
	 * @param subject - 議題.
	 * @param truthValue - 真理値.
	 * @param userName - userName.
	 */
	public static void addArgumentRoom(String subject,String truthValue,String userName){
		subject = subject.replace(" ", "");
		subject = subject.replace("　", "");
		if (!room.containsKey(subject)) {
			room.put(subject,new ArgumentRoom(subject,truthValue,userName));
		}
	}
	
	/**
	 * room を削除.
	 * @param subject
	 */
	public static void removeArgumentRoom(String subject) throws IllegalArgumentException{
		if(room.containsKey(subject)){
			ArgumentRoom r = room.get(subject);
			r.close();
			room.remove(subject);
		}else{
			throw new IllegalArgumentException("Illegitimate room name : " + subject);
		}
	}
	
	/**
	 * room を削除.
	 * @param subject
	 */
	public static Boolean removeArgumentRoom(String subject,String userName) throws IllegalArgumentException{
		if(room.containsKey(subject)){
			ArgumentRoom r = room.get(subject);
			if (r.close(userName)) {
				room.remove(subject);
				return true;
			}
		}else{
			throw new IllegalArgumentException("Illegitimate room name : " + subject);
		}
		return false;
	}
	
	/**
	 * roomリストを取得.
	 * @return - ArrayList
	 */
	public static List<String> getRoomList(){
		List<String> list = new ArrayList<String>();
		Iterator<String> it = room.keySet().iterator();
        while (it.hasNext()) {
            String o = it.next();
            list.add(o);
        }
		return list;
	}
	
	/**
	 * roomリストを取得.
	 * @param tv - truthValue
	 * @return - List
	 */
	public static List<String> getRoomList(String tv){
		List<String> list = new ArrayList<String>();
		Iterator<String> it = room.keySet().iterator();
        while (it.hasNext()) {
            String o = it.next();
            ArgumentRoom ar = room.get(o);
            if(tv.equals(ar.getTruthValue())){
            	list.add(o);
            }
        }
		return list;
	}
	
	/**
	 * 知識ベースを更新.
	 * @param subject
	 * @param userName
	 * @param filePath
	 */
	public static void updateInfo(String subject, String userName, String filePath) throws IllegalArgumentException{
		ArgumentRoom r = room.get(subject);
		if (r!=null) {
			r.updateUser(userName, filePath);
		}else{
			throw new IllegalArgumentException("Illegitimate room name : " + subject);
		}
	}
	
	/**
	 * 真理値を取得.
	 * @param subject
	 * @return
	 */
	public static String getTruthValue(String subject) throws IllegalArgumentException{
		ArgumentRoom r = room.get(subject);
		if (r!=null) {
			return r.getTruthValue();
		}else{
			throw new IllegalArgumentException("Illegitimate room name : " + subject);
		}
	}
	
	/**
	 * 参加者を取得.
	 * @param subject
	 * @return
	 */
	public static List<String> getRoomMember(String subject) throws IllegalArgumentException{
		ArgumentRoom r = room.get(subject);
		if (r!=null) {
			return r.getUserList();
		}else{
			throw new IllegalArgumentException("Illegitimate room name : " + subject);
		}
	}
	
	/**
	 * 議論結果を取得.
	 * @param subject
	 * @return
	 */
	public static List<String> getRoomResult(String subject) throws IllegalArgumentException{
		ArgumentRoom r = room.get(subject);
		if (r!=null) {
			return r.getRoomResult();
		}else{
			throw new IllegalArgumentException("Illegitimate room name : " + subject);
		}
	}
	
	/**
	 * 全ノード情報(論証，攻撃関係)を受け取る.
	 * @param subject
	 * @return
	 */
	public static List<String> getNodeInfo(String subject) throws IllegalArgumentException{
		ArgumentRoom r = room.get(subject);
		if (r!=null) {
			return r.getNodeInfo();
		}else{
			throw new IllegalArgumentException("Illegitimate room name : " + subject);
		}
	}
	
	/**
	 * GroundedExtension の結果を受け取る.
	 * @param subject
	 * @return
	 * @throws ConnectException
	 */
	public static String getGrounded(String subject) throws IOException, IllegalArgumentException{
		ArgumentRoom r = room.get(subject);
		if (r!=null) {
			return r.getGroundedResult();
		}else{
			throw new IllegalArgumentException("Illegitimate room name : " + subject);
		}
	}
	
	/**
	 * StableExtension の結果を受け取る.
	 * @param subject
	 * @return
	 * @throws ConnectException
	 */
	public static String getStable(String subject) throws IOException, IllegalArgumentException{
		ArgumentRoom r = room.get(subject);
		if (r!=null) {
			return r.getStableResult();
		}else{
			throw new IllegalArgumentException("Illegitimate room name : " + subject);
		}
	}
	
	/**
	 * PreferredExtension の結果を受け取る.
	 * @param subject
	 * @return
	 * @throws ConnectException
	 */
	public static String getPreferred(String subject) throws IOException,IllegalArgumentException{
		ArgumentRoom r = room.get(subject);
		if (r!=null) {
			return r.getPreferredResult();
		}else{
			throw new IllegalArgumentException("Illegitimate room name : " + subject);
		}
	}
	
	/**
	 * CompleteExtension の結果を受け取る.
	 * @param subject
	 * @return
	 * @throws ConnectException
	 */
	public static String getComplete(String subject) throws IOException,IllegalArgumentException{
		ArgumentRoom r = room.get(subject);
		if (r!=null) {
			return r.getCompleteResult();
		}else{
			throw new IllegalArgumentException("Illegitimate room name : " + subject);
		}
	}
	
	/**
	 * 議論木表示する．
	 * @param subject
	 */
	public static void displayArgumentTree(String subject) throws IllegalArgumentException{
		ArgumentRoom r = room.get(subject);
		if (r!=null) {
			r.displayArgumentTree();
		}else{throw new IllegalArgumentException("Illegitimate room name : " + subject);};
	}
	
	/**
	 * 議論フレームワーク表示する．
	 * @param subject
	 */
	public static void displayArgumentFramework(String subject) throws IllegalArgumentException{
		ArgumentRoom r = room.get(subject);
		if (r!=null) {
			r.displayFrame();
		}else{throw new IllegalArgumentException("Illegitimate room name : " + subject);};
	}
	
	/**
	 * 議論ルームを保存.
	 */
	public static void saveRoom(){
		Iterator<String> key = room.keySet().iterator();
		for (int i=0; key.hasNext(); i++) {
			ArgumentRoom ar = room.get(key.next());
			ar.saveRoom(i);
		}
	}
	
	/**
	 * 議論ルームを復元.
	 */
	public static void loadRoom(){
		String sep = File.separator;
		
		File f = new File("."+sep+"Room");
		File[] list = f.listFiles();
		for (File file : list) {
			Properties props = new Properties();
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try {
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				props.load(bis);
				
				if (!file.getName().startsWith(".") && !room.containsKey(props.getProperty("subject"))) {
					try {
						ArgumentRoom ar = new ArgumentRoom(props);
						room.put(props.getProperty("subject"), ar);
					} catch (IllegalStateException e) {
						System.err.println(e.getMessage() + " : " + file.getName());
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if (fis!=null) {
						bis.close();
						fis.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static void close(){
		List<String> list = ArgumentRoomRoot.getRoomList();
		for (int i = 0; i < list.size(); i++) {
			ArgumentRoomRoot.removeArgumentRoom(list.get(i));
		}
	}
}
