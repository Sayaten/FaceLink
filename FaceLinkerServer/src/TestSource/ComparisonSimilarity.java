package TestSource;

import Luxand.*; 
import Luxand.FSDK.FSDK_FaceTemplate;
import Luxand.FSDK.FSDK_FaceTemplate.ByReference;
import Luxand.FSDK.*; 
import Luxand.FSDKCam.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.sun.jna.Structure;

public class ComparisonSimilarity {
	private static final String[] ERRORCODE = 
	{
		"FSDKE_OK",
		"FSDKE_FAILED",
		"FSDKE_NOT_ACTIVATED",
		"FSDKE_OUT_OF_MEMORY",
		"FSDKE_INVALID_ARGUMENT",
		"FSDKE_IO_ERROR",
		"FSDKE_IMAGE_TOO_SMALL",
		"FSDKE_FACE_NOT_FOUND",
		"FSDKE_INSUFFICIENT_BUFFER_SIZE",	
		"FSDKE_UNSUPPORTED_IMAGE_EXTENSION",
		"FSDKE_CANNOT_OPEN_FILE",
		"FSDKE_CANNOT_CREATE_FILE",
		"FSDKE_BAD_FILE_FORMAT",
		"FSDKE_FILE_NOT_FOUND",
		"FSDKE_CONNECTION_CLOSED",
		"FSDKE_CONNECTION_FAILED",
		"FSDKE_IP_INIT_FAILED",
		"FSDKE_NEED_SERVER_ACTIVATION",
		"FSDKE_ID_NOT_FOUND",
		"FSDKE_ATTRIBUTE_NOT_DETECTED",
		"FSDKE_INSUFFICIENT_TRACKER_MEMORY_LIMIT",
		"FSDKE_UNKNOWN_ATTRIBUTE",
		"FSDKE_UNSUPPORTED_FILE_VERSION",
		"FSDKE_SYNTAX_ERROR",
		"FSDKE_PARAMETER_NOT_FOUND",
		"FSDKE_INVALID_TEMPLATE",
		"FSDKE_UNSUPPORTED_TEMPLATE_VERSION"
	};
	public static final String IMG_DIR = "/home/saya/Project/FLImages/";
	public static final String KEY = "WiqK4afCKZdMQRZBZBKTlQ+Rv/sdhksC0xAwgPjq/0QCZ3LTSjnc28EB1GojIEi2CKmfkTknG+IBJFJQ0oQWkLiZBz1YjAY8ZagNyuV2HF0ebKuIKfaZEBRTLwYx1k1DIJQzNI3yQmHn9sh5lyPKY/YRl4atM7yZhdcaqVsDAmE=";

	public static void main(String[] agrs){
		compareWithOneSample("Kim.jpg");
	}
	
	public static void compareWithOneSample(String sample){
		HImage imgOrg = new HImage();
		HImage imgCmp = new HImage();
		TFacePosition.ByReference fpOrg = new TFacePosition.ByReference();
		TFacePosition.ByReference fpCmp = new TFacePosition.ByReference();
		//TFacePosition fpOrg = null, fpCmp = null;
		FSDK_FaceTemplate.ByReference ftOrg = new FSDK_FaceTemplate.ByReference();
		FSDK_FaceTemplate.ByReference ftCmp = new FSDK_FaceTemplate.ByReference();
		int result;
		float[] similarity = new float[1];
		float maxSim = 0.0f;
		String pair = null;
		int nFile;
		String[] pics = null;
		String temp = null;
		
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter("BadPicsPair.txt"));
			
			pics = getFileNames();
			
			initFaceSDK();
			
			FSDK.SetFaceDetectionParameters(false, false, 500);
			
			for(int i = 0 ; i < pics.length ; ++i){
				if(pics[i].compareTo(IMG_DIR + sample) == 0){
					temp = pics[0];
					pics[0] = pics[i];
					pics[i] = temp;
				}
			}
			
			result = FSDK.LoadImageFromFile(imgOrg, pics[0]);
			if(result != FSDK.FSDKE_OK)
			{
				System.out.println("============= Input Sample is Bad ============\n");
				return;
			}
			result = FSDK.DetectFace(imgOrg, fpOrg);
			if(result != FSDK.FSDKE_OK)
			{
				System.out.println("============= Input Sample is Bad ============\n");
				return;
			}
			result = FSDK.GetFaceTemplateInRegion(imgOrg, fpOrg, ftOrg);
			if(result != FSDK.FSDKE_OK)
			{
				System.out.println("============= Input Sample is Bad ============\n");
				return;
			}
			for(int i = 1 ; i < pics.length ; ++i)
			{
				//System.out.println("=================== This pair ====================");
				//System.out.println(pics[i]+"\n");

				// load 1 imgs
				result = FSDK.LoadImageFromFile(imgCmp, pics[i]);
			
				//System.out.println("=================== Load Image ===================");
				//System.out.println(ERRORCODE[-result] + "\n");
			
				// find faces
				result = FSDK.DetectFace(imgCmp, fpCmp);
				if(result != FSDK.FSDKE_OK)
				{
					System.out.println("============= break this comparison =============\n");
					if(result != FSDK.FSDKE_OK) writer.write(pics[i]+"\n");

					FSDK.FreeImage(imgCmp);
					break;
				}
				
				//System.out.println("=================== Find Faces ===================");
				//System.out.println(ERRORCODE[-result]+"\n");
			
				FSDK.GetFaceTemplateInRegion(imgCmp, fpCmp, ftCmp);
				
				if(result != FSDK.FSDKE_OK)
				{
					System.out.println("============= break this comparison =============");
					System.out.println(pics[i]+"\n");
					FSDK.FreeImage(imgCmp);
					break;
				}

				FSDK.MatchFaces(ftOrg, ftCmp, similarity);
			
				System.out.println("=================== Similarity ==================");
				System.out.println("Similarity is " + similarity[0] * 100 + "%\n");

				if(similarity[0] >= maxSim)
				{
					maxSim = similarity[0];
					
					pair = pics[i];
				}
				FSDK.FreeImage(imgCmp);
			}
			writer.close();
			
			System.out.println("===================== Result ====================");
			System.out.println("Total " + pics.length + " files");
			System.out.println("Max Similarity: " + maxSim * 100 + "%");
			
			System.out.println(pics[0]);
			System.out.println(pair);
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void compareAllSample(){
		HImage imgOrg = null, imgCmp = null;
		TFacePosition fpOrg = null, fpCmp = null;
		FSDK_FaceTemplate ftOrg = null, ftCmp = null;
		int result1, result2;
		float similarity, maxSim;
		String pair1 = null, pair2 = null;
		int nFile;
		String[] pics = null;
		
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter("BadPicsPair.txt"));
			
			
			
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static String[] getFileNames()
	{
		String[] files = null;
		
		File dirFile = new File(IMG_DIR);
		File []fileList = dirFile.listFiles();
		
		files = new String[fileList.length];
		
		for(int i = 0 ; i < fileList.length ; ++i){
			files[i] = fileList[i].getParent() + "/" + fileList[i].getName();
		}
		
		return files;
	}
	
	public static int initFaceSDK()
	{
		int ret = 0;
		ret = FSDK.ActivateLibrary(KEY);

		FSDK.Initialize();

		return ret;
	}
}
