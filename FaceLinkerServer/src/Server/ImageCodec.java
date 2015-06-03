package Server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCodec {
	public static final String IMG_DIR = "/home/saya/Project/FLImages/";
	public static void saveImage(byte[] data, String name){
		File ofile = new File(IMG_DIR + name);
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
	/*
	public byte[] convertImageToBase64(String name){
		byte[] result;
		String fileString = new String();
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		
		try{
			
		}catch(IOException e){
			
		}finally{
			fis.close();
			bos.close();
		}
		return result;
	}
	*/
}
