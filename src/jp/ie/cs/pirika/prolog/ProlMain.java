package jp.ie.cs.pirika.prolog;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.igormaznitsa.prol.data.Term;
import com.igormaznitsa.prol.data.Var;
import com.igormaznitsa.prol.exceptions.ParserException;
import com.igormaznitsa.prol.exceptions.ProlCriticalError;
import com.igormaznitsa.prol.exceptions.ProlInstantiationErrorException;
import com.igormaznitsa.prol.io.DefaultProlStreamManagerImpl;
import com.igormaznitsa.prol.logic.Goal;
import com.igormaznitsa.prol.logic.ProlContext;
import com.igormaznitsa.prol.parser.ProlConsult;
import com.igormaznitsa.prol.utils.Utils;

public class ProlMain implements Prol{
	private ProlContext context;
	private String name;

	public ProlMain(){
		this("test");
	}

	public ProlMain(String name){
		this.name = name;
		this.clear(name);
	}

	public String getName(){
		return this.name;
	}

	public void clear(String name){
		try {
			if(this.context!=null){
				this.context.halt();
			}
			this.context = new ProlContext(name,DefaultProlStreamManagerImpl.getInstance());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * initialize.
	 * <p>
	 * Set truth value and ealp file path. 
	 * </p>
	 * @param truthvalue - string
	 * @param filepath - ealp file path
	 * @return
	 */
	public Boolean init(String truthvalue,String filepath){
		File[] init = new File[2];
		String sep = File.separator;

		init[0] = new File("TruthValue"+sep+"LMA_EX.pl");
		
		String[] truth = truthvalue.split("/",0);
		if (truth[0].toLowerCase().equals("userdefine")) {
			init[1] = new File("TruthValue"+ sep + truth[0] + sep + truth[1]+".pl");
		}else{
			init[1] = new File("TruthValue"+sep+truthvalue.toLowerCase()+".pl");
		}
		
		BufferedReader br = null;
		try {
			for (int i = 0; i < init.length; i++) {
				File file = init[i];
				br = new BufferedReader(new FileReader(file));
				ProlConsult consult = new ProlConsult(br, this.context);
				consult.consult();
				br.close();
			}
			this.command("read_EALP('"+filepath+"').");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("File_Not_Found.");
			this.clear(this.name);
			return false;
		} catch (com.igormaznitsa.prol.exceptions.ProlExistenceErrorException e) {
			System.err.println("File_Not_Found.");
			this.clear(this.name);
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			this.clear(this.name);
			return false;
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * create demonstration.
	 * @param arg - subject
	 * @return String - demonstration.
	 */
	public String getArgument(String arg) throws ProlCriticalError,ParserException,ProlInstantiationErrorException{
		try {
			Goal g = new Goal("write_query_2("+ arg +",X).", this.context);
			g.solve();
			return g.getVarAsText("X");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Error";
	}

	/**
	 * create demonstration.
	 * @param arg - subject
	 * @return List - demonstrations.
	 */
	public List<String> getArgumentList(String arg) throws ProlCriticalError,ParserException,ProlInstantiationErrorException{
		List<String> list = null;
		try {
			Goal g = new Goal("write_query_2("+ arg +",X).", this.context);
			g.solve();
			String demoString = g.getVarAsText("X");
			list = new ArrayList<String>();
			if (demoString.equals("[]")) {
				return null;
			}

			String s = demoString.substring(2, demoString.length()-2);
			String[] demoList = s.split("\\],\\[", 0);
			for(int k=0;k<demoList.length;k++){
				String str = new String('[' + demoList[k] + ']');
				if(!list.contains(str))
					list.add(str);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}


	/**
	 * The demonstration which is opposed is searched.
	 * @param arg - demonstration.
	 * @return String - demonstration which is opposed.
	 */
	public String defeat(String arg) throws ProlCriticalError,ParserException,ProlInstantiationErrorException{
		try {
			Goal g = new Goal("write_defeatter_2(" + arg + ",X).", this.context);
			g.solve();
			return g.getVarAsText("X");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Error";
	}

	/**
	 * The demonstration which is opposed is searched.
	 * @param arg - demonstration.
	 * @return List - demonstration which is opposed.
	 */
	public List<String> defeatList(String arg) throws ProlCriticalError,ParserException,ProlInstantiationErrorException{
		List<String> list = null;
		try {
			Goal g = new Goal("write_defeatter_2(" + arg + ",X).", this.context);
			g.solve();
			String demoString = g.getVarAsText("X");
			list = new ArrayList<String>();
			if (demoString.equals("[]")) {
				return null;
			}

			String s = demoString.substring(2, demoString.length()-2);
			String[] demoList = s.split("\\],\\[", 0);
			for(int k=0;k<demoList.length;k++){
				String str = new String('[' + demoList[k] + ']');
				list.add(str);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public String command(String com,String v) throws ProlCriticalError,ParserException,ProlInstantiationErrorException{
		try {
			Goal g = new Goal(com, this.context);
			Term result = g.solve();
			Map<String, Var> r = Utils.fillTableWithVars(result);
			return (r.get(v).toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Error";
	}

	public Boolean command(String com) throws ProlCriticalError,ParserException,ProlInstantiationErrorException{
		try {
			Goal g = new Goal(com, this.context);
			Term result = g.solve();
			if (result!=null) {
				return true;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<String> allResult(String arg,String v) throws ProlCriticalError,ParserException,ProlInstantiationErrorException{
		List<String> list = new ArrayList<String>();
		try {
			while(true){
				Goal g = new Goal(arg, this.context);
				Term result = g.solve();
				if (result == null) {
					break;
				}
				Map<String, Var> r = Utils.fillTableWithVars(result);
				list.add(r.get(v).toString());
			}
			return list;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void listing(){
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		final PrintWriter out = new PrintWriter(baos);
		context.getKnowledgeBase().write(out);
		out.flush();
		out.close();
		System.out.println(new String(baos.toByteArray()));
	}

	public static Boolean checkPrologGrammer(String contents) throws ParserException{
		try {
			ProlConsult consult = new ProlConsult(contents,new ProlContext("test",DefaultProlStreamManagerImpl.getInstance()));
			consult.consult();
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Boolean checkPrologGrammer(File filePath) throws ParserException{
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			ProlConsult consult = new ProlConsult(br, new ProlContext("test",DefaultProlStreamManagerImpl.getInstance()));
			consult.consult();
			br.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			try {
				if (br!=null) {br.close();}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static Boolean checkEALPGrammer(String contents) throws ParserException{
		String sep = File.separator;
		File lma = new File("."+sep+"TruthValue" + sep + "LMA_EX.pl");
		BufferedReader br = null;

		try {
			ProlContext context = new ProlContext("test",DefaultProlStreamManagerImpl.getInstance());
			br = new BufferedReader(new FileReader(lma));
			ProlConsult consult = new ProlConsult(br, context);
			consult.consult();
			ProlConsult c = new ProlConsult(contents, context);
			c.consult();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			try {
				if (br!=null) {br.close();}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static Boolean checkEALPGrammer(File filePath) throws ParserException{
		String sep = File.separator;

		File lma = new File("TruthValue" + sep + "LMA_EX.pl");
		BufferedReader br = null;

		try {
			ProlContext context = new ProlContext("test",DefaultProlStreamManagerImpl.getInstance());
			br = new BufferedReader(new FileReader(lma));
			ProlConsult consult = new ProlConsult(br, context);
			consult.consult();
			br.close();
			br = new BufferedReader(new FileReader(filePath));
			ProlConsult c = new ProlConsult(br, context);
			c.consult();
			br.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			try {
				if (br!=null) {br.close();}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void main(String[] args){
		String s = new String("a(X) :- b(X).");
		File f = new File("TruthValue"+"/"+"LMA_EX.pl");
		try {
//			ProlMain.checkPrologGrammer(s);
//			ProlMain.checkPrologGrammer(f);
			StringBuffer sb = new StringBuffer();
			sb.append("秘密道具(ガリバートンネル)::[1.0]<==スモールライトより便利::[1.0].\nスモールライトより便利::[1.0]<==時間制限がない(ガリバートンネル)::[1.0].\n時間制限がない(ガリバートンネル)::[1.0]<==true.");
//			sb.append("a::[1.0]<==b(X)::[1.0].");
			ProlMain.checkEALPGrammer(sb.toString());
//			ProlMain.checkEALPGrammer(new File("/Users/katsura/Desktop/test.ealp"));
		} catch (ParserException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getLine());
			System.out.println(e.getPos());
			//			System.out.println(s.charAt(e.getPos()));
		}
	}
}
