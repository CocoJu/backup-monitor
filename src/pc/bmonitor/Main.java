package pc.bmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import  java.io.OutputStream;

public class Main {

	public static void main(String[] argv) throws Throwable{
		ServerSocket serverSocket =
					new ServerSocket(80);
		while (true) {
			Socket clientSocket = serverSocket.accept();
			new Thread(new SocketProcessor(clientSocket)).start();
		}
	}

	private static class SocketProcessor implements Runnable {

		private Socket s;
		private InputStream is;
		private OutputStream os;

		private SocketProcessor(Socket aClientSocket) throws Throwable {
			this.s = aClientSocket;
			this.is = s.getInputStream();
			this.os = s.getOutputStream();
		}

		public void run() {
			StringBuffer errorLog = new StringBuffer();
			String requestURN = null;
			try {
				requestURN = this.readInputHeaders();
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
			if(requestURN != "/favicon.ico" | requestURN!=null){
				ArrayList<BackupInfo> arrayBackupFact =
								new ArrayList<BackupInfo>();
				ReadXMLFile rxml = new ReadXMLFile();
				//переделать парсер, сделать статические методы
				List<ConfigUnit> confArray = 
						rxml.parseConfig("config.xml");
				for(ConfigUnit conf:confArray){
					try{
						Socket sock = new Socket(conf.hostname, 9090);
						System.out.println("new socket connection");
						NetReceiver sockRW = 
							new NetReceiver(sock.getOutputStream(), sock.getInputStream());
						sockRW.sendDirQuery(conf.getDirs());
						BackupInfo bf = 
							new BackupInfo(sockRW.getBackupInfo(), conf.getTrackedFiles());
						arrayBackupFact.add(bf);
						sockRW.closeStreams();
						sock.close();
					}catch(java.net.ConnectException e){
						errorLog.append("Ошибка соединения с " + conf.hostname + "<br>");
					}catch(UnknownHostException e){
						errorLog.append("Ошибка соединения с " + conf.hostname + "<br>");
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					new HttpResponse(requestURN, arrayBackupFact , rxml.echoFilesList, errorLog)
						.sendResponse(os);
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private String readInputHeaders() throws Throwable {
			String urn = null;
			BufferedReader br = 
				new BufferedReader(new InputStreamReader(this.is));
			while(true){
				String str = br.readLine();
				if(str.matches("^GET.*"))
					urn = str.replaceAll("^GET ", "").replaceAll(" HTTP/1.1", "");
				if(str == null || str.trim().length() == 0)
					break;
			}
			return urn;
		}
	}
}
