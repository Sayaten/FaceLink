#include "LuxandFaceSDK.h"
#include "getAllFiles.h"
#include "faceComparison.h"

char ErrorCode[][60] = 
{
	"FSDKE_OK                               ",
	"FSDKE_FAILED                           ",
	"FSDKE_NOT_ACTIVATED                    ",
	"FSDKE_OUT_OF_MEMORY                    ",
	"FSDKE_INVALID_ARGUMENT                 ",
	"FSDKE_IO_ERROR                         ",
	"FSDKE_IMAGE_TOO_SMALL                  ",
	"FSDKE_FACE_NOT_FOUND                   ",
	"FSDKE_INSUFFICIENT_BUFFER_SIZE         ",	
	"FSDKE_UNSUPPORTED_IMAGE_EXTENSION      ",
	"FSDKE_CANNOT_OPEN_FILE                 ",
	"FSDKE_CANNOT_CREATE_FILE               ",
	"FSDKE_BAD_FILE_FORMAT                  ",
	"FSDKE_FILE_NOT_FOUND                   ",
	"FSDKE_CONNECTION_CLOSED                ",
	"FSDKE_CONNECTION_FAILED                ",
	"FSDKE_IP_INIT_FAILED                   ",
	"FSDKE_NEED_SERVER_ACTIVATION           ",
	"FSDKE_ID_NOT_FOUND                     ",
	"FSDKE_ATTRIBUTE_NOT_DETECTED           ",
	"FSDKE_INSUFFICIENT_TRACKER_MEMORY_LIMIT",
	"FSDKE_UNKNOWN_ATTRIBUTE                ",
	"FSDKE_UNSUPPORTED_FILE_VERSION         ",
	"FSDKE_SYNTAX_ERROR                     ",
	"FSDKE_PARAMETER_NOT_FOUND              ",
	"FSDKE_INVALID_TEMPLATE                 ",
	"FSDKE_UNSUPPORTED_TEMPLATE_VERSION     "
};

void compareWithOneSample(char* sample)
{
	HImage imgOrg, imgCmp;
	TFacePosition fpOrg, fpCmp;
	FSDK_FaceTemplate ftOrg, ftCmp;

	int result;
	float similarity = 0.0f, maxSim = 0.0f;
	char *pair = NULL;
	char *temp;

	int nFile;
	char **pics;

	std::ofstream fileOut;
	fileOut.open("BadPicsPair.txt");

	pics = getAllFiles(nFile);

	initFaceSDK();

	/* 
		set detection parameters
		first param : set comparing rotation ratio 
					  true == -30 ~ 30 degree, false == -15 ~ 15
		second param : get twisted ratio
					   true == get , false == not get
		third param : set internal resize ratio
	*/
	FSDK_SetFaceDetectionParameters(false, false, 500);

	for(int i = 0 ; i < nFile ; ++i)
	{
		if(!strcmp(pics[i] + strlen("./Pics/"), sample))
		{
			temp = pics[0];
			pics[0] = pics[i];
			pics[i] = temp;
			break;
		}
	}
	
	result = FSDK_LoadImageFromFile(&imgOrg, pics[0]);
	if(result != FSDKE_OK)
	{
		std::cout<<"============= Input Sample is Bad ============"<<std::endl<<std::endl;
		return;
	}
	result = FSDK_DetectFace(imgOrg, &fpOrg);
	if(result != FSDKE_OK)
	{
		std::cout<<"============= Input Sample is Bad ============"<<std::endl<<std::endl;
		return;
	}

	for(int i = 1 ; i < nFile ; ++i)
	{
		//std::cout<<"=================== This pair ===================="<<std::endl;
		//std::cout<<pics[i]<<" "<<pics[j]<<std::endl<<std::endl;

		// load 1 imgs
		result = FSDK_LoadImageFromFile(&imgCmp, pics[i]);
	
		//std::cout<<"=================== Load Image ==================="<<std::endl;
		//std::cout<<ErrorCode[-result1]<<std::endl<<std::endl;
	
		// find faces
		result = FSDK_DetectFace(imgCmp, &fpCmp);
		if(result != FSDKE_OK)
		{
			std::cout<<"============= break this comparison ============="<<std::endl<<std::endl;
			if(result != FSDKE_OK) fileOut<<pics[i]<<std::endl;

			FSDK_FreeImage(imgCmp);
			break;
		}
		
		//std::cout<<"=================== Find Faces ==================="<<std::endl;
		//std::cout<<ErrorCode[-result1]<<std::endl<<std::endl;
	
		FSDK_GetFaceTemplateInRegion(imgCmp, &fpCmp, &ftCmp);
		
		if(result != FSDKE_OK)
		{
			std::cout<<"============= break this comparison ============="<<std::endl;
			std::cout<<pics[i]<<std::endl<<std::endl;
			FSDK_FreeImage(imgCmp);
			break;
		}

		FSDK_MatchFaces(&ftOrg, &ftCmp, &similarity);
	
		std::cout<<"=================== Similarity =================="<<std::endl;
		std::cout<<"Similarity is "<<similarity * 100<<"%"<<std::endl<<std::endl;

		if(similarity >= maxSim)
		{
			maxSim = similarity;
			if(pair != NULL)
			{
				delete[] pair;

				pair = NULL;
			}
			pair = new char[strlen(pics[i]) + 1];

			strcpy(pair, pics[i]);
		}
		FSDK_FreeImage(imgCmp);
	}
	
	FSDK_FreeImage(imgOrg);

	std::cout<<"===================== Result ===================="<<std::endl;
	std::cout<<"Total "<<nFile<<" files"<< std::endl;
	std::cout<<"Max Similarity: "<<maxSim* 100<<"%"<<std::endl;
	
	std::cout<<pics[0]<<std::endl;
	std::cout<<pair<<std::endl;
	
	for(int i = 0 ; i < nFile ; ++i)
		delete[] pics[i];
	delete[] pics;
	delete[] pair;

	fileOut.close();

	getchar();
}

