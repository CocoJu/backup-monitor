package pc.bmonitor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.text.SimpleDateFormat;

public class HttpResponse {
	
	private static StringBuffer css;
	private static StringBuilder dynContent;
	private String body;            
	private List<String> trackedFls;
	private List<String> echoFls;
	
	public HttpResponse(ArrayList<BackupFactory> arrBackups , List<String> echoFiles, StringBuffer errorLog) throws IOException {
		echoFls = echoFiles;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
		css = new StringBuffer();
		dynContent = new StringBuilder();
		css = UtilSupport.readFileToBuffer("style.css", null, "utf-8");
		if(errorLog.length()!=0){
			dynContent.append("<div class=\"errorLog\">")
				.append(errorLog)
			.append("</div>");
		}
		if(!(echoFiles == null)){
			for(String s:echoFls){
				dynContent.append("<div class=\"sequence_db\">")
					.append("<a class=\"header\">" + s +"</a></br></br>")					
					.append(UtilSupport.readFileToBuffer(s, "</br>","windows-1251"))
				.append("</div>");
			}
		}		
		for(BackupFactory backupInstance : arrBackups){
			dynContent.append("<div class=\"backup_unit\">")
				.append("<a class=\"header\">") 
				.append("Hostname: " + backupInstance.getHostName())
				.append("</a>");
			Map<String, BackupFactory.DirContainer> mapBackup = backupInstance.getBackups();
			Set<String> backupIterator = mapBackup.keySet();
					for(String dir:backupIterator){
						dynContent.append("<div class=\"list_files\">")
							.append("<a>")
							.append(dir)
							.append("</a>")
							.append("<ul>");
						ArrayList<BackupFactory.FileInfo> fileArray = mapBackup.get(dir).getfInfo();
						for(BackupFactory.FileInfo fileInstance:fileArray){
							dynContent.append("<li>")
								.append(fileInstance.getPath())
								.append("<ul><li>")
									//.append("")
										.append(sdf.format(fileInstance.getDate()))
									.append("</li>")
									.append("<li>")
									
										.append( UtilSupport.humanReadableByteCount(fileInstance.getSize()))
							.append("</li></ul></li>");
						}
						dynContent.append("</ul>");
						
						dynContent.append("</div>");	
					}
					trackedFls = backupInstance.trackFiles();
					if(trackedFls!=null){
						dynContent.append("<div class=\"untracked_f\">")
								.append("<a class=\"header\">Не найдены файлы!</a></br>")
								.append("<ul>");
						for(String tFile:trackedFls){
							dynContent.append("<li>")
								.append(tFile)
								.append("</li>");
						}
						dynContent.append("</ul>")
							.append("</div>");
					}
			dynContent.append("</div>");
		}
		
		body =		 "<!DOCTYPE html>" +
						"<html>" +
						"<head>" +
							"<style type=\"text/css\">" +
								css +
							"</style>" +
						"<meta charset=\"UTF-8\">" +
						"<title>Мониторинг</title>" +
						"</head>" +
						"<body>" +
							"<div class=\"main\">" +
								dynContent +
							"</div>" +
						"</body>" +
						"</html>";
	}

	public void sendResponse(OutputStream out) throws IOException{
		byte[] b = body.getBytes();
		String header = "HTTP/1.1 200 OK\r\n" +
                "Server: YarServer/2009-09-09\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + b.length + "\r\n" +
                "Connection: close\r\n\r\n";
		out.write(header.getBytes());
		out.write(b);
	}
}
