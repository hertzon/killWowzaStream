
public class pruebaStrings {
	static String[][][] streams = new String[100][100][100];
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		streams[0][0][0]="tron";
		streams[0][1][0]="37";
		streams[0][0][1]="sd";
		
		streams[1][0][0]="tron2";
		streams[1][1][0]="372";
		streams[1][0][1]="sd2";
		
		for (int i=0;i<3;i++){
			System.out.println(streams[i][0][0]+","+streams[i][1][0]+","+streams[i][0][1]);
		}
		

	}

}