void compareAllSample()
{
	HImage imgOrg, imgCmp;
	TFacePosition fpOrg, fpCmp;
	FSDK_FaceTemplate ftOrg, ftCmp;

	int result1, result2;
	float similarity = 0.0f, maxSim = 0.0f;
	char *pair1 = NULL, *pair2 = NULL;

	int nFile;
	char **pics;

	std::ofstream fileOut;
	fileOut.open("BadPicsPair.txt");

	pics = getAllFiles(nFile);

	initFaceSDK();

	/* 
		set detection parameters
		first param : set comparing rotation ratio 
					  true == -30 ~ 30 degree, false == -15 ~ 15
		second param : get twisted ratio
					   true == get , false == not get
		third param : set internal resize ratio
	*/
	FSDK_SetFaceDetectionParameters(false, false, 500);

	for(int i = 0 ; i < nFile - 1; ++i)
	{
		for(int j = i + 1 ; j < nFile ; ++j)
		{
			//std::cout<<"=================== This pair ===================="<<std::endl;
			//std::cout<<pics[i]<<" "<<pics[j]<<std::endl<<std::endl;

			// load 2 imgs
			result1 = FSDK_LoadImageFromFile(&imgOrg, pics[i]);
			result2 = FSDK_LoadImageFromFile(&imgCmp, pics[j]);
		
			//std::cout<<"=================== Load Image ==================="<<std::endl;
			//std::cout<<ErrorCode[-result1]<<std::endl;
			//std::cout<<ErrorCode[-result2]<<std::endl<<std::endl;
		
			// find faces
			result1 = FSDK_DetectFace(imgOrg, &fpOrg);
			result2 = FSDK_DetectFace(imgCmp, &fpCmp);
			if(result1 != FSDKE_OK || result2 != FSDKE_OK)
			{
				std::cout<<"============= break this comparation ============="<<std::endl<<std::endl;
				if(result1 != FSDKE_OK) fileOut<<pics[i]<<std::endl;
				if(result2 != FSDKE_OK) fileOut<<pics[j]<<std::endl;

				FSDK_FreeImage(imgOrg);
				FSDK_FreeImage(imgCmp);
				break;
			}
			
			//std::cout<<"=================== Find Faces ==================="<<std::endl;
			//std::cout<<ErrorCode[-result1]<<std::endl;
			//std::cout<<ErrorCode[-result2]<<std::endl<<std::endl;
		
			FSDK_GetFaceTemplateInRegion(imgOrg, &fpOrg, &ftOrg);
			FSDK_GetFaceTemplateInRegion(imgCmp, &fpCmp, &ftCmp);
			
			if(result1 != FSDKE_OK || result2 != FSDKE_OK)
			{
				std::cout<<"============= break this comparation ============="<<std::endl;
				std::cout<<pics[i]<<" "<<pics[j]<<std::endl<<std::endl;
				FSDK_FreeImage(imgOrg);
				FSDK_FreeImage(imgCmp);
				break;
			}

			FSDK_MatchFaces(&ftOrg, &ftCmp, &similarity);
		
			std::cout<<"=================== Similarity ==================="<<std::endl;
			std::cout<<"Similarity is "<<similarity * 100<<"%"<<std::endl<<std::endl;

			if(similarity >= maxSim)
			{
				maxSim = similarity;
				if(pair1 != NULL && pair2 != NULL)
				{
					delete[] pair1;
					delete[] pair2;

					pair1 = NULL;
					pair2 = NULL;
				}
				pair1 = new char[strlen(pics[i]) + 1];
				pair2 = new char[strlen(pics[j]) + 1];

				strcpy(pair1, pics[i]);
				strcpy(pair2, pics[j]);
			}

			FSDK_FreeImage(imgOrg);
			FSDK_FreeImage(imgCmp);
		}
	}

	std::cout<<"===================== Result ====================="<<std::endl;
	std::cout<<"Total "<<nFile<<" files"<< std::endl;
	std::cout<<"Max Similarity: "<<maxSim* 100<<"%"<<std::endl;
	
	std::cout<<pair1<<std::endl;
	std::cout<<pair2<<std::endl;
	

	for(int i = 0 ; i < nFile ; ++i)
		delete[] pics[i];
	delete[] pics;
	delete[] pair1;
	delete[] pair2;

	fileOut.close();

	getchar();
}

int initFaceSDK()
{
	int ret = 0;
	ret = FSDK_ActivateLibrary(KEY);

	FSDK_Initialize("");

	return ret;
}

