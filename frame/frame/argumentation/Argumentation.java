package frame.argumentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 議論を表すクラスです。
 * @author Yutaka Omido
 */
public class Argumentation {
	
	private ArrayList<Argument> arguments;
	private ArrayList<Relation> relations;
	public Map<Integer, ArrayList<Relation>> map = new HashMap<Integer, ArrayList<Relation>>();
	private int idCount = 0;
	
	/**
	 * 引数なしのコンストラクタです。
	 * 論証集合と関係集合が空の議論を生成します。
	 */
	public Argumentation(){
		arguments = new ArrayList<Argument>();
		relations = new ArrayList<Relation>();
	}

	/**
	 * 指定されたindexの論証を返します
	 * @param id
	 * @return
	 */
	public Argument getArgument(int index){
		return arguments.get(index);
	}
	/**
	 * 指定されたIDの論証を返します。
	 * ない場合はnullを返します。
	 * @param index
	 * @return
	 */
	public Argument getArgumentByID(int id){
		for(int i=0; i<arguments.size(); i++){
			Argument argument = arguments.get(i);
			if(id == argument.getId()){
				return argument;
			}
		}
		return null;
	}
	/**
	 * 指定されたデータを持つ論証を返します
	 */
	public Argument getArgumentByData(String data){
		for(int i=0; i<arguments.size(); i++){
			Argument argument = arguments.get(i);
			if(data.equals(argument.getData())){
				return argument;
			}
		}
		return null;
	}
	/**
	 * 論証を全て返します。
	 */
	public ArrayList<Argument> getArguments(){
		return arguments;
	}
	/**
	 * indexで指定した関係を返します。
	 * ない場合はnullを返します。
	 * @param index
	 * @return
	 */
	public Relation getRelation(int index){
		return relations.get(index);
	}
	/**
	 * Idを用いて指定された関係を返します
	 */
	public Relation getRelationByID(int fromID, int toID){
		for(int i=0; i<relations.size(); i++){
			Relation relation = relations.get(i);
			if((relation.getFrom() == fromID) && (relation.getTo() == toID)){
				return relation;
			}
		}
		return null;
	}
	/**
	 * 関係をすべて返します
	 */
	public ArrayList<Relation> getRelations(){
		return relations;
	}
	
	/**
	 * この議論に新たな議論を追加します。
	 * ただし、IDはこの議論上のIDに書き換えられます。
	 */
	public void addArgument(Argument argument){
		argument.setId(idCount++);
		arguments.add(argument);
		updateMap();
	}
	/**
	 * この議論に新たな関係を追加します。
	 */
	public void addRelation(Relation relation){
		relations.add(relation);
		updateMap();
	}
	/**
	 * 議論をクリアします。
	 */
	public void refresh(){
		arguments.clear();
		relations.clear();
		idCount = 0;
	}

	/**
	 * mapを更新
	 */
	private void updateMap(){
		Map<Integer, ArrayList<Relation>> map = new HashMap<Integer, ArrayList<Relation>>();
		for(int i=0; i<arguments.size(); i++){
			map.put(arguments.get(i).getId(), new ArrayList<Relation>());
		}
		for(int i=0; i<relations.size(); i++){
			Relation relation = relations.get(i);
			map.get(relations.get(i).getFrom()).add(relation);
		}
		this.map = map;
	}
	
	/**
	 * 関係があるかどうかを調べます。
	 */
	public boolean haveRelationTo(int id1, int id2){
		ArrayList<Relation> relations = map.get(id1);
		for(int i=0; i<relations.size(); i++){
			if(relations.get(i).getTo() == id2){
				return true;
			}
		}
		return false;
	}
	/**
	 * 関係があるかどうかを調べます。
	 */
	public boolean haveRelationBetween(int id1, int id2){
		if(haveRelationTo(id1, id2) || haveRelationTo(id2, id1)) return true;
		return false;
	}
	/**
	 * 関係がいくつあるかを返します
	 */
	public int howManyRelationsTo(int id1, int id2){
		int count = 0;
		for(int i=0; i<relations.size(); i++){
			if(haveRelationTo(id1, id2)) count++;
		}
		return count;
	}
	/**
	 * 2つの論証に関係のあるRelationをListにまとめて返します
	 */
	public ArrayList<Relation> relationsBetween(int id1, int id2){
		ArrayList<Relation> relations = new ArrayList<Relation>();
		for(int i=0; i<this.relations.size(); i++){
			Relation relation = this.relations.get(i);
			if(((id1 == relation.getFrom()) && (id2 == relation.getTo())) ||
			   ((id1 == relation.getTo()) && (id2 == relation.getFrom()))){
				relations.add(this.relations.get(i));
			}
		}
		return relations;
	}
	/**
	 * 2つの論証が相互関係をもつかどうか
	 */
	public boolean interrelatingRelation(int id1, int id2){
		if(haveRelationTo(id1, id2) && haveRelationTo(id2, id1)) return true;
		return false;
	}

	public static void main(String[] args){
		Argumentation argumentation = new Argumentation();
		argumentation.addArgument(new Argument());
		argumentation.addArgument(new Argument());
		argumentation.addRelation(new Relation(0, 1));
	}
}
