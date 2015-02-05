package pc.bmonitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Scanner;

public class UtilSupport {
	
	public static Date pythonDateToJava(String s){
		return  new Date( (long)( Double.parseDouble(s)*1000) );
	}
	
	public static String humanReadableByteCount(long bytes) {
		long unit = 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = "kMGTPE".charAt(exp-1) + "";
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static StringBuffer readFileToBuffer(String filename , String splitter, String codeCharachter) throws IOException{
		StringBuffer sbuff = new StringBuffer();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), codeCharachter);
		Scanner sc = new Scanner(reader);
		if(splitter!=null){
			while(sc.hasNext()){
				sbuff.append(sc.nextLine());
					sbuff.append("</br>");
			}
			sbuff.delete(sbuff.length()-splitter.length(), sbuff.length());
		}else{
			while(sc.hasNext()){
				sbuff.append(sc.nextLine());
			}
		}
		sc.close();
		return sbuff;
	}
}
