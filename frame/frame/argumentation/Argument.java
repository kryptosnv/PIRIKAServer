package frame.argumentation;

import frame.util.Vector;

/**
 * 1つの論証を表す
 * @author Yutaka Omido
 */
public class Argument{
	private int id;
	private String data;
	private double strength;
	private Vector position;
	private Vector velocity;
	
	/**
	 * 引数なしのコンストラクタです。
	 * idが-1、dataは空の文字列、strengthは-1にセットされます。
	 */
	public Argument() {
		this(-1, "", -1);
	}
	/**
	 * 受け取ったargumentと同じものを生成します。
	 * ただし、物理的ステータスは受け継がれないので注意が必要です。
	 * @param argument
	 */
	public Argument(Argument argument) {
		this(argument.getId(), argument.getData(), argument.getStrength());
	}
	/**
	 * 受け取ったステータスを持った論証を生成します。
	 * @param id
	 * @param data
	 * @param strength
	 */
	public Argument(String data, double strength) {
		this(-1, data, strength);
	}
	/**
	 * 受け取ったステータスを持った論証を生成します。
	 * @param id
	 * @param data
	 * @param strength
	 */
	public Argument(int id, String data, double strength) {
		this.id       = id;
		this.data     = data;
		this.strength = strength;
		this.position = new Vector();
		this.velocity = new Vector();
	}

	public String toString(){
		return "id       : " + this.id + "\n" +
			   "data     : " + this. data + "\n" +
			   "strength : " + this.strength;
	}
	
	/**
	 * @return id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @return data
	 */
	public String getData() {
		return data;
	}
	/**
	 * @return strength
	 */
	public double getStrength() {
		return strength;
	}
	/**
	 * @return position
	 */
	public Vector getPosition() {
		return position;
	}
	/**
	 * @return velocity
	 */
	public Vector getVelocity() {
		return velocity;
	}

	/**
	 * @param id セットする id
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @param data セットする data
	 */
	public void setData(String data) {
		this.data = data;
	}
	/**
	 * @param strength セットする strength
	 */
	public void setStrength(double strength) {
		this.strength = strength;
	}
	/**
	 * @param position セットする position
	 */
	public void setPosition(Vector position) {
		this.position = position;
	}
	/**
	 * @param velocity セットする velocity
	 */
	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	/**
	 * (x, y)との距離を求めます。
	 * @param x
	 * @param y
	 * @return
	 */
	public double getDistance(double x, double y){
		double dx = this.getPosition().getX() - x;
		double dy = this.getPosition().getY() - y;
		
		return Math.sqrt((dx * dx) + (dy * dy));
	}
}
