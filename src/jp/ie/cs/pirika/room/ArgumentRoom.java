package jp.ie.cs.pirika.room;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jp.ie.cs.pirika.argumentFrame.Node;
import jp.ie.cs.pirika.communication.ConnectSemanticsServer;
import jp.ie.cs.pirika.prolog.Argument;
import jp.ie.cs.pirika.prolog.ArgumentTree;
import frame.argumentation.Argumentation;
import frame.argumentation.Relation;
import frame.gui.ResultPanel;

/**
 * 議論ルームの管理クラス.
 * @author katsura
 */
public class ArgumentRoom extends ConnectSemanticsServer{
	private String subject=null;
	/**
	 * key   - userName</br>
	 * value - ealpFilePath
	 */
	private Map<String,String> filePath = new HashMap<String,String>();
	private List<String> userList = new ArrayList<String>();
	private String truthValue=null;
	private List<String> argumentResult = new ArrayList<String>();
	private ArgumentTree nowTree = null;
	private JFrame nowFrame = null;
	private JFrame nowFramework = null;
    
	/**
	 * 議論結果を返却する.
	 * @return
	 */
	public List<String> getRoomResult(){
		return new ArrayList<String>(argumentResult);
	}
	
	/**
	 * ユーザリストを取得.
	 * @return List - userList.
	 */
	public List<String> getUserList(){
		return new ArrayList<String>(userList);
	}
    
	/**
	 * 真理値を返却.
	 * @return
	 */
	public String getTruthValue(){
		return new String(truthValue);
	}
    
	/**
	 * room を初期化.
	 * @param subject - 議題.
	 * @param truthValue - 真理値.
	 * @param superUser - スーパーユーザ名
	 */
	public ArgumentRoom(String subject,String truthValue,String superUser){
		this.subject = subject;
		this.truthValue = truthValue;
		this.userList.add(superUser);
	}
	
	public ArgumentRoom(Properties props) throws IllegalArgumentException{
		this.loadRoom(props);
	}
	
	/**
	 * ユーザ情報を更新.
	 * @param userName - user名.
	 * @param filePath - ealpFilePath.
	 */
	public void updateUser(String userName,String filePath){
		this.filePath.put(userName, filePath);
		if (!this.userList.contains(userName)) {
			this.userList.add(userName);
		}
		this.calculate();
	}
	
	/**
	 * サーバ用close.
	 */
	public void close(){
		if (nowFrame!=null) {nowFrame.dispose();}
		if (nowFramework!=null) {nowFramework.dispose();}
	}
	
	/**
	 * 一般用close.
	 * スーパーユーザが削除を試みた場合はtrue.
	 * @param userName - 削除を試みたユーザ名.
	 * @return
	 */
	public Boolean close(String userName){
		if (userList.get(0).equals(userName)) {
			if (nowFrame!=null) {nowFrame.dispose();}
			if (nowFramework!=null) {nowFramework.dispose();}
			return true;
		}else{
			return false;
		}
	}
    
