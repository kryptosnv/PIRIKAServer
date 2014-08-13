package jp.ie.cs.pirika.prolog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * The class which forms the argument tree.
 * <br>
 * @author katsura
 *
 */
public class TreeNode extends JPanel implements MouseListener, Runnable{
	private static final long serialVersionUID = 1L;

//	private TreeNode prev=null;
	private TreeNode[] next=null;
	private String arg=null;
	private String name=null;
	private int depth;
	
	private int width=0;
	private Rectangle[] nextDem = null;
	private Boolean frag = false;
	private Container container;
	private Thread pThread = null;
	private JScrollPane jsp = new JScrollPane();
	private JLabel nameLabel = null;

	/**
	 * initialize.
	 * @param prev - previous argument pointer.
	 * @param name - user name
	 * @param depth - tree depth
	 * @param arg - argument(demonstration).
	 */
	public TreeNode(TreeNode prev,String name,int depth,String arg){
//		this.prev = prev;
		this.name = name;
		this.depth = depth;
		this.arg = arg;
		this.next = null;
	}

	/**
	 * get this node argument.
	 */
	public String getArgument(){
		return this.arg;
	}

	public int getNodeWidth(){
		return width;
	}
	
	public int getMaxDepth(){
		if (next==null) {
			return depth+1;
		}else{
			int max=0;
			for (int i = 0; i < next.length; i++) {
				TreeNode t = next[i];
				int j = t.getMaxDepth();
				if (max<j) {
					max = j;
				}
			}
			
			return max;
		}
	}

	/**
	 * 論証木を構成する.
	 * <p>
	 * ポインタを用いて議論木を表現する.<br>
	 * 深さ優先で構成し,論証がなくなったら別の同じ深さの論証に処理を移す.
	 * </p>
	 * @param proList - 論証の配列.(重複の確認)
	 * @param ProlMain[].
	 */
	public int nextArgument(List<String> argList, ProlMain[] prolList){
		/**
		 * String - demonstration.</br>
		 * String - userName.
		 */
		Map<String,String> result = new HashMap<String,String>();
		for (int i = 0; i < prolList.length; i++) {
			ProlMain prolMain = prolList[i];

			String s = prolMain.defeat(this.arg);
			if(s.equals("[]")){ continue; }

			String userName = prolMain.getName();
			String str = s.substring(2, s.length()-2);
			String[] defeateList = str.split("\\],\\[", 0);
			for(int j=0;j<defeateList.length;j++){
				String string = new String("["+ defeateList[j] + "]");
				if(!result.containsKey(string)){
					if (argList.contains(string)) {
						// if this node is proponent.
						if (depth%2==0) {
							int count=0;
							for (int k = 0; k < argList.size(); k++) {
								if (argList.get(k).equals(string)) {
									count++;
									if (count>1) {break;}
								}
							}
							if (count>1) {continue;}
						}else{continue;}
					}
					result.put(string,userName);
				}
			}
		}
		if(result.isEmpty()){
			width = 1;
			return width;
		}else{
			next = new TreeNode[result.size()];
		}

		Iterator<String> pro = result.keySet().iterator();
		for(int i=0;pro.hasNext();i++) {
			String arg = pro.next();
			List<String> argList2 = new LinkedList<String>(argList);
			argList2.add(arg);
			next[i] = new TreeNode(this, result.get(arg), depth+1, arg);
			width = width + next[i].nextArgument(argList2,prolList);
		}
		
		return width;
	}

	/**
	 *	配列に結果を保存する.
	 */
	public List<String> insertArgument(List<String> argTree){
		argTree.add(String.valueOf(depth));
		argTree.add(arg);
		for(int i=0;i<this.next.length;i++){
			if(this.next[i]!=null){
				this.next[i].insertArgument(argTree);
			}
		}
		return argTree;
	}

	/**
	 *	配列に結果を保存する.
	 */
	public List<String> insertArgumentWithName(List<String> argTree){
		argTree.add(String.valueOf(this.depth));
		argTree.add(this.name);
		argTree.add(this.arg);
		if(this.next!=null){
			for(int i=0;i<this.next.length;i++){
				if(this.next[i]!=null){
					this.next[i].insertArgumentWithName(argTree);
				}
			}
		}
		return argTree;
	}

