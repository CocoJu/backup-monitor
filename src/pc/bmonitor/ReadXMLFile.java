package pc.bmonitor;

import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReadXMLFile {
	ArrayList<String> echoFilesList;
	
	private class ReadHandler extends DefaultHandler{
						
		private ArrayList<ConfigUnit> configUnitList;
		private ArrayList<String> trackedFiles;
		private ConfigUnit currentConfigUnit;
		private String currentElement;
		private String keyDir;
		private ArrayList<String> echoFiles;
		
		private ReadHandler(){
			super();
			echoFiles = new ArrayList<String>();
		}
			 
		public void startElement(String uri, String localName,String qName, 
	                Attributes attributes) throws SAXException {
				if(qName == "root"){ 
					configUnitList = new ArrayList<ConfigUnit>();
				}
				if(qName == "host"){
					currentConfigUnit = new ConfigUnit();
					currentConfigUnit.hostname = attributes.getValue(0);
					}
				if(qName == "dir"){
					keyDir = attributes.getValue(0);
					trackedFiles = new ArrayList<String>();
					}
				if(qName == "trackedfile"){
					currentElement="trackedfile";
					}
				if(qName == "echofile"){
					echoFiles.add(attributes.getValue(0));
					}
			
		}
	 
		public void endElement(String uri, String localName,
			String qName) throws SAXException {
			
				if(qName == "host"){
					configUnitList.add(currentConfigUnit);
					currentConfigUnit = null;
					}
				if(qName == "dir"){
					currentConfigUnit.dirs.put(keyDir, 
							trackedFiles.toArray(
									new String[trackedFiles.size()]));
					trackedFiles.clear();
					keyDir=null;
					currentElement=null;
					}
				if(qName == "trackedfile"){
					currentElement = null;
					}
			
		}
	 
		public void characters(char ch[], int start, int length) throws SAXException {
			if(currentElement != null){
				if(currentElement.equals("trackedfile")){
					trackedFiles.add(new String(ch,start,length));
					//currentConfigUnit.dirs.add();
				}
			}
		}
	 
		public ArrayList<ConfigUnit> getConfigUnit(){
			return configUnitList;
		}
		public ArrayList<String> getEchoFiles(){
			return echoFiles.size() == 0 ? null : echoFiles;
		}
	 
	 }

   public ArrayList<ConfigUnit> parseConfig(String path) {
 
    try {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		ReadHandler handler = new ReadXMLFile().new ReadHandler();	
		saxParser.parse(path, handler);
		echoFilesList = handler.getEchoFiles();
		return handler.getConfigUnit();		
     } catch (NullPointerException e) {
    	 e.printStackTrace();
	    return null;   	
     }catch (Exception e) {
 	    e.printStackTrace();   	
 	    return null;   	
      }
   }
}