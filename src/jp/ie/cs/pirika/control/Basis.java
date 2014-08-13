package jp.ie.cs.pirika.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.stream.FileImageInputStream;

public class Basis {
	/**
	 * The ealp file is saved with fileName.
	 * @param userName - String 
	 * @param truthValue - String
	 * @param fileName - String
	 * @param ealp - List
	 * @return filePath
	 */
	protected String saveEalpFile(String userName, String truthValue, String fileName, List<String> ealp) throws IOException{
		String sep = File.separator;
		String[] truth = truthValue.split("/",0);

		File begin = new File("." + sep + "EALP" + sep + userName);
		
		if (!begin.exists()) {
			begin.mkdirs();
			this.fileCopy("."+sep+"default.jpg", new String("." + sep + "EALP" + sep + userName + sep + "self.jpg"));
		}

		File fl=null;

		if (truth[0].toLowerCase().equals("userdefine")) {
			fl = new File("." + sep + "EALP" + sep + userName + sep + "UserDefine" + sep + truth[1]);
		}else{
			fl = new File("." + sep + "EALP" + sep + userName + sep + truth[0]);
		}
		if(!fl.exists()){ fl.mkdirs();}

		String filePath = new String(fl.getAbsolutePath() +sep+ fileName);
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(filePath));
			for (int i = 0; i < ealp.size(); i++) {
				String string = ealp.get(i);
				bw.write(string);
				bw.newLine();
			}
		}finally{
			if (bw!=null) {bw.close();}
		}
		return filePath;
	}

	/**
	 * 
	 * @param userName
	 * @param truthValue
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	protected String readEALP(String userName, String truthValue, String fileName) throws IOException, FileNotFoundException{
		String sep = File.separator;
		String[] truth = truthValue.split("/",0);
		File fl=null;

		if (truth[0].toLowerCase().equals("userdefine")) {
			fl = new File("." + sep + "EALP" + sep + userName + sep + "UserDefine" + sep + truth[1]);
		}else{
			fl = new File("." + sep + "EALP" + sep + userName + sep + truth[0]);
		}

		String filePath = new String(fl.getAbsolutePath() +sep+ fileName);
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try{
			br = new BufferedReader(new FileReader(filePath));
			String str = br.readLine();
			while(str != null){
				sb.append(str+"\n");
				str = br.readLine();
			}
		}finally{
			if (br!=null) { br.close();}
		}
		return sb.toString();
	}

	protected String[] ealpFileList(String userName, String truthValue){
		String sep = File.separator;
		String[] truth = truthValue.split("/",0);
		File fl=null;

		if (truth[0].toLowerCase().equals("userDefine")) {
			fl = new File("." + sep + "EALP" + sep + userName + sep + "UserDefine" + sep + truth[1]);
		}else{
			fl = new File("." + sep + "EALP" + sep + userName + sep + truth[0]);
		}
		return fl.list();
	}
	
	protected String[] truthValueFileList(){
		String sep = File.separator;
		File fl = new File("." + sep + "TruthValue" + sep + "UserDefine");
		return fl.list();
	}

	protected String readTruthValue(String fileName) throws IOException {
		String sep = File.separator;

		File fl = new File("." + sep + "TruthValue" + sep + "UserDefine" + sep + fileName);

		String filePath = fl.getAbsolutePath();
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try{
			br = new BufferedReader(new FileReader(filePath));
			String str = br.readLine();
			while(str != null){
				sb.append(str+"\n");
				str = br.readLine();
			}
		}finally{
			if (br!=null) { br.close();}
		}
		return sb.toString();
	}
	/**
	 * 真理値ファイルを保存.
	 * @param userName
	 * @param fileName
	 * @param contents
	 * @return
	 * @throws IOException
	 */
	protected String saveTruthValueFile(String userName, String fileName, String contents) throws IOException{
		String sep = File.separator;
		File fl = new File("." + sep + "TruthValue" + sep + "UserDefine");
		if(!fl.exists()){fl.mkdirs();}
		
		String filePath = new String(fl.getAbsolutePath() +sep+ fileName);
		BufferedWriter bw = null;
		String[] list = contents.split("\n",0);
		try{
			bw = new BufferedWriter(new FileWriter(filePath));
			bw.write("% This is created by " + userName);
			bw.newLine();
			Date d = new Date();
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			bw.write("% "+df.format(d));
			bw.newLine();
			for(int i=0;i<list.length;i++){
				if (!list[i].startsWith("%")) {
					bw.write(list[i]);
					bw.newLine();
				}
			}
		}finally{
			if (bw!=null) {
				bw.close();
			}
			
		}
		return filePath;
	}


	/**
	 * ファイルコピー.
	 * @param srcPath    コピー元のパス
	 * @param destPath    コピー先のパス
	 * @throws IOException    何らかの入出力処理例外が発生した場合
	 */
	protected void fileCopy(String srcPath, String destPath) throws IOException{
		FileInputStream fis = new FileInputStream(srcPath);
		FileOutputStream fos = new FileOutputStream(destPath);
		FileChannel srcChannel = fis.getChannel();
		FileChannel destChannel = fos.getChannel();
		try {
			srcChannel.transferTo(0, srcChannel.size(), destChannel);
		} finally {
			fis.close();
			fos.close();
			srcChannel.close();
			destChannel.close();
		}

	}


	/**
	 * Does the directory exist?
	 * @param name - user name.
	 * @return If existing, yes.
	 */
	protected Boolean checkDirectory(String name){
		String sep = File.separator;
		File fl = new File("."+ sep + "EALP" + sep + name);
		if(fl.exists()) return true;
		else 			return false;
	}

	/**
	 * ファイルサイズを返す.
	 * @param userName - user名.
	 * @return
	 */
	protected long getImageSize(String userName){
		String sep = File.separator;
		String filePath = new String("."+ sep + "EALP" + sep + userName + sep + "self.jpg");
		File f = new File(filePath);
		return f.length();
	}

	/**
	 * ファイルの更新日時を返す.
	 * @param userName - user名
	 * @param fileName - ファイル名.
	 * @return 日時 - yyyy/MM/dd HH:mm:ss
	 */
	protected String getFileModified(String userName){
		String sep = File.separator;
		String filePath = new String("."+ sep + "EALP" + sep + userName + sep + "self.jpg");
		File f = new File(filePath);
		Date d = new Date(f.lastModified());
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return df.format(d);
	}

	/**
	 * 画像を書き出す.
	 * @param bytebuffer
	 * @param userName
	 */
	protected void writeImage(ByteBuffer bytebuffer,String userName) throws IOException{
		String sep = File.separator;
		String filePath = new String("."+ sep + "EALP" + sep + userName + sep + "self.jpg");
		File f = new File("."+ sep + "EALP" + sep + userName);
		if(!f.exists()){f.mkdirs();}
		byte[] buf = new byte[bytebuffer.limit()];
		bytebuffer.get(buf);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			fos.write(buf);
		}finally{
			if (fos!=null) { fos.close();}
		}
	}

	/**
	 * 画像を読み出す.
	 * @param name - fileName
	 * @return
	 * @throws IOException
	 */
	protected ByteBuffer readImage(String name) throws IOException{
		String sep = File.separator;
		String filePath = new String("."+ sep + "EALP" + sep + name + sep + "self.jpg");
		FileImageInputStream fiis = null;
		try {
			File f = new File(filePath);
			int len = (int) f.length();
			fiis = new FileImageInputStream(f);
			ByteBuffer buffer = ByteBuffer.allocate(len);
			byte[] buf = new byte[len];
			fiis.read(buf);
			buffer.put(buf);
			buffer.flip();
			return buffer;
		} catch (FileNotFoundException e) {
			this.fileCopy("." + sep + "default.jpg", new String("." + sep + "EALP" + sep + name + sep + "self.jpg"));
			return this.readImage(name);
		}finally{
			if(fiis!=null){ fiis.close();}
		}
	}
}
