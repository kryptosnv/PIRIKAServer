package frame.argumentation;

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * 関係を表すクラスです。
 * fromとtoという2つの値を保持し、fromからtoへの関係を表します。
 * 関係のタイプは ORG_ATTACK, ORG_SUPPORT, CGN_ATK, CGN_SUPPORT, DYAD_ATTACK, DYAD_SUPPORTと6種類あります。
 * @author Yutaka Omido
 */
public class Relation {
	
	public static final int NO_TYPE      = -1;
	public static final int ORG_ATTACK   =  0;
	public static final int ORG_SUPPORT  =  1;
	public static final int CGN_ATTACK   =  2;
	public static final int CGN_SUPPORT  =  3;
	public static final int DYAD_ATTACK  =  4;
	public static final int DYAD_SUPPORT =  5;
	
	private int from;
	private int to;
	private int type;
	
	private static float dash[] = { 5.0f, 4.0f };
	private static BasicStroke defaultStroke    = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f);
	private static BasicStroke orgStroke        = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f);
	private static BasicStroke cgnStroke        = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
	private static BasicStroke dyadStroke       = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f);
	private static BasicStroke thinArrowStroke  = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f);
	private static BasicStroke thickArrowStroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f);
	
	private static Color defaultColor = Color.BLACK;
	private static Color attackColor  = Color.RED;
	private static Color supportColor = Color.BLUE;
	
	/**
	 * 引数なしのコンストラクタです。
	 * fromとtoはどちらも-1がセットされ、
	 * ノードタイプはRelaiton.NO_TYPEになります。
	 */
	public Relation(){
		this(-1, -1);
	}
	/**
	 * fromとtoを指定するコンストラクタです。
	 * ノードタイプはRelaiton.NO_TYPEになります。
	 * @param from
	 * @param to
	 */
	public Relation(int from, int to){
		this(from, to, NO_TYPE);
	}
	/**
	 * from,to,ノードタイプの全てを指定するコンストラクタです。
	 * @param from
	 * @param to
	 * @param type
	 */
	public Relation(int from, int to, int type){
		this.from = from;
		this.to   = to;
		this.type = type;
	}
	
	public String toString(){
		return "fromArgumentID : " + this.from + "¥n" +
			   "toArgumentID   : " + this.to;
	}
	
	/**
	 * @return from
	 */
	public int getFrom() {
		return from;
	}
	/**
	 * @return to
	 */
	public int getTo() {
		return to;
	}
	/**
	 * @return type
	 */
	public int getType() {
		return type;
	}
	/**
	 * この関係の色を得る
	 */
	public Color getColor(){
		if((this.type == ORG_ATTACK) || (this.type == CGN_ATTACK) || (this.type == DYAD_ATTACK)){
			return attackColor;
		}else if((this.type == ORG_SUPPORT) || (this.type == CGN_SUPPORT) || (this.type == DYAD_SUPPORT)){
			return supportColor;
		}else{
			return defaultColor;
		}
	}
	/**
	 * 関係描画の際のStrokeを得る
	 * @return
	 */
	public BasicStroke getStroke(){
		if((this.type == ORG_ATTACK) || (this.type == ORG_SUPPORT)){
			return orgStroke;
		}else if((this.type == CGN_ATTACK) || (this.type == CGN_SUPPORT)){
			return cgnStroke;
		}else if((this.type == DYAD_ATTACK) || (this.type == DYAD_SUPPORT)){
			return dyadStroke;
		}else{
			return defaultStroke;
		}
	}
	/**
	 * 関係描画の際の矢印のStrokeを得る
	 */
	public BasicStroke getArrowStroke(){
		if((this.type == DYAD_ATTACK) || (this.type == DYAD_SUPPORT)){
			return thickArrowStroke;
		}else{
			return thinArrowStroke;
		}
	}
	/**
	 * @param orgStroke セットする orgStroke
	 */
	public static void setOrgStroke(BasicStroke orgStroke) {
		Relation.orgStroke = orgStroke;
	}
	/**
	 * @param cgnStroke セットする cgnStroke
	 */
	public static void setCgnStroke(BasicStroke cgnStroke) {
		Relation.cgnStroke = cgnStroke;
	}
	/**
	 * @param dyadStroke セットする dyadStroke
	 */
	public static void setDyadStroke(BasicStroke dyadStroke) {
		Relation.dyadStroke = dyadStroke;
	}
	/**
	 * @param thinArrowStroke セットする thinArrowStroke
	 */
	public static void setThinArrowStroke(BasicStroke thinArrowStroke) {
		Relation.thinArrowStroke = thinArrowStroke;
	}
	/**
	 * @param thickArrowStroke セットする thickArrowStroke
	 */
	public static void setThickArrowStroke(BasicStroke thickArrowStroke) {
		Relation.thickArrowStroke = thickArrowStroke;
	}
	/**
	 * セットします from
	 */
	public void setFrom(int from) {
		this.from = from;
	}
	/**
	 * @param セットします to
	 */
	public void setTo(int to) {
		this.to = to;
	}
	/**
	 * @param セットします type
	 */
	public void setType(int type) {
		this.type = type;
	}
}
