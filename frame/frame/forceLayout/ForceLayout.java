package frame.forceLayout;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

import frame.util.Vector;
import frame.argumentation.*;

/**
 * フォースレイアウトに従い、物理演算によるレイアウトを行うクラス
 * @author Yutaka Omido
 */
public class ForceLayout extends JPanel{
	private static final long serialVersionUID = 1L;
	private Argumentation argumentaiton;
	/**
	 * ばねの自然長を表す
	 */
	static double naturalLength = 35;
	/**
	 * ばね定数
	 */
	static double k = 1.5;
	/**
	 * 空気抵抗を求める際の比例定数
	 */
	static double m = 0.5;
	/**
	 * 斥力を求める際の定数
	 */
	static double g = 3000.0;
	
	/**
	 * コンストラクタ
	 */
	public ForceLayout(){
		this(new Argumentation());
	}
	/**
	 * 引数で受け取った議論をセットする
	 */
	public ForceLayout(Argumentation argumentation){
		super();
		this.argumentaiton = argumentation;
	}
	
	/**
	 * 引数iで指定されたノードを返す
	 */
	public Argument getArgument(int i){ return this.argumentaiton.getArgument(i); }
	/**
	 * ノードを全て返す
	 */
	public ArrayList<Argument> getArguments(){ return this.argumentaiton.getArguments(); }
	/**
	 * 関係をすべて返す
	 */
	public ArrayList<Relation> getRelations(){ return this.argumentaiton.getRelations(); }
	
	/**
	 * 引数iで指定された関係を返す
	 * @param i
	 * @return
	 */
	public Relation getRelation(int i){ return this.argumentaiton.getRelation(i); }
	
	/**
	 * レイアウト上の議論を返す
	 */
	public Argumentation getArgumentation(){ return argumentaiton; }
	/**
	 * forceLayoutを初期化(ノードの登録されていない状態)に戻す
	 */
	public void refresh(){
		this.argumentaiton.refresh();
	}


	/**
	 * ノードすべてをdtミリ秒分動かす
	 */
	public void moveAll(double dt, int lockedNodeID){
		for(int i=0; i<getArguments().size(); i++){
			Argument node1 = getArguments().get(i);
			Vector f = new Vector();
			//力の合計を計算
			for(int j=0; j<getArguments().size(); j++){
				if(i != j){
					Argument node2 = getArguments().get(j);
					//力の合計を計算
					if((argumentaiton.howManyRelationsTo(node1.getId(), node2.getId()) > 0) ||
					   (argumentaiton.howManyRelationsTo(node2.getId(), node1.getId()) > 0)){
						f.plusVector(getSpringForce(node1, node2));
					}
					f.plusVector(getRepulsiveForce(node1, node2));
				}
			}
			f.plusVector(getAirResistance(node1));
			
			//動かす
			if(node1.getId() != lockedNodeID){
				moveByMotionEquation(node1, f, dt);
			}
		}
	}
	

	/**
	 * 引数で与えられたノードの間のバネの弾性力を求める
	 * @return 弾性力
	 */
	public Vector getSpringForce(Argument node1, Argument node2){
		//このバネの現在の長さを求め、今自然長からどのくらい伸びているのかを計算
		double dx = node1.getPosition().getX() - node2.getPosition().getX();
		double dy = node1.getPosition().getY() - node2.getPosition().getY();		
		double d = Math.sqrt(dx * dx + dy * dy);
		
		//距離が0ならば、乱数で決定（例外処理）
		if(d <= 0.5){
			Random random = new Random();
			return new Vector(random.nextDouble() % 1.0, random.nextDouble() % 1.0);
		}
		
		//cos sin　を求める
		double cos = dx / d;
		double sin = dy / d;
		
		return new Vector(-k * (d - naturalLength) * cos, -k * (d - naturalLength) * sin);
	}
	
	/**
	 * 引数で与えられたノードの間の斥力を計算する
	 * @param node - 斥力を与えるノード
	 * @return - 斥力
	 */
	public Vector getRepulsiveForce(Argument node1, Argument node2){
		//ノード間の距離を調べる
		double dx = node1.getPosition().getX() - node2.getPosition().getX();
		double dy = node1.getPosition().getY() - node2.getPosition().getY();		
		double d = Math.sqrt(dx * dx + dy * dy);
		
		//距離がかなり小さいならば、乱数で決定（例外処理）
		if(d <= 0.5){
			Random random = new Random();
			return new Vector(random.nextDouble()*10.0, random.nextDouble()*10.0);
		}
		
		//cos sinを求める
		double cos = dx / d;
		double sin = dy / d;
		
		//斥力は距離の2乗に反比例する
		return new Vector(g * cos / (d * d), g * sin /(d * d));
	}
	
	/**
	 * 運動方程式に従って、dtミリ秒間動かす。ノードの質量は1として計算する。
	 * @param node - 動かすノード
	 * @param f  - このノードにかかっている力
	 * @param dt - ここに設定された分の時間だけ進む
	 */
	public void moveByMotionEquation(Argument node, Vector f, double dt){
		Vector a = f; //加速度、m=1なのでa=f
		
		//現在の速さの更新
		node.getVelocity().setVector(node.getVelocity().getX()+(a.getX()*dt),
				                     node.getVelocity().getY()+(a.getY()*dt));
		//現在の位置の更新(ただし、速度が十分小さいときにはノードは動かない)
		node.getPosition().setVector(node.getPosition().getX()+(node.getVelocity().getX()*dt),
									 node.getPosition().getY()+(node.getVelocity().getY()*dt));
	}
	
	/**
	 * 空気抵抗を求める
	 * @return 空気抵抗
	 */
	public Vector getAirResistance(Argument node){
		return new Vector(-m*node.getVelocity().getX(), -m*node.getVelocity().getY());
	}
}
