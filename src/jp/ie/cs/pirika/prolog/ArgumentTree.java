package jp.ie.cs.pirika.prolog;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.igormaznitsa.prol.exceptions.ParserException;
import com.igormaznitsa.prol.exceptions.ProlCriticalError;
import com.igormaznitsa.prol.exceptions.ProlInstantiationErrorException;

public class ArgumentTree{
	protected ProlMain[] prol;
	protected TreeNode[] tree;

	/**
	 * initialize.
	 * @param truthvalue - String
	 * @param userMap - Map<'String','String'> - userName and ealp file path
	 * @return
	 */
	public void init(String truthvalue, Map<String, String>userMap){
		this.prol = new ProlMain[userMap.size()];

		Iterator<String> it = userMap.keySet().iterator();
		for(int i=0;it.hasNext();) {
			String userName = it.next();
			String filepath = userMap.get(userName);
			this.prol[i] = new ProlMain(userName);
			if(this.prol[i].init(truthvalue, filepath)){
				i++;
			}
		}
	}
	
	/**
	 * The argument tree is calculated.
	 * @param subject
	 * @return List<'String'> - depth,userName,argument
	 */
	public List<String> calculate(String subject) throws ProlCriticalError,ParserException,ProlInstantiationErrorException{
		Map<String,String> demo = new HashMap<String,String>();
		
		for (int i = 0; i < prol.length; i++) {
            ProlMain prolMain = this.prol[i];
			String demoString = prolMain.getArgument(subject);
			if (demoString.equals("[]")) { continue;}
				
			String s = demoString.substring(2, demoString.length()-2);
			String[] demoList = s.split("\\],\\[", 0);
			for(int k=0;k<demoList.length;k++){
				String str = new String('[' + demoList[k] + ']');

				if(!demo.containsKey(str)){
					demo.put(str,prolMain.getName());
				}
			}
        }
		
        tree = new TreeNode[demo.size()];
        Iterator<String> it = demo.keySet().iterator();
        for(int i=0;it.hasNext();i++) {
        	List<String> proList = new LinkedList<String>();
        	String demoString = it.next();
        	String userName = demo.get(demoString);
        	tree[i] = new TreeNode(null, userName, 0 ,demoString);
        	proList.add(demoString);
        	tree[i].nextArgument(proList, this.prol);
        }
        
        List<String> resultList = new ArrayList<String>();
        for(int i=0;i<tree.length;i++){
        	tree[i].insertArgumentWithName(resultList);
        }
        
        return resultList;
	}
	
	public JScrollPane[] setPanel(){
		TreePanel[] panels = new TreePanel[tree.length];
		JScrollPane[] jsp = new JScrollPane[tree.length];
		for (int i = 0; i < tree.length; i++) {
			Dimension d = new Dimension(tree[i].getNodeWidth()*(70+25), tree[i].getMaxDepth()*(70+25)+70);
			panels[i] = new TreePanel(i);
			panels[i].setPreferredSize(d);
			panels[i].setLayout(null);
			
			jsp[i] = new JScrollPane(panels[i]);
			jsp[i].setBackground(null);
		}
		return jsp;
	}
	

	
	private class TreePanel extends JPanel{
		private static final long serialVersionUID = 1L;

		private int num = 0;
		public TreePanel(int num){
			this.num = num;
		}
		
		public void paintComponent(Graphics g){
			TreeNode t = tree[num];
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			t.setPanel(this, new Rectangle(((int)this.getWidth()-70)/2, 70, 70, 70));
			g2.clearRect(0, 0, this.getWidth(), this.getHeight());
			t.drawLine(g);
		}
	}

	public static void main(String[] args){
		ArgumentTree argumentTree = new ArgumentTree();
		Map<String,String> map = new HashMap<String,String>();
		map.put("katsura", "/Users/katsura/Documents/workspace/PIRIKA-Server-2.0/EALP/katsura/01value/climb(we,mountain).ealp");
//		map.put("won", "/Users/katsura/Documents/workspace/PIRIKA-Server-2.0/EALP/katsura/01value/climb2.ealp");
		argumentTree.init("01value", map);
		System.out.println(argumentTree.calculate("climb(we,mountain)::[0.7]"));
		JScrollPane[] jsp = argumentTree.setPanel();
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setTitle("JFrameTest");
		jf.setVisible(true);
		jf.getContentPane().add(jsp[0]);
		jf.pack();
	}
}
