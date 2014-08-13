package frame.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;




import frame.argumentation.Argument;
import frame.argumentation.Argumentation;
import frame.argumentation.Relation;
import frame.forceLayout.ForceLayout;
import frame.util.Vector;

/**
 * ノードの情報を出力するパネルを表すクラス。
 * @author Yutaka Omido
 */
public class ResultPanel extends JPanel implements Runnable, MouseListener, MouseMotionListener{
	/**
	 * シリアライズID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * パネル上にある全てのノードを管理する動的配列
	 */
	private Argumentation argumentation;
	/**
	 * マウスクリックによって操作対象となっているノード
	 */
	private int lockedNodeID = -1;
	/**
	 * 現在の表示倍率
	 */
	private double scale = 1.0;
	/**
	 * レイアウトを表示する間隔
	 */
	private double dt = 0.03;
	/**
	 * フォースレイアウトのインスタンス
	 */
	private ForceLayout forceLayout;
	/**
	 * 描画の際のノードの半径
	 */
	private double R = 3.0;
	/**
	 * 矢印の先端部分の長さ
	 */
	private double arrowHead = 5.0;
	/**
	 * 矢印の先端部分の角度
	 */
	private double arrawAngle = 25.0;
	/**
	 * 曲線を描くときのまがり具合
	 */
	private double curveDegree = 25.0;
	/**
	 * ロックされているノードの描画位置
	 */
	Vector lockedPosition = new Vector();
	
	private Thread draw = new Thread(this);

	/**
	 * 引数なしコンストラクタ
	 */
	public ResultPanel() {
		this(new Argumentation());
	}
	/**
	 * コンストラクタ
	 * @param nodes - パネルに表示したいノード
	 */
	public ResultPanel(Argumentation argumentation){
		super();
		this.forceLayout = new ForceLayout();
		this.setArgumentation(argumentation);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}


	public double getDt() {
		return dt;
	}
	public void setDt(double dt) {
		this.dt = dt;
	}
	public double getR() {
		return R;
	}
	public void setR(double r) {
		R = r;
	}
	public double getArrowHead() {
		return arrowHead;
	}
	public void setArrowHead(double arrowHead) {
		this.arrowHead = arrowHead;
	}
	public double getArrawAngle() {
		return arrawAngle;
	}
	public void setArrawAngle(double arrawAngle) {
		this.arrawAngle = arrawAngle;
	}
	public double getCurveDegree() {
		return curveDegree;
	}
	public void setCurveDegree(double curveDegree) {
		this.curveDegree = curveDegree;
	}
	public void setArgumentation(Argumentation argumentation) {
		this.argumentation = argumentation;
		this.forceLayout = new ForceLayout(argumentation);
	}


	/**
	 * スレッド処理
	 */
	public void run(){
		try{
			while(true){
				long sleepTime = (long)(int)(this.argumentation.getArguments().size() * 0.2);
				if(sleepTime < 6) sleepTime = 6L;

				Thread.sleep(sleepTime);
				repaint();
			}
		}catch(InterruptedException e){}
	}

	/**
	 * 描画
	 */
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		//パネルの中心がわかるように線を描画
		g2.draw(new Line2D.Double(0, this.getHeight()/2.0, this.getWidth(), this.getHeight()/2.0));
		g2.draw(new Line2D.Double(this.getWidth()/2.0, 0, this.getWidth()/2.0, this.getHeight()));

		//フォースレイアウトに従ってノード情報を更新
		this.forceLayout.moveAll(this.dt, this.lockedNodeID);
		this.argumentation = forceLayout.getArgumentation();

		//すべてのノードの位置を、図の中心が(0, 0にくるように修正)
		lockDrawing();

		//図をすべてパネルに収めるための表示倍率を求める
		this.scale = getScale();

