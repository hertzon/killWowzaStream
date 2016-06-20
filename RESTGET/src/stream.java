import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Time;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class stream {
	static String uri;
	static String stream;
	String a="http://coltrack.com:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/instances/_definst_/incomingstreams/";
	static String Strjson=null;
	int uptime=0;
	static String uri1=null;
	static String uristop=null;
	static int bytesOut=0;
	boolean alive;
	int reviewUptime=0;
	static int timeout=0;
	static int prescalerTimeout=0;
	HttpURLConnection connection;
	
	
	
	public stream(String string) {
		// TODO Auto-generated constructor stub
		System.out.println("Has creado un stream:"+string+',');
		stream=string;
		uri=a+stream;
		uri1="http://coltrack.com:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/instances/_definst_/incomingstreams/"+stream+"/monitoring/current";
		uristop="http://coltrack.com:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/instances/_definst_/incomingstreams/"+stream+"/actions/disconnectStream";
		
		URL url;
		try {
			url = new URL(uri);
			
			try {
				connection = (HttpURLConnection) url.openConnection();
				alive=true;
				timeout=360;
				prescalerTimeout=0;
				while (uptime<timeout){
					
					
					
					url = new URL(uri1);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Accept", "application/json");
					BufferedReader in = new BufferedReader(
		                    new InputStreamReader(
		                    connection.getInputStream()));
					
					Strjson="";
					in = new BufferedReader(
		                    new InputStreamReader(
		                    connection.getInputStream()));
					String decodedString;
					while ((decodedString = in.readLine()) != null) {
						//System.out.println(decodedString);
						
						Strjson=decodedString;
						
						
					}
					//System.out.println("Json original: "+Strjson);
					JSONObject object = null;
					object = new JSONObject(Strjson);
					uptime=object.getInt("uptime");
					bytesOut=object.getInt("bytesOut");	
					System.out.println(stream);
					System.out.println("UpTime: "+uptime);
					System.out.println("bytesOut: "+bytesOut);
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
					
					if (bytesOut==0){
						prescalerTimeout++;
						if (prescalerTimeout>2){
							timeout=45;
						}
						
					}else {
						prescalerTimeout=0;
						timeout=250;
					}
					
					if (uptime==0){
						reviewUptime++;
						if (reviewUptime>2){
							alive=false;
							break;
						}
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				
				
				
				
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			
			
			
			
			
			
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		System.out.println("Apagando stream.....");
		System.out.println(uristop);
		try {
		    URL urla = new URL(uristop);
		    HttpURLConnection httpCon = (HttpURLConnection) urla.openConnection();
		    httpCon.setDoOutput(true);
		    httpCon.setRequestMethod("PUT");
		    OutputStreamWriter out = new OutputStreamWriter(
		        httpCon.getOutputStream());
		    out.write("Resource content");
		    out.close();
		    //httpCon.getInputStream();
		    InputStream inp = httpCon.getInputStream();
		    String encoding=httpCon.getContentEncoding();
		    encoding = encoding == null ? "UTF-8" : encoding;
		    String body = IOUtils.toString(inp,encoding);
		    System.out.println("Respuesta: "+body);
		    alive=false;
		    
		    httpCon.disconnect();
		    connection.disconnect();
		    
		} catch (MalformedURLException e) {
		    e.printStackTrace();
		} catch (ProtocolException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		
		
		
		
		
		
//		
//		while (true){
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
	
	public boolean isAlive(){
		return alive;
	}

}
