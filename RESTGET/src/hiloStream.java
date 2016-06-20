
public class hiloStream extends Thread {
	String stream;
	public hiloStream(String string) {
		// TODO Auto-generated constructor stub
		stream=string;
	}

	public void run() {
		System.out.println("Ha comenzado el hilo para: "+stream);

	}

}