	/**
	 * ルームを保存.
	 */
	public void saveRoom(int num){
		Properties props = new Properties();
		String sep = File.separator;
		try {
			File configFile = new File("."+sep+"Room");
			if (!configFile.exists()) {configFile.mkdirs();}
			configFile = new File(configFile.getCanonicalPath()+sep+String.format("%d.room", num));
			props.setProperty("subject", this.subject);
			props.setProperty("truthValue", this.truthValue);
			for (int i = 0; i < this.userList.size(); i++) {
				String u = this.userList.get(i);
				props.setProperty(String.format("user%d", i),u);
			}
			for ( String key : this.filePath.keySet() ) {
				String f = this.filePath.get(key);
				props.setProperty(key,f);
			}
			FileOutputStream fos = new FileOutputStream(configFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			props.store(bos, this.subject);
			fos.close();
			bos.close();
		} catch (IOException ex) {}
	}
	
	/**
	 * ルームを復元.
	 * @param props
	 * @return
	 */
	public void loadRoom(Properties props) throws IllegalStateException{
		this.subject = props.getProperty("subject");
		this.truthValue = props.getProperty("truthValue");
		
		String buf = new String();
		for(int i=0;buf!=null;i++){
			buf = props.getProperty(String.format("user%d", i));
			if (buf!=null && !buf.equals("")) {
				this.userList.add(buf);
			}
		}
		// 情報に欠損，または不正がある場合
		if(this.subject==null || this.subject.equals("")  || this.truthValue==null || this.truthValue.equals("") || userList.size()==0){
			throw new IllegalStateException("The backup data is illegitimate.");
		}
		
		for (int i = 0; i < this.userList.size(); i++) {
			String user = this.userList.get(i);
			String p = props.getProperty(user);
			this.filePath.put(user, p);
		}
	}
	
	/**
	 * 全ノードの論証と攻撃関係を返却する.
	 * @return List
	 */
	public List<String> getNodeInfo(){
		return Argument.nodeInfo(truthValue, filePath);
	}
	
	/**
	 * 議論計算.
	 */
	public void calculate(){
		this.nowTree = new ArgumentTree();
		this.nowTree.init(truthValue, filePath);
		
		this.argumentResult = this.nowTree.calculate(subject);
		
		String attackList = Argument.allAttack(truthValue, filePath).toString();
		int nodenum = Argument.getNodeNum(truthValue, filePath);
		
		if (this.nowFrame!=null) {
			this.displayArgumentTree();
		}
		
		if(this.nowFramework!=null){
			this.displayFrame();
		}
		
		try {
			this.cancelTask("GROUNDED");
			this.cancelTask("STABLE");
			this.cancelTask("PREFERRED");
			this.cancelTask("COMPLETE");
			this.groundedNum = this.getTaskNum(attackList,nodenum,"CALCULATE_GROUNDED");
			this.stableNum = this.getTaskNum(attackList,nodenum,"CALCULATE_STABLE");
			this.preferredNum = this.getTaskNum(attackList,nodenum,"CALCULATE_PREFERRED");
			this.completeNum = this.getTaskNum(attackList,nodenum,"CALCULATE_COMPLETE");
		} catch (SocketException e){
			System.err.println("Semantics Server is unreachable");
		} catch (SocketTimeoutException e) {
			System.err.println("Time out!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
    
	/**
	 * 渡された議題と等しいか判定.
	 * @param subject - 判定する議題.
	 * @return 等しい場合 true.
	 */
	public boolean isEqualSubject(String subject){
		if(this.subject.equals(subject)){
			return true;
		}else{
			return false;
		}
	}
    
	/**
	 * 議論木を表示.
	 */
	public void displayArgumentTree(){
		if (this.nowFrame==null) {
			this.nowFrame = new JFrame(this.subject);
			this.nowFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
            this.nowFrame.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    nowFrame = null;
                }
            });
			
			this.nowFrame.setVisible(true);
		}else{
			this.nowFrame.getContentPane().removeAll();
		}
		
		if (this.nowTree!=null) {
			JScrollPane[] jsps = this.nowTree.setPanel();
			JPanel buttonPanel = new JPanel();
			final JPanel card = new JPanel();
			final CardLayout layout = new CardLayout();
			card.setLayout(layout);
			buttonPanel.setLayout(new FlowLayout());
			for (int i = 0; i < jsps.length; i++) {
				String cmd = String.format("tree%d", i);
				JButton jb = new JButton(cmd);
				buttonPanel.add(jb);
				jb.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        String cmd = e.getActionCommand();
                        layout.show(card, cmd);
                    }
				});
				jb.setActionCommand(cmd);
				card.add(jsps[i],cmd);
			}
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			p.add("North",buttonPanel);
			p.add("Center",card);
			this.nowFrame.getContentPane().add(p);
		}
		this.nowFrame.validate();
		this.nowFrame.pack();
	}
	
	/**
	 * 議論フレームワークを表示.
	 */
	public void displayFrame(){
		final ResultPanel p = new ResultPanel();
		if (this.nowFramework==null) {
			this.nowFramework = new JFrame(this.subject);
			this.nowFramework.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
            this.nowFramework.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    ResultPanel p2 = (ResultPanel) nowFramework.getContentPane().getComponent(0);
    				p2.stopDrawing();
    				nowFramework = null;
                }
            });
			this.nowFramework.setVisible(true);
			this.nowFramework.setPreferredSize(new Dimension(500, 500));
		}else{
			if(this.nowFramework.getContentPane().getComponentCount()!=0){
				ResultPanel p2 = (ResultPanel) this.nowFramework.getContentPane().getComponent(0);
				p2.stopDrawing();
			}
			this.nowFramework.getContentPane().removeAll();
		}
		
		if (this.nowTree!=null) {
			
			p.setPreferredSize(new Dimension(500, 500));
			Map<String, Node> map = Argument.nodeInfoMap(this.truthValue, this.filePath);
			
			Argumentation arg = new Argumentation();
			Iterator<String> it = map.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				Node n = map.get(key);
				arg.addArgument(new frame.argumentation.Argument());
				List<String> list = n.getAttackList();
				try{
					for (int i = 0; i < list.size(); i++) {
						arg.addRelation(new Relation(Integer.parseInt(key), Integer.parseInt(list.get(i)), Relation.ORG_ATTACK));
					}
				}catch(NumberFormatException e){e.printStackTrace();}
			}
			p.setArgumentation(arg);
			this.nowFramework.getContentPane().add(p);
			p.startDrawing();
		}
		this.nowFramework.validate();
		this.nowFramework.pack();
	}
	
	public static void main(String[] args){
		ArgumentRoom argroom = new ArgumentRoom("~climb(we,mountain)::[0.8]", "01value", "test");
		argroom.updateUser("test", "/Users/katsura/Documents/workspace/PIRIKA-Server-2.0/EALP/katsura/01value/climb(we,mountain).ealp");
        //		argroom.updateUser("test2", "/Users/katsura/Documents/workspace/PIRIKA-Server-2.0/EALP/katsura/01value/climb2.ealp");
		
        //		System.out.println(argroom.getRoomResult());
        //		argroom.displayArgumentTree();
        //		System.out.println(argroom.getNodeInfo());
		argroom.displayFrame();
		/*
         try {
         System.out.println(argroom.getGroundedResult());
         } catch (IOException e) {
         e.printStackTrace();
         }
         try {
         Thread.sleep(10000);
         } catch (InterruptedException e) {
         e.printStackTrace();
         }
         try {
         System.out.println(argroom.getStableResult());
         System.out.println(argroom.getPreferredResult());
         System.out.println(argroom.getCompleteResult());
         } catch (IOException e) {
         e.printStackTrace();
         }*/
		
	}
}