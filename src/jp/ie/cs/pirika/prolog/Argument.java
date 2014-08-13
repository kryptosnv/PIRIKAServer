package jp.ie.cs.pirika.prolog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ie.cs.pirika.argumentFrame.CompareNumber;
import jp.ie.cs.pirika.argumentFrame.Node;

public class Argument {
	/**
	 * initialize.
	 * @param userMap - Map<'String','String'> - userName and ealp file path
	 * @param truthvalue - String
	 * @return
	 */
	private static ProlMain[] init(Map<String, String>userMap,String truthvalue){
		ProlMain[] prol = new ProlMain[userMap.size()];

		Iterator<String> it = userMap.keySet().iterator();
		for(int i=0;it.hasNext();) {
			String userName = it.next();
			String filepath = userMap.get(userName);
			prol[i] = new ProlMain(userName);
			if(prol[i].init(truthvalue, filepath)){
				i++;
			}
		}
		return prol;
	}

	/**
	 * get all attack relation.
	 * <p>
	 * このメソッドはMapで渡された情報からすべての攻撃関係をリスト形式で返します．</br>
	 * ここで得られる結果はすべての知識ベースを一つにまとめてから(複数のエージェントの知識を一つにまとめ，一人のエージェントとして扱う)
	 * Mapに渡した場合と結果が異なる場合があります．</br>
	 * </br>
	 * 論証の生成がエージェント単位で行われるため，知識をまとめた場合，別のエージェントの知識を使って論証が生成される場合があるためです．
	 * </br>
	 * </p>
	 * @param truthvalue - String
	 * @param userMap - Map<'String','String'> - userName and ealp file path
	 * @return List - attack relation
	 */
	public static List<String> allAttack(String truthvalue,Map<String, String>userMap){
		ProlMain[] prol = init(userMap,truthvalue);
		Map<String,String> buf = new HashMap<String,String>();
		Map<String,Node> arg = new TreeMap<String,Node>(new CompareNumber());
		int count=0;
		Boolean debug = false;
		
		Iterator<String> it = userMap.keySet().iterator();
		
		for(int i=0;it.hasNext();i++) {
			List<String>check = new ArrayList<String>();
			String name = it.next();
			List<String>list = readEALP(userMap.get(name));

			for(int j=0;j<list.size();j++){
				String[] s = list.get(j).split("<==");
				if(!check.contains(s[0])){
					check.add(s[0]);
					List<String> argList =  prol[i].getArgumentList(s[0]);
					if (argList!=null) {
						for(int k=0;k<argList.size();k++){
							if(!buf.containsKey(argList.get(k))){
								String c = String.valueOf(count);
								Node n = new Node(c,argList.get(k),name);
								arg.put(c, n);
								buf.put(argList.get(k), c);
								count++;
							}
						}
					}
				}
			}
		}
		
		it = arg.keySet().iterator();
        while (it.hasNext()) {
        	String key = it.next();
            Node r = arg.get(key);
            
            for (int i = 0; i < prol.length; i++) {
            	List<String> defeats = prol[i].defeatList(r.getArgument());
            	if (defeats!=null) {
            		for(int j=0;j<defeats.size();j++){
        				String sym = buf.get(defeats.get(j));
        				Node n = arg.get(sym);
        				r.addRecieveRelation(sym, n);
        				n.addAttackRelation(key, r);
        			}
				}
			}
        }
        
        it = arg.keySet().iterator();
        List<String> attacks = new ArrayList<String>();
        while (it.hasNext()) {
        	String key = it.next();
            Node n = arg.get(key);
            if(debug){
            	System.out.println(key + "/" + n.getRecieveList() + "/" + n.getAttackList() + "/" + n.getArgument());
            }
            
            List<String> l = n.getAttackList();
            for(int i=0;i<l.size();i++){
            	attacks.add(new String("["+key+","+l.get(i)+"]"));
            }
        }
        return attacks;
	}
	
	/**
	 * get node number.</br>
	 * @param truthvalue
	 * @param userMap
	 * @return int
	 */
	public static int getNodeNum(String truthvalue,Map<String, String>userMap){
		ProlMain[] prol = init(userMap,truthvalue);
		int count=0;
		List<String>check2 = new ArrayList<String>();
		Iterator<String> it = userMap.keySet().iterator();
		for(int i=0;it.hasNext();i++) {
			String name = it.next();
			List<String>list = readEALP(userMap.get(name));
			List<String>check = new ArrayList<String>();
			
			for(int j=0;j<list.size();j++){
				String[] s = list.get(j).split("<==");
				if(!check.contains(s[0])){
					check.add(s[0]);
					List<String> argList =  prol[i].getArgumentList(s[0]);
					if (argList!=null) {
						for(int k=0;k<argList.size();k++){
							if (!check2.contains(argList.get(k))) {
								count++;
								check2.add(argList.get(k));
							}
						}
					}
				}
			}
		}
		return count;
	}
	
