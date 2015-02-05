package pc.bmonitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import static pc.bmonitor.UtilSupport.pythonDateToJava;

public class BackupInfo {
	
	private LinkedHashMap<String,String[]> trackedFiles;
	private String HostName;
	private LinkedHashMap<String, DirContainer> dirThithFileInfo;
	
	
	public BackupInfo(String[] sArray, LinkedHashMap<String,String[]> lhMap) {
		trackedFiles = lhMap;
		String hostN = "^Hostname:.*";
		String dirN = "^Dir:.*";
		LinkedHashMap<String,DirContainer> dirs = new LinkedHashMap<String,DirContainer>();
		int flag = 0;
		String key = null;
		String FileDateSize[] = new String[3];
		for(String s:sArray){
			if(Pattern.matches(hostN, s)){
				HostName = s.replaceAll("^Hostname:", "");
				continue;
			}
			if(Pattern.matches(dirN, s)){
				key = s.replaceAll("^Dir:", "");
				dirs.put(
						key, 
						new DirContainer( key, new ArrayList<BackupInfo.FileInfo>() )
							);
				continue;
			}
			FileDateSize[flag] = s;
			if(flag == 2){
				dirs.get(key).getfInfo().add(
						new FileInfo(
								FileDateSize[0], 
									pythonDateToJava(FileDateSize[1]), 
										Long.parseLong(FileDateSize[2])
												)
						);
				flag=0;
				continue;
			}
			flag++;
		}
		dirThithFileInfo = dirs;
	}

	public class DirContainer{

		private String dirname;
		private ArrayList<BackupInfo.FileInfo> fInfo;

		public String getDirname() {
			return dirname;
		}

		public ArrayList<BackupInfo.FileInfo> getfInfo() {
			return fInfo;
		}

		public DirContainer(String dname, ArrayList<BackupInfo.FileInfo> listFiles ) {
			dirname = dname;
			fInfo = listFiles;
		}
	}

	public class FileInfo{

		private Date date;
		private String path;
		private long size;

		protected FileInfo(String pathArg, Date dateArg, long sizeArg) {
			path = pathArg;
			date = dateArg;
			size = sizeArg;
		}

		protected String getPath() {
			return path;
		}

		protected long getSize() {
			return size;
		}

		protected Date getDate() {
			return date;
		}
		
	}

	public ArrayList<String> trackFiles(){
		ArrayList<String> noTracketFiles = new ArrayList<String>();
		Set<String> dirSet = trackedFiles.keySet();
		Iterator<String> it = dirSet.iterator();
		String s;
		boolean TrueMatch=false;
		while(it.hasNext()){
			s = it.next();
			String[] arrFilesTracked = trackedFiles.get(s);
			List<String> listFactualFiles = getFilesOfDir(s);
			for(String file:arrFilesTracked){
				if(file.contains("*")){
					for(String soaf:listFactualFiles){
						if(soaf.matches(file)){
							TrueMatch = true;
							break;
						}
					}
					if(!TrueMatch){
						noTracketFiles.add(file);
						TrueMatch = false;
					}
				}
				else if(!listFactualFiles.contains(file))
					noTracketFiles.add(file);
			}
		}
		return noTracketFiles.size()==0 ? null : noTracketFiles;
	}

	public ArrayList<String> getFilesOfDir(String dirKey){
		DirContainer dContainer = dirThithFileInfo.get(dirKey);
		ArrayList<String> arr = new ArrayList<String>();
		for(FileInfo fi:dContainer.fInfo){
			arr.add(fi.getPath());
		}
		return arr;
	}
	
	public Map<String, DirContainer> getBackups() {
		return dirThithFileInfo;
	}

	public String getHostName() {
		return HostName;
	}
}
