package frame.util;

/**
 * ベクトルを表現するクラス
 * @author Yutaka Omido
 */
public class Vector {
	//変数
	private double x=0;
	private double y=0;
	
	//コンストラクタ
	/**
	 * 引数なしコンストラクタ
	 */
	public Vector(){
		this.x = 0;
		this.y = 0;
	}
	/**
	 * 引数ありコンストラクタ
	 * @param x　- x座標
	 * @param y - y座標
	 */
	public Vector(double x, double y){
		this.x = x;
		this.y = y;
	}
	/**
	 * ベクトルを引数とするコンストラクタ
	 * @param vct - 代入するベクトル
	 */
	public Vector(Vector vct){
		this.x = vct.getX();
		this.y = vct.getY();
	}

	//ゲッタ
	public double getX(){ return x; }
	public double getY(){ return y; }
	
	//セッタ
	public void setX(double x){ this.x = x; }
	public void setY(double y){ this.y = y; }
	public void setVector(double x, double y){
		setX(x);
		setY(y);
	}
	public void setVector(Vector vct){
		setVector(vct.getX(), vct.getY());
	}

	//メソッド
	/**
	 * このベクトルに、引数で受け取ったベクトルvctを足す
	 * @param vct -このベクトルと足したいベクトル
	 */
	public void plusVector(Vector vct){
		this.x += vct.getX();
		this.y += vct.getY();
	}
	
	/**
	 * このベクトルの長さを返す
	 */
	public double getLength(){
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
}
