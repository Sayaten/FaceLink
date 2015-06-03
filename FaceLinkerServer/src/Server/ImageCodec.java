package Server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCodec {
	public static final String IMG_ROOT_DIR = "/home/saya/Project/FLImages/";
	public static void saveImage(byte[] data, String dir, String name){
		File ofile = new File(IMG_ROOT_DIR + dir + "/" + name);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(ofile);
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static byte[] loadImageToByteArray(String dir, String name){
		File ifile = new File(IMG_ROOT_DIR + dir + "/" + name);
		FileInputStream fis;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int readbyte = 0;
		try{
			fis = new FileInputStream(ifile);
			while((readbyte = fis.read(buf)) != -1){
				bos.write(buf, 0, readbyte);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return bos.toByteArray();
	}
}
