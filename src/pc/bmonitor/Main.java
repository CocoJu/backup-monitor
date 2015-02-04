package pc.bmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import  java.io.OutputStream;

public class Main {
	
	private static StringBuffer errorLog = new StringBuffer();

	public static void main(String[] argv) throws UnknownHostException, IOException, InterruptedException{
		while(true){
			ServerSocket serverSocket =
	                new ServerSocket(80);
			Socket clientSocket = serverSocket.accept();
			BufferedReader br = 
					new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while(true){
				String s = br.readLine();
				if(s == null || s.trim().length() == 0)
					break;
			}
			
			ArrayList<BackupFactory> arrayBackupFact = new ArrayList<BackupFactory>();
			ReadXMLFile rxml = new ReadXMLFile();
			//переделать парсер, сделать статические методы
			List<ConfigUnit> confArray = rxml.parseConfig("config.xml");
			
			for(ConfigUnit conf:confArray){
				try{
					Socket sock = new Socket(conf.hostname, 9090);
					SocketReaderWriter sw = 
							new SocketReaderWriter(sock.getOutputStream(),sock.getInputStream());
					sw.sendDirQuery(conf.getDirs());
					BackupFactory bf = 
							new BackupFactory(sw.getBackupInfo(), conf.getTrackedFiles());
					arrayBackupFact.add(bf);
					sw.closeStreams();
					sock.close();
					
					}
				catch(java.net.ConnectException e){
					Main.errorLog.append("Ошибка соединения с " + conf.hostname + "<br>");
				}
			}
			OutputStream out =
		            clientSocket.getOutputStream();
			new HttpResponse(arrayBackupFact , rxml.echoFilesList, Main.errorLog)
					.sendResponse(out);
			serverSocket.close();
			//браузер делает 2 запроса за один раз, приходится использовать таймер
			Thread.sleep(3000);
			Main.errorLog.setLength(0);
		}
	}	
}
