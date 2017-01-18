import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class killer {
	static String uri="http://localhost:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/instances?";
	static HttpURLConnection connection;
	static String Strjson=null;
	static String uri1;
	
	static String[][][] streams = new String[100][100][100];
	static int index=0;
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Iniciando Killer..............");
		try {
			while (true){
				System.out.println("Leyendo Streams....");
				URL url = new URL(uri);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");	
				connection.setRequestProperty("Accept", "application/json");
				BufferedReader in = new BufferedReader(
	                    new InputStreamReader(
	                    connection.getInputStream()));
				String decodedString;
				while ((decodedString = in.readLine()) != null) {
					Strjson=decodedString;				
				}
				//System.out.println("Json original: "+Strjson);
				JSONObject obj = new JSONObject(Strjson);
				JSONArray arr = obj.getJSONArray("instanceList");
				try {
					JSONObject obj1=arr.getJSONObject(0);
					JSONArray arr1 = obj1.getJSONArray("incomingStreams");
					System.out.println("nStreams: "+arr1.length());
					for (int i = 0; i < arr1.length(); i++){
					    String name = arr1.getJSONObject(i).getString("name");
					    System.out.println("name["+i+"]: "+name);
					    
					    
//					    boolean save=true;
//						for (int j=0;j<streamArray.length;j++){
//							if (name.equals(streamArray[j])  ){
//								save=false;
//							}
//						}
//						if (save){
//							streamArray[index]=name;
//							//streams[index]=parts[i];
//							index++;
//						}
					    boolean save=true;
					    for (int j=0;j<streams.length;j++){
							if (name.equals(streams[j][0][0])  ){
								save=false;
							}
						}
						if (save){
							System.out.println("Guardando: "+name);
							streams[index][0][0]=name;
							streams[index][1][0]="0";
							
							index++;
						}
					    
					    
					    uri1="http://localhost:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/instances/_definst_/incomingstreams/"+name+"/monitoring/current";
					    //System.out.println(uri1);
					    url = new URL(uri1);
					    connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						connection.setRequestProperty("Accept", "application/json");
						in = new BufferedReader(
			                    new InputStreamReader(
			                    connection.getInputStream()));
						
						Strjson="";
						in = new BufferedReader(
			                    new InputStreamReader(
			                    connection.getInputStream()));
						
						while ((decodedString = in.readLine()) != null) {
							//System.out.println(decodedString);								
							Strjson=decodedString;								
							
						}
						//System.out.println("Json original: "+Strjson);
						JSONObject objd = new JSONObject(Strjson);
						int uptime=(int) objd.get("uptime");
						//System.out.println("uptime: "+uptime);
						
						int bytesIn=(int) objd.get("bytesIn");
						//System.out.println("bytesIn: "+bytesIn);
						
						int bytesOut=(int) objd.get("bytesOut");
						//System.out.println("bytesOut: "+bytesOut);
						
						int bytesInRate=(int) objd.get("bytesInRate");
						System.out.println("bytesInRate: "+bytesInRate);
						
						int bytesOutRate=(int) objd.get("bytesOutRate");
						System.out.println("bytesOutRate: "+bytesOutRate);
						
						int totalConnections=(int) objd.get("totalConnections");
						//System.out.println("totalConnections: "+totalConnections);
						
						
						if (bytesOutRate==0){
							for (int j=0;j<streams.length;j++){
								if (name.equals(streams[j][0][0])){
									int n=Integer.valueOf(streams[j][1][0]);
									n++;
									String nn=String.valueOf(n);
									streams[j][1][0]=nn;
								}
							}
//							for (int j=0;j<streams.length;j++){
//								if (name.equals(streams[j][0][0])){
//									String nn=String.valueOf(0);
//									streams[j][0][1]=nn;
//								}
//							}
							
						}else {
							
							
							
							
							for (int j=0;j<streams.length;j++){
								if (name.equals(streams[j][0][0])){
									String nn=String.valueOf(0);
									streams[j][1][0]=nn;
								}
							}
						}
						
						for (int j=0;j<streams.length;j++){
							if (name.equals(streams[j][0][0])){
								if (streams[j][0][1]==null){
									streams[j][0][1]="0";
								}
								int n=Integer.valueOf(streams[j][0][1]);
								n++;
								String nn=String.valueOf(n);
								streams[j][0][1]=nn;
							}
						}
						
						
						//Leemos el timeout y si es >60 matamos el stream
						for (int j=0;j<streams.length;j++){
							if (name.equals(streams[j][0][0])){
								int timeout=Integer.valueOf(streams[j][1][0]);
								int timeoutDatos=Integer.valueOf(streams[j][0][1]);
								//if (timeout>60 || timeoutDatos>300){
								if (timeout>120 || timeoutDatos>300){
									System.out.println("Matar stream: "+streams[j][0][0]);
									String uristop="http://localhost:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/instances/_definst_/incomingstreams/"+name+"/actions/disconnectStream";
									System.out.println("Parando Stream: "+uristop);
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
									    //System.out.println("Respuesta: "+body);
									    //Borramos stream del array de streams:
									    for (int k=0;k<streams.length;k++){
									    	if (name.equals(streams[k][0][0])){
									    		streams[k][0][0]=null;
									    		streams[k][1][0]=null;
									    		streams[k][0][1]=null;
									    		if (index>0){
									    			index--;
									    		}
									    		
									    	}
									    }
									    
									    
									    httpCon.disconnect();
									    connection.disconnect();
									    
									} catch (MalformedURLException e) {
									    e.printStackTrace();
									    System.out.println("Error: Borrando stream de la lista...");
							    		
									} catch (ProtocolException e) {
									    e.printStackTrace();
									    System.out.println("Error: Borrando stream de la lista...");
							    		
									} catch (IOException e) {
									    e.printStackTrace();
									    System.out.println("Error: Borrando stream de la lista...");
							    		
									}
								}
								
							}
						}
						
						
						if (totalConnections==0){
//							System.out.println("Matando stream: "+name);
//							String uristop="http://localhost:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/instances/_definst_/incomingstreams/"+name+"/actions/disconnectStream";
//							System.out.println("Parando Stream: "+uristop);
//							try {
//							    URL urla = new URL(uristop);
//							    HttpURLConnection httpCon = (HttpURLConnection) urla.openConnection();
//							    httpCon.setDoOutput(true);
//							    httpCon.setRequestMethod("PUT");
//							    OutputStreamWriter out = new OutputStreamWriter(
//							        httpCon.getOutputStream());
//							    out.write("Resource content");
//							    out.close();
//							    //httpCon.getInputStream();
//							    InputStream inp = httpCon.getInputStream();
//							    String encoding=httpCon.getContentEncoding();
//							    encoding = encoding == null ? "UTF-8" : encoding;
//							    String body = IOUtils.toString(inp,encoding);
//							    //System.out.println("Respuesta: "+body);
//							    //Borramos stream del array de streams:
//							    
//							    
//							    httpCon.disconnect();
//							    connection.disconnect();
//							    
//							} catch (MalformedURLException e) {
//							    e.printStackTrace();
//							    System.out.println("Error: Borrando stream de la lista...");
//					    		
//							} catch (ProtocolException e) {
//							    e.printStackTrace();
//							    System.out.println("Error: Borrando stream de la lista...");
//					    		
//							} catch (IOException e) {
//							    e.printStackTrace();
//							    System.out.println("Error: Borrando stream de la lista...");
//					    		
//							}
						
						}
					    
					    
					    
					    
					    
					    
					    
						//String name=arr.("incomingStreams");
					    //System.out.println("arr["+i+"]"+arr.getJSONObject(i));
						System.out.println("///////////////////////////////////////////////");
					   
					}
					System.out.println("");System.out.println("");
					for (int j=0;j<10;j++){
						System.out.println("Stream ["+streams[j][0][0]+"]"+" timeout: "+streams[j][1][0]+" timeoutDatos: "+streams[j][0][1]);
					}
					System.out.println("");
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				
				
				
				
					Thread.sleep(1000);
			}
			
			
			
			
			
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
