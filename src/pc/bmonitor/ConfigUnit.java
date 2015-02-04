package pc.bmonitor;

import java.util.LinkedHashMap;

public class ConfigUnit {
	public String hostname;
	public LinkedHashMap<String, String[]> dirs;
	private String[] sDirs;
	
	public LinkedHashMap<String, String[]> getTrackedFiles(){
		return dirs;
	}
	
	public ConfigUnit() {
		dirs = new LinkedHashMap<String, String[]>();
	}
	
	public String[] getDirs(){
		sDirs = new String[dirs.size()];
		return dirs.keySet().toArray(sDirs);
	}
	
}
