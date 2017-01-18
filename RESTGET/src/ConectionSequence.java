import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

public class ConectionSequence {
	static HttpURLConnection connection;
	static String Strjson=null;
	static int bytesInRate=0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("bytesInRate check Example");
		System.out.println("28/10/2016");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Note: This example use 5571001700_cam1.stream");

		String name="5571001700_cam1.stream";
		
		String uri="http://localhost:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/streamfiles/5571001700_cam1/actions/connect?connectAppName=live&appInstance=_definst_&mediaCasterType=rtp";
		String uri1="http://localhost:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/instances/_definst_/incomingstreams/"+name+"/monitoring/current";
		try {
			Thread.sleep(3000);
			System.out.println("Connect stream....(not load the camera player mediaelement for the moment)");
			System.out.println("Note: the webpage must show a loading(Cargando) state!");
			URL url = new URL(uri);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Accept", "application/json");
			BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    connection.getInputStream()));
			System.out.println("Once stream is connected and active check bytesInRate until it is more than > 100");
			//Monitoring the bytesInRate
			do {
				url = new URL(uri1);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "application/json");
				in = new BufferedReader(
	                    new InputStreamReader(
	                    connection.getInputStream()));
				
				Strjson="";
				String decodedString;
				in = new BufferedReader(
	                    new InputStreamReader(
	                    connection.getInputStream()));
				
				while ((decodedString = in.readLine()) != null) {
					//System.out.println(decodedString);								
					Strjson=decodedString;								
					
				}
				//System.out.println("Json Received: "+Strjson);
				JSONObject objd = new JSONObject(Strjson);
				bytesInRate=(int) objd.get("bytesInRate");
				if (bytesInRate <= 100){
					System.out.println("bytesInRate: "+bytesInRate + "\tWait!");
				}
				else {
					System.out.println("bytesInRate: "+bytesInRate + "\tContinue!");
				}
				Thread.sleep(10);
			}while (bytesInRate<=100);
			Thread.sleep(3000);
			System.out.println("Wait 2 aditional seconds to allow wowza to buffer some video data");
			Thread.sleep(2000);
			System.out.println("Load Media Element Player and allow user to start viewing video");
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
