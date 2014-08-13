package jp.ie.cs.pirika.argumentFrame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class Node {
	private TreeMap<String, Node> attack = new TreeMap<String, Node>(new CompareNumber());
	private TreeMap<String, Node> recieve = new TreeMap<String, Node>(new CompareNumber());
	
	private String arg;
	private String symbol;
	private String name;
	
	public Node(String symbol,String arg, String name){
		this.symbol = symbol;
		this.arg = arg;
		this.name = name;
	}
	
	public Node(String symbol,String arg){
		this.symbol = symbol;
		this.arg = arg;
	}
	
	/**
	 * 攻撃関係の追加(攻撃する側)
	 * @param sym
	 * @param n
	 */
	public void addAttackRelation(String sym, Node n){
		attack.put(sym, n);
	}
	
	/**
	 * 攻撃関係の追加(攻撃される側)
	 * @param sym
	 * @param n
	 */
	public void addRecieveRelation(String sym ,Node n){
		recieve.put(sym, n);
	}
	
	/*
	public void setArgument(String arg){
		this.arg = arg;
	}*/
	
	/**
	 * 論証を取得.
	 * @return
	 */
	public String getArgument(){
		return arg;
	}
	/*
	public void setSymbol(String s){
		this.symbol = s;
	}*/
	
	/**
	 * シンボルを取得.
	 * @return
	 */
	public String getSymbol(){
		return symbol;
	}
	
	/**
	 * ユーザ名を取得.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * この論証を攻撃しているリストを得る.
	 * @return ArrayList
	 */
	public List<String> getRecieveList(){
		Iterator<String> it = recieve.keySet().iterator();
		ArrayList<String> l = new ArrayList<String>();
        while (it.hasNext()) {
        	String key = it.next();
        	l.add(key);
        }
        return l;
	}
	
	/**
	 * この論証が攻撃しているリストを得る.
	 * @return ArrayList
	 */
	public List<String> getAttackList(){
		Iterator<String> it = attack.keySet().iterator();
		ArrayList<String> l = new ArrayList<String>();
        while (it.hasNext()) {
        	String key = it.next();
        	l.add(key);
        }
        return l;
	}
}
