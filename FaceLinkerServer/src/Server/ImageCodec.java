package Server;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageCodec {
	public static final String IMG_ROOT_DIR = "/home/saya/Project/FLImages/";
	public static final String RESIZE_WITH_WIDTH = "WIDTH";
	public static final String RESIZE_WITH_HEIGHT = "HEIGHT";

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
	
	public static void saveThumbnailImage(byte[] byteImage, String dir, String name, int width, int height){
		ByteArrayInputStream bis = new ByteArrayInputStream(byteImage);
		BufferedImage image;
		Image scaledImage;
		BufferedImage imageBuf;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			image = ImageIO.read(bis);
			if(height == 0 || width == 0){
				System.out.println("Unvalid length!!");
				saveImage(byteImage, dir, name);
			}
			scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			imageBuf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			imageBuf.getGraphics().drawImage(scaledImage, 0, 0, new Color(0,0,0), null);
			
			ImageIO.write(imageBuf, "jpg", bos);
		} catch(IOException e){
			e.printStackTrace();
		}
		
		saveImage(bos.toByteArray(), dir, name);
	}
	
	public static void saveThumbnailImage(byte[] byteImage, String dir, String name, int length, String type){
		ByteArrayInputStream bis = new ByteArrayInputStream(byteImage);
		BufferedImage image;
		Image scaledImage;
		BufferedImage imageBuf;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			image = ImageIO.read(bis);
			
			switch(type){
			case RESIZE_WITH_WIDTH:
				scaledImage = image.getScaledInstance(length, image.getHeight() * length / image.getWidth(), Image.SCALE_SMOOTH);
				imageBuf = new BufferedImage(length, image.getHeight() * length / image.getWidth() , BufferedImage.TYPE_INT_RGB);
				imageBuf.getGraphics().drawImage(scaledImage, 0, 0, new Color(0,0,0), null);
				ImageIO.write(imageBuf, "jpg", bos);
				break;
			case RESIZE_WITH_HEIGHT:
				scaledImage = image.getScaledInstance(image.getWidth() * length / image.getHeight() , length, Image.SCALE_SMOOTH);
				imageBuf = new BufferedImage(image.getWidth() * length / image.getHeight(), length, BufferedImage.TYPE_INT_RGB);
				imageBuf.getGraphics().drawImage(scaledImage, 0, 0, new Color(0,0,0), null);
				ImageIO.write(imageBuf, "jpg", bos);
				break;
			default:
				System.out.println("Unvalid type!!");
				saveImage(byteImage, dir, name);
			}
		} catch(IOException e){
			e.printStackTrace();
		}

		saveImage(bos.toByteArray(), dir, name);
	}
	
	public static void saveThumbnailImage(byte[] byteImage, String dir, String name, float ratio){
		ByteArrayInputStream bis = new ByteArrayInputStream(byteImage);
		BufferedImage image;
		Image scaledImage;
		BufferedImage imageBuf;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			image = ImageIO.read(bis);
			
			if (ratio <= 0.0f){
				System.out.println("Unvaild ratio!!");
				saveImage(bos.toByteArray(), dir, name);
			}
			
			scaledImage = image.getScaledInstance((int)(image.getWidth() * ratio), (int)(image.getHeight() * ratio), Image.SCALE_SMOOTH);
			imageBuf = new BufferedImage((int)(image.getWidth() * ratio), (int)(image.getHeight() * ratio), BufferedImage.TYPE_INT_RGB);
			imageBuf.getGraphics().drawImage(scaledImage, 0, 0, new Color(0,0,0), null);
			
			
			ImageIO.write(imageBuf, "jpg", bos);
		} catch(IOException e){
			e.printStackTrace();
		}

		saveImage(bos.toByteArray(), dir, name);
	}
}
