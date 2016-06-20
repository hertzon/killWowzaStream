import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;
import org.omg.CORBA.PolicyOperations;


import javax.xml.bind.JAXBContext;

public class disconnectStream {
	private static String[] streams;
	static int index=0;
	static stream dvr;
	static String[] streamArrays = new String[50];
	static boolean save=false;
	
	static String uri1;
	static int uptime=0;
	static int ti=0;
	static int tf=0;
	static int counterBytesOut=0;
	static int[] countersBytesOut=new int[50];
	static int[] bytesIn=new int[50];
	static int[] bytesOut=new int[50];
	static int[] totalConnections=new int[50];
	static int[] totalConnectionsOld=new int[50];
	static int[] countersBytesOutData=new int[50];
	static int[] timeoutWithData=new int[50];
	

	public static void main(String[] args) {
		String uri="http://coltrack.com:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/instances?";
		
		String Strjson=null;
		int dos=0;
		while (true){
			try {
				//Obtenemos los streams que estan conectados:
				URL url = new URL(uri);
				try {
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");	
					connection.setRequestProperty("Accept", "application/json");
					BufferedReader in = new BufferedReader(
	                        new InputStreamReader(
	                        connection.getInputStream()));
					String decodedString;
					while ((decodedString = in.readLine()) != null) {
						//System.out.println(decodedString);
						Strjson=decodedString;				
					}
					//System.out.println("Json original: "+Strjson);
					String[] parts = Strjson.split("\"");
					index=0;
					for (int i=0;i<parts.length;i++){
						System.out.println(i+":"+parts[i]);
						if (parts[i].equals("name")){
							dos=0;
						}
						dos++;

						if (dos==3 && !parts[i].equals("_definst_") && !parts[i].equals(":")){
							System.out.println("Stream obtenido http:"+parts[i]);
							//dvr = new stream(parts[i]);
							save=true;
							for (int j=0;j<streamArrays.length;j++){
								if (parts[i].equals(streamArrays[j])){
									save=false;
								}
							}
							if (save){
								streamArrays[index]=parts[i];
								//streams[index]=parts[i];
								index++;
							}
							
						}
					}	
//					System.out.println("Arrays Guardados:");
//					for (int i=0;i<streamArrays.length;i++){
//						if (streamArrays[i]!=null){
//							
//							System.out.println(streamArrays[i]);
//							//hiloStream stream=new hiloStream(streamArrays[i]);
//							
//							//stream.start();
//							//streamArrays[i]=null;
//							
//							
//							
//						}
//					}
					
					//Obtenemos el uptime y bytesOut
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
					for (int i=0;i<streamArrays.length;i++){
						if (streamArrays[i]!=null){
							System.out.print("Stream Procesado:");
							System.out.println(streamArrays[i]);						
							
							uri1="http://coltrack.com:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/instances/_definst_/incomingstreams/"+streamArrays[i]+"/monitoring/current";
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
							int intNumber=0;
							JSONObject object = null;
							object = new JSONObject(Strjson);
							uptime=object.getInt("uptime");
							intNumber=object.getInt("bytesIn");
							bytesIn[i]=intNumber;
							System.out.print("bytesIn: "+bytesIn[i]+",");
							
							intNumber=object.getInt("bytesOut");
							bytesOut[i]=intNumber;
							System.out.print("bytesOut: "+bytesOut[i]+",");

							intNumber=object.getInt("totalConnections");
							totalConnections[i]=intNumber;
							System.out.print("totalConnections: "+totalConnections[i]+",");
							
							if (totalConnections[i]>totalConnectionsOld[i]){
								System.out.println("");
								System.out.println("Nuevo Usuario Detectado*************************");
								timeoutWithData[i]=timeoutWithData[i]+60;
							}
							totalConnectionsOld[i]=totalConnections[i];
							
							
							
							if (bytesOut[i]==0){
								int diferenciaTiempo=0;
								//diferenciaTiempo=uptime-ti;
								//System.out.println("Diferencia tiempo: "+diferenciaTiempo);
								System.out.print("counterBytesOut: "+countersBytesOut[i]+",");
								counterBytesOut++;
								countersBytesOut[i]++;
								//System.out.println(counterBytesOut);
								if (countersBytesOut[i]>30 ){
									System.out.println("Matando stream por tiempo y por bytes 0...");
									
									
									String uristop="http://coltrack.com:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/instances/_definst_/incomingstreams/"+streamArrays[i]+"/actions/disconnectStream";
									
									
									
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
									    //System.out.println("Respuesta: "+body);
									    //Borramos stream del array de streams:
									    System.out.println("Stream a borrar de la lista: "+streamArrays[i]);
									    String streama=streamArrays[i];
									    for (int k=0;k<streamArrays.length;k++){
									    	String streamb=streamArrays[k];
									    	if (streama.equals(streamb)){
									    		System.out.println("Borrando stream de la lista...");
									    		streamArrays[i]=null;
									    		countersBytesOut[i]=0;
									    	}
									    	
									    }
									    httpCon.disconnect();
									    connection.disconnect();
									    
									} catch (MalformedURLException e) {
									    e.printStackTrace();
									    System.out.println("Error: Borrando stream de la lista...");
							    		streamArrays[i]=null;
									} catch (ProtocolException e) {
									    e.printStackTrace();
									    System.out.println("Error: Borrando stream de la lista...");
							    		streamArrays[i]=null;
									} catch (IOException e) {
									    e.printStackTrace();
									    System.out.println("Error: Borrando stream de la lista...");
							    		streamArrays[i]=null;
									}
									
								}
								countersBytesOutData[i]=0;
							}else {
								//si bytesOut>0
								counterBytesOut=0;
								countersBytesOut[i]=0;
								ti=uptime;	
								countersBytesOutData[i]++;
								
								
								
								
							}
							//System.out.println(streamArrays[i]);
							System.out.print("timeoutData: "+timeoutWithData[i]+",");
							System.out.print("UpTime: "+uptime+",");
							System.out.println("Conteo con dataOut: "+countersBytesOutData[i]+",");
							System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
							ti=uptime;
						}	
					}
					

					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			while (dvr.isAlive()){
//				
//			}
			//System.out.println("streams killed!!!");
			System.out.println("--------------------------------");
			System.out.println("");

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}