package jp.ie.cs.pirika.argumentFrame;

import java.util.Comparator;

public class CompareNumber implements Comparator<String>{
	public int compare(String arg0, String arg1) {
		if(Integer.valueOf(arg0) < Integer.valueOf(arg1)){
			return -1;
		}else if(Integer.valueOf(arg0) > Integer.valueOf(arg1)){
			return 1;
		}
		return 0;
	}

}