	public Container setPanel(Container c, Rectangle r){
		double y = r.getY();
		double w = r.getWidth();
		double h = r.getHeight();
		int space = 25;
		if (this.getSize().width > r.getSize().width) {
			this.run();
//			this.setBounds(r.x,r.y,this.getWidth(),this.getHeight());
//			this.setPreferredSize(this.getSize());
		}else{
//			this.setPreferredSize(r.getSize());
			this.setBounds(r);
		}
		c.add(this);
		this.addMouseListener(this);
		this.container = c;
		double zero = r.getCenterX() - ((w*width + space*(width-1))/2.0);
		if(this.next!=null){
			nextDem = new Rectangle[this.next.length];
			for(int i=0;i<this.next.length;i++){
				int nodewidth = this.next[i].getNodeWidth();
				if(this.next[i]!=null){
					double d = (w * nodewidth + space * (nodewidth-1))/2.0;
					Rectangle rec = new Rectangle((int)(zero + d - w/2.0), (int)(y+space+h), (int)w, (int)h);
					this.next[i].setPanel(c, rec);
					this.nextDem[i] = rec;
					zero = zero + w*nodewidth + space*(nodewidth);
				}
			}
		}
		if(nameLabel==null){
			nameLabel = new JLabel(name);
			this.add(nameLabel,BorderLayout.CENTER);
		}
		
		return c;
	}

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;

		if(depth%2==0){
			g2.setBackground(Color.BLUE);
		}else{
			g2.setBackground(Color.RED);
		}
			g2.clearRect(0, 0, getWidth(), getHeight());
	}

	public void drawLine(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		Rectangle r = this.getBounds();
		double cx = r.getCenterX();
		double cy = r.getCenterY();
		double w = r.getWidth();

		if(nextDem!=null){
			for (int i = 0; i < nextDem.length; i++) {
				Rectangle nr = nextDem[i];
				double nextcx = nr.getCenterX();
				double nexty = nr.getY();
				g2.draw(new Line2D.Double(cx, cy+w/2.0, nextcx, nexty));
				this.next[i].drawLine(g);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (pThread==null) {
			pThread = new Thread(this);
			pThread.start();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void run() {
		int x = 10;
		int y = 2;
		Rectangle r = this.getBounds();
		int rx = r.x;
		int ry = r.y;
		int rw = r.width;
		int rh = r.height;
		this.container.setComponentZOrder(this, 0);
		
		if (frag) {
			jsp.removeAll();
			this.remove(jsp);
			jsp = null;
			while(true){
				rx = rx+(int)(x/2.0);
				ry = ry+(int)(y/2.0);
				rw = rw - x;
				rh = rh - y;
				/*
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
				
				if (rw<r.width/4) {
					break;
				}
			}
			this.setBounds(rx,ry,rw,rh);
			frag = false;
			this.validate();
			this.repaint();
		}else{
			String sub = arg.substring(1, arg.length()-1);
//			sub = sub.replace(" ", "");
//			sub = sub.replace("not", "not ");
			String[] base = sub.split("],", 0);
			JTextArea jta = new JTextArea();
//			jta.setForeground(Color.BLACK);
			jta.setEnabled(false);
			for(int i=0;i<base.length;i++){
				if (i!=base.length-1) {
					base[i] = new String(base[i]+"]");
				}			
				for(int k=0;k<i;k++){
					jta.append("   ");
				}
				jta.append(base[i]+"\n");
			}
			
			while(true){
				rx = rx-(int)(x/2.0);
				ry = ry-(int)(y/2.0);
				rw = rw + x;
				rh = rh + y;
				this.setBounds(rx,ry,rw,rh);
				this.repaint();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (rw>r.width*4) {
					break;
				}
			}
			frag = true;
			jsp = new JScrollPane(jta);
			jsp.setPreferredSize(this.getSize());
//			jsp.setViewportView(jta);
			jsp.setBackground(null);
			this.add(jsp,BorderLayout.CENTER);
			this.validate();
			container.validate();
		}
		pThread = null;
		this.repaint();
	}
}
