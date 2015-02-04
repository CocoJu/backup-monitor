package pc.bmonitor;

import java.util.Arrays;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class SocketReaderWriter {
	
	private static final String SPLITTER = "<s>";	
	private static final int SIZE_READ_ISBUFFER = 32;
	private OutputStream oStream;
	private InputStream iStream;
	private byte[] IOStreamBuffer;
	private int flagEndOfIStream;
	
	SocketReaderWriter(OutputStream os, InputStream is){
		oStream = os;
		iStream = is;
		IOStreamBuffer = new byte[SIZE_READ_ISBUFFER];
	}
	
	private void sendKeyword(String s) throws IOException{
		if(s.equals("BEGIN")){
			/*проталкиваем в поток сокета пакет из 16 байт, ровно столько, 
			сколько принимает серверная часть, для того чтобы войти в цикл приема-передачи данных
			эту порнографию ниже нужно заменить стандартной функцией*/
			int indexIterator = 0;
			for(byte i : s.getBytes()){
				IOStreamBuffer[indexIterator] = i;
				indexIterator++;
			}
			oStream.write(IOStreamBuffer);
		}
		
	}
	private SocketReaderWriter print(String s) throws IOException{
		oStream.write(s.getBytes());
		oStream.flush();
		return this;
	}
	
	private String read() throws IOException{
		Arrays.fill(IOStreamBuffer, (byte)0);
		//IOStreamBuffer = new byte[32];
		flagEndOfIStream = iStream.read(IOStreamBuffer);
		int i=0;
		for(byte b:IOStreamBuffer){
			if(b!=0)
				i++;
		}
		byte[] containerString = new byte[i]; 
		i=0;
		for(byte b:IOStreamBuffer){
			if(b!=0)
				containerString[i]=b;
				i++;
		}
		return (flagEndOfIStream == -1)? null:new String(containerString);			
	}
	
	public void sendDirQuery(String[] dirs) throws IOException, InterruptedException{
		sendKeyword("BEGIN");
		for(int i=0; i < dirs.length ; i++){
			if(i == dirs.length -1){
			//в последнюю строку SPLITTER не дописывается, т.к. в питоне разбиение str.split() даст лишний хвост ""
				print(dirs[i]);								
				break;
			}
			print(dirs[i])
				.print(SPLITTER);
		}
		print("END!");
	}
	
	public String[] getBackupInfo() throws IOException{
		StringBuilder sb = new StringBuilder();
		String str;
		while((str = read()) != null){
			sb.append(str);
		}
		String[] sArr = (new String(sb)).split("<s>");
		return sArr.length == 0? null:sArr;
	}
	
	public void closeStreams() throws IOException{
		oStream.close();
		iStream.close();
	}
}