		//描画
		drawLayout(g2, scale);
	}

	/**
	 * スレッドの処理をスタートさせる(レイアウトを始める)
	 */
	public void startDrawing(){
		draw.start();
	}
	
	public void stopDrawing(){
		draw.interrupt();
		draw=null;
	}

	/**
	 * レイアウトを図示する	
	 * @param g2
	 * @param scale
	 */
	void drawLayout(Graphics2D g2, double scale){
		//関係を描画
		for(int i=0; i<this.argumentation.getArguments().size(); i++){
			for(int j=0; j<=i; j++){
				int argumentID1 = this.argumentation.getArgument(i).getId();
				int argumentID2 = this.argumentation.getArgument(j).getId();

				if(argumentation.haveRelationBetween(argumentID1, argumentID2)){
					ArrayList<Relation> relations = argumentation.relationsBetween(argumentID1, argumentID2);	
					if(!argumentation.interrelatingRelation(argumentID1, argumentID2)){
						if(argumentation.haveRelationTo(argumentID1, argumentID2)){
							//相手のノードとの相互関係はないが、argumrnt1からの関係が存在する場合
							drawOneWayRelations(g2, scale, relations);
						}else if(argumentation.haveRelationTo(argumentID2, argumentID1)){
							//相手のノードとの相互関係はないが、argument2からの関係が存在する場合
							drawOneWayRelations(g2, scale, relations);
						}
					}else{
						if(argumentID1 == argumentID2){
							//相互関係があり、その相手が自分のとき(自己矛盾・自己擁護のとき)
							drawOwnRelations(g2, scale, relations);
						}else{
							//相互関係があるとき
							drawInterrelatedRelations(g2, scale, relations);
						}
					}
				}
			}
		}
		//各ノードを描画
		for(int i=0; i<argumentation.getArguments().size(); i++){
			drawNode(argumentation.getArgument(i), g2, scale, this.getWidth(), this.getHeight());		
		}
	}

	/**
	 * ノードを描画する
	 * @param g2
	 * @param scale - 表示倍率
	 * @param width - コンポーネントの幅
	 * @param height - コンポーネントの高さ
	 */
	public void drawNode(Argument node, Graphics2D g2, double scale, int width, int height){
		Ellipse2D.Double ellipse;
		if(node.getId() != lockedNodeID){
			//コンポーネントの中心を求める
			double centerX = width  / 2.0;
			double centerY = height / 2.0;
			//このノードの座標をコンポーネントの中心へ
			double thisX = (node.getPosition().getX() * scale) + centerX;
			double thisY = (node.getPosition().getY() * scale) + centerY;
			ellipse = new Ellipse2D.Double(thisX-(this.R*scale), thisY-(this.R*scale), 2*this.R*scale, 2*this.R*scale);
		}else{
			ellipse = new Ellipse2D.Double(lockedPosition.getX()-(this.R*scale), lockedPosition.getY()-(this.R*scale), 2*this.R*scale, 2*this.R*scale);
		}
		//このノードの描画
		if(node.getId() == lockedNodeID) g2.setColor(Color.GREEN);
		else                             g2.setColor(Color.BLACK);
		g2.fill(ellipse);
	}

	/**
	 * argument1からargument2へ向かう矢印つきの曲線をを描画する
	 * @param x1 - 始点ｘ座標
	 * @param y1 - 始点ｙ座標
	 * @param x2 - 終点ｘ座標
	 * @param y2 - 終点ｙ座標
	 * @param scale - 表示倍率
	 * @param g2 - Graphics2Dの描画用インスタンス
	 * @param degree - どのくらい膨らんだカーブを描くか
	 */
	void drawArrow(Relation relation, double scale, Graphics2D g2, int degree){
		//描画の位置を求める
		double panelCenterX = this.getWidth()  / 2.0;
		double panelCenterY = this.getHeight() / 2.0;
		double x1;
		double y1;	
		double x2;
		double y2;
		Argument argument1 = argumentation.getArgument(relation.getFrom());
		Argument argument2 = argumentation.getArgument(relation.getTo());

		if(argument1.getId() != lockedNodeID){
			x1 = (argument1.getPosition().getX() * scale) + panelCenterX;
			y1 = (argument1.getPosition().getY() * scale) + panelCenterY;	
		}else{
			x1 = lockedPosition.getX();
			y1 = lockedPosition.getY();				
		}
		if(argument2.getId() != lockedNodeID){
			x2 = (argument2.getPosition().getX() * scale) + panelCenterX;
			y2 = (argument2.getPosition().getY() * scale) + panelCenterY;
		}else{
			x2 = lockedPosition.getX();
			y2 = lockedPosition.getY();				
		}

		//2点を結ぶ直線の角度を求める
		double angle = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
		if(angle < 0) angle += 360;

		//(法線ベクトル) * (基準を起きたい場所までの長さ)
		double dx = (x2 - x1);
		double dy = (y2 - y1);
		double length = Math.sqrt((dx * dx) + (dy * dy));

		Vector n = new Vector(dy / length * this.curveDegree * degree, -dx / length * this.curveDegree * degree);

		//カーブの基準点
		double x3 = (x1 + x2) / 2.0;
		double y3 = (y1 + y2) / 2.0;

		//曲線のどまんなか
		double curveCenterX = (0.25 * x1) + (0.5 * (x3 + n.getX())) + (0.25 * x2); 
		double curveCenterY = (0.25 * y1) + (0.5 * (y3 + n.getY())) + (0.25 * y2);

		//曲線描画
		QuadCurve2D curve1 = new QuadCurve2D.Double(x1, y1, x3 + n.getX(), y3 + n.getY(), x2, y2);
		g2.setStroke(relation.getStroke());
		g2.setColor(relation.getColor());
		g2.draw(curve1);

		//矢印描画
		Line2D line1 = new Line2D.Double(curveCenterX,
				curveCenterY,
				curveCenterX - (arrowHead * scale) * Math.cos(Math.toRadians(angle - arrawAngle)),
				curveCenterY - (arrowHead * scale) * Math.sin(Math.toRadians(angle - arrawAngle)));
		Line2D line2 = new Line2D.Double(curveCenterX,
				curveCenterY,
				curveCenterX - (arrowHead * scale) * Math.cos(Math.toRadians(angle + arrawAngle)),
				curveCenterY - (arrowHead * scale) * Math.sin(Math.toRadians(angle + arrawAngle)));

		g2.setStroke(relation.getArrowStroke());
		g2.draw(line1);
		g2.draw(line2);
	}

	/**
	 * 相互攻撃がない場合の関係描画処理
	 * @param g2
	 * @param scale - 表示倍率
	 * @param argument1 - 矢印の出発点となるノード
	 * @param argument2 - 矢印の終点となるノード
	 */
	void drawOneWayRelations(Graphics2D g2, double scale, ArrayList<Relation> relations){
		//関係の数を数える
		int num = relations.size();

		//関係の数に応じて、矢印をどのように描くか決める
		ArrayList<Integer> arrowPropertys = new ArrayList<Integer>();
		for(int i=-(num/2); i<0; i++) arrowPropertys.add(i);
		if((num % 2) == 1) arrowPropertys.add(0);
		for(int i=1; i<=(num/2); i++) arrowPropertys.add(i);

		//描画
		for(int i=0; i<relations.size(); i++){
			Relation relation = relations.get(i);
			drawArrow(relation,
					scale,
					g2,
					arrowPropertys.get(i));
		}
	}

	/**
	 * 自己矛盾・自己擁護の場合の描画処理
	 * @param g2
	 * @param scale - 表示倍率
	 * @param node - 対称のノード
	 */
	void drawOwnRelations(Graphics2D g2, double scale, ArrayList<Relation> relations){
		//矢印を描画
		for(int i=0; i<relations.size(); i++){
			Relation relation = relations.get(i);
			drawOwnRelation(relation, scale, g2, i);
		}
	}

	/**
	 * 引数で与えられたノードの自己矛盾、自己擁護を描画
	 */
	void drawOwnRelation(Relation relation, double scale, Graphics2D g2, int degree){
		//描画の位置を求める
		double centerX = this.getWidth()  / 2.0;
		double centerY = this.getHeight() / 2.0;
		double x;
		double y;
		Argument argument = argumentation.getArgumentByID(relation.getFrom());
		if(relation.getFrom() != lockedNodeID){
			x = (argument.getPosition().getX() * scale) + centerX;
			y = (argument.getPosition().getY() * scale) + centerY;	
		}else{
			x = lockedPosition.getX();
			y = lockedPosition.getY();				
		}

		//円を描画
		double width = 4; //複数表示するときの幅
		double r = (6 + (width * degree)); //半径
		Ellipse2D.Double ownRlation = new Ellipse2D.Double(x - (r * scale),
				y - (2.0 * r * scale),
				2.0 * r * scale,
				2.0 * r * scale);
		g2.setStroke(relation.getStroke());
		g2.setColor(relation.getColor());
		g2.draw(ownRlation);

		//矢印描画
		Line2D line1 = new Line2D.Double(x + (3.0 * scale),
				y - (2.0 * r * scale),
				x + (3.0 * scale)- (arrowHead * scale) * Math.cos(Math.toRadians(-arrawAngle)),
				y - (2.0 * r * scale) - (arrowHead * scale) * Math.sin(Math.toRadians(-arrawAngle)));
		Line2D line2 = new Line2D.Double(x + (3.0 * scale),
				y - (2.0 * r * scale),
				x + (3.0 * scale)- (arrowHead * scale) * Math.cos(Math.toRadians(arrawAngle)),
				y - (2.0 * r * scale) - (arrowHead * scale) * Math.sin(Math.toRadians(arrawAngle)));
		g2.setStroke(relation.getArrowStroke());
		g2.draw(line1);
		g2.draw(line2);
	}

	/**
	 * 相互攻撃がある場合の描画処理
	 * @param g2
	 * @param scale - 表示倍率
	 * @param node1 - ノードその1
	 * @param node2 - ノードその2
	 */
	void drawInterrelatedRelations(Graphics2D g2, double scale, ArrayList<Relation> relations){		
		int argument1ID = relations.get(0).getFrom();

		ArrayList<Relation> relations1 = new ArrayList<Relation>();
		ArrayList<Relation> relations2 = new ArrayList<Relation>();

		//攻撃の方向ごとに関係をわける
		for(int i=0; i<relations.size(); i++){
			Relation relation = relations.get(i);
			if(relation.getFrom() == argument1ID){
				relations1.add(relation);
			}else{
				relations2.add(relation);
			}
		}

		for(int i=0; i<relations1.size(); i++){
			Relation relation = relations1.get(i);
			drawArrow(relation, scale, g2, i+1);
		}
		for(int i=0; i<relations2.size(); i++){
			Relation relation = relations2.get(i);
			drawArrow(relation, scale, g2, i+1);
		}
	}

	/**
	 * 図を固定する(図の中心をパネルの中心に持ってくる)
	 */
	void lockDrawing(){
		//一番左、上、右、下のノード
		double upper =  10000;
		double lower = -10000;
		double left =   10000;
		double right = -10000;

		//一番左、上、右、下のノードを探す
		for(int i=0; i<argumentation.getArguments().size(); i++){
			Argument compared = argumentation.getArgument(i); 
			if(compared.getPosition().getY() > lower) lower = compared.getPosition().getY();
			if(compared.getPosition().getY() < upper) upper = compared.getPosition().getY();
			if(compared.getPosition().getX() < left) left = compared.getPosition().getX();
			if(compared.getPosition().getX() > right) right = compared.getPosition().getX();
		}

		//図の中心を求める
		double centerX = (right + left) / 2.0;
		double centerY = (upper + lower) / 2.0;

		//図の中心を(0, 0)に持っていく
		for(int i=0; i<argumentation.getArguments().size(); i++){
			Argument node = argumentation.getArgument(i);
			//ノードがロックされているときは動かさない
			if(node.getId() != lockedNodeID) node.setPosition(new Vector(node.getPosition().getX()-centerX, node.getPosition().getY()-centerY));
		}
	}

	/**
	 * 図がすべてパネルに収まるように拡大縮小する倍率を決める
	 */
	double getScale(){
		//一番左、上、右、下のノード
		double upper = 10000;
		double lower = 0;
		double left = 10000;
		double right = 0;

		//一番左、上、右、下のノードを探す
		for(int i=0; i<argumentation.getArguments().size(); i++){
			Argument compared = argumentation.getArgument(i); 
			if(compared.getPosition().getY() > lower) lower = compared.getPosition().getY();
			if(compared.getPosition().getY() < upper) upper = compared.getPosition().getY();
			if(compared.getPosition().getX() < left)  left  = compared.getPosition().getX();
			if(compared.getPosition().getX() > right) right = compared.getPosition().getX();
		}

		//等倍で表示した場合に必要なパネルの大きさ
		double scaleWidth  = (right - left)  + 30.0;
		double scaleHeight = (lower - upper) + 30.0;

		//全体が収まるような倍率を求める
		scaleWidth  = this.getWidth()  / scaleWidth;
		scaleHeight = this.getHeight() / scaleHeight;
		double scale = ((scaleWidth < scaleHeight) ? scaleWidth : scaleHeight);
		return scale;
	}	
	/**
	 * 関係の描画に用いる色を得る
	 */
	Color getColor(Relation relation){
		if((relation.getType() == Relation.ORG_ATTACK) || 
				(relation.getType() == Relation.CGN_ATTACK) ||
				(relation.getType() == Relation.DYAD_ATTACK)){
			return Color.RED;
		}else{
			return Color.BLUE;
		}
	}

	/**
	 * 与えられた座標に一番近いノードのインデックスを求める
	 * @param x - x座標(スケールされていない状態の)
	 * @param y - ｙ座標(スケールされていない状態の)
	 * @return  - 一番近いノードのインデックス
	 */
	Argument getNearestNode(double x, double y){
		int nearest=0;
		double minimumDistance = 100000;

		for(int i=0; i<argumentation.getArguments().size(); i++){
			//距離を求め、一番近いノードのインデックスをnearestとする
			double d = argumentation.getArgument(i).getDistance(x, y);
			if(d <= minimumDistance){
				nearest = i;
				minimumDistance = d;
			}
		}
		return argumentation.getArgument(nearest);	
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("click");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//System.out.println("enterd");		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//System.out.println("exit");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//クリックをした場所を取得し、それを実際の座標に戻す
		double clickedX = (e.getPoint().getX() - (this.getWidth()  / 2.0)) / scale;
		double clickedY = (e.getPoint().getY() - (this.getHeight() / 2.0)) / scale;

		//最もクリックした場所に近いノードを探し、ロックする
		lockedPosition.setVector(e.getPoint().getX(), e.getPoint().getY());
		this.lockedNodeID = getNearestNode(clickedX, clickedY).getId();
		this.argumentation.getArgumentByID(lockedNodeID).setPosition(new Vector(clickedX, clickedY));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		lockedNodeID = -1;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//System.out.println("drugging");
		if(lockedNodeID != -1){
			//現在のマウスポインタの場所を取得し、それを実際の座標に戻す
			lockedPosition.setVector(e.getPoint().getX(), e.getPoint().getY());
			double drugedX = (e.getPoint().getX() - (this.getWidth()  / 2.0)) / scale;
			double drugedY = (e.getPoint().getY() - (this.getHeight() / 2.0)) / scale;
			argumentation.getArgumentByID(lockedNodeID).setPosition(new Vector(drugedX, drugedY));
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//フレーム・パネルの設定
		JFrame mainFrame = new JFrame("Result Panel Test");
		//		mainFrame.setSize(500, 400);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ResultPanel panel = new ResultPanel();
		panel.setPreferredSize(new Dimension(500, 400));
		mainFrame.getContentPane().add(panel);
		mainFrame.pack();
		//議論の作成
		int argumentNum = 5;
		int relationNum = 10;		
		Random rand = new Random(System.currentTimeMillis());

		Argumentation argumentation = new Argumentation();
		for(int i=0; i<argumentNum; i++) argumentation.addArgument(new Argument());
		for(int i=0; i<relationNum; i++) argumentation.addRelation(new Relation(rand.nextInt(argumentNum), rand.nextInt(argumentNum), rand.nextInt(6)));

		//論証として登録
		panel.setArgumentation(argumentation);

		Thread thread = new Thread(panel);
		thread.start();
	}
}
