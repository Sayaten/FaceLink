package Server;

public class Timer {
	public static void MatrixTime(int delayTime){
		long saveTime = System.currentTimeMillis();
		long currTime = 0;

		while( currTime - saveTime < delayTime){
			currTime = System.currentTimeMillis();
		}
	}
}