	/**
	 * get node information.
	 * @param truthvalue
	 * @param userMap
	 * @return
	 */
	public static List<String> nodeInfo(String truthvalue,Map<String, String>userMap){
		ProlMain[] prol = init(userMap,truthvalue);
		Map<String,String> buf = new HashMap<String,String>();
		Map<String,Node> arg = new TreeMap<String,Node>(new CompareNumber());
		int count=0;
		
		Iterator<String> it = userMap.keySet().iterator();
		for(int i=0;it.hasNext();i++) {
			String name = it.next();
			List<String>list = readEALP(userMap.get(name));
			List<String>check = new ArrayList<String>();

			for(int j=0;j<list.size();j++){
				String[] s = list.get(j).split("<==");
				if(!check.contains(s[0])){
					check.add(s[0]);
					List<String> argList =  prol[i].getArgumentList(s[0]);
					if (argList!=null) {
						for(int k=0;k<argList.size();k++){
							if(!buf.containsKey(argList.get(k))){
								String c = String.valueOf(count);
								Node n = new Node(c,argList.get(k),name);
								arg.put(c, n);
								buf.put(argList.get(k), c);
								count++;
							}
						}
					}
				}
			}
		}
		
		it = arg.keySet().iterator();
        while (it.hasNext()) {
        	String key = it.next();
            Node r = arg.get(key);
            
            for (int i = 0; i < prol.length; i++) {
            	List<String> defeats = prol[i].defeatList(r.getArgument());
            	if (defeats!=null) {
            		for(int j=0;j<defeats.size();j++){
        				String sym = buf.get(defeats.get(j));
        				Node n = arg.get(sym);
        				r.addRecieveRelation(sym, n);
        				n.addAttackRelation(key, r);
        			}
				}
			}
        }
        
        it = arg.keySet().iterator();
        List<String> list = new ArrayList<String>();
        while (it.hasNext()) {
        	String key = it.next();
            Node n = arg.get(key);
            list.add(new String(key + "/" + n.getRecieveList() + "/" + n.getAttackList() + "/" + n.getArgument()));
        }
        return list;
	}
	
	/**
	 * get node information.
	 * @param truthvalue
	 * @param userMap
	 * @return
	 */
	public static Map<String,Node> nodeInfoMap(String truthvalue,Map<String, String>userMap){
		ProlMain[] prol = init(userMap,truthvalue);
		Map<String,String> buf = new HashMap<String,String>();
		Map<String,Node> arg = new TreeMap<String,Node>(new CompareNumber());
		int count=0;
		
		Iterator<String> it = userMap.keySet().iterator();
		for(int i=0;it.hasNext();i++) {
			String name = it.next();
			List<String>list = readEALP(userMap.get(name));
			List<String>check = new ArrayList<String>();

			for(int j=0;j<list.size();j++){
				String[] s = list.get(j).split("<==");
				if(!check.contains(s[0])){
					check.add(s[0]);
					List<String> argList =  prol[i].getArgumentList(s[0]);
					if (argList!=null) {
						for(int k=0;k<argList.size();k++){
							if(!buf.containsKey(argList.get(k))){
								String c = String.valueOf(count);
								Node n = new Node(c,argList.get(k),name);
								arg.put(c, n);
								buf.put(argList.get(k), c);
								count++;
							}
						}
					}
				}
			}
		}
		
		it = arg.keySet().iterator();
        while (it.hasNext()) {
        	String key = it.next();
            Node r = arg.get(key);
            
            for (int i = 0; i < prol.length; i++) {
            	List<String> defeats = prol[i].defeatList(r.getArgument());
            	if (defeats!=null) {
            		for(int j=0;j<defeats.size();j++){
        				String sym = buf.get(defeats.get(j));
        				Node n = arg.get(sym);
        				r.addRecieveRelation(sym, n);
        				n.addAttackRelation(key, r);
        			}
				}
			}
        }
        return arg;
	}
	

	/**
	 * EALPファイルを読み込む．
	 * @param filePath
	 * @return 重複を排除したファイル内容の配列．
	 */
	private static List<String> readEALP(String filePath){
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = br.readLine()) != null) {
				// 全角と半角を削除．
				line = line.replaceAll(" ", "");
				line = line.replaceAll("　", "");
				line = line.replaceAll("<==not", "<==not ");
				if(!line.equals("") && !list.contains(line) && !line.startsWith("%")){
					list.add(line);
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static void main(String[] args){
		Map<String,String> map = new HashMap<String,String>();
		map.put("katsura", "./EALP/katsura/01value/climb(mountain).ealp");
		map.put("katsura", "./EALP/katsura/01value/climb(mountain).ealp");
//		map.put("won", "/Users/katsura/Documents/workspace/PIRIKA-Server-2.0/EALP/katsura/01value/climb2.ealp");
		System.out.println(Argument.allAttack("01value", map));
		System.out.println(Argument.getNodeNum("01value", map));
		List<String> list = Argument.nodeInfo("01value", map);
		for (String string : list) {
			System.out.println(string);
		}
	}
}
