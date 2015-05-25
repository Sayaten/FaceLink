#include "getAllFiles.h"
 
char** getAllFiles(int &nFiles)
{
	char** files;         
	char* dir = "./Pics/";
	nFiles = 0;
	
	struct _finddata_t c_file;
	intptr_t hFile;

	// *.* ����������������������˴ϴ�.
	// �������"aaa\*.*"
	if( (hFile = _findfirst( ".\\Pics\\*.jpg", &c_file )) == -1L )
		std::cout << "No file(s) in that directory!" << std::endl<<std::endl;
	else
	{
		// ���ڿ�����
		do
		{
			++nFiles;
		} while( _findnext( hFile, &c_file ) == 0 );
		_findclose(hFile);
	}

	files = new char*[nFiles];
	nFiles = 0;

	if( (hFile = _findfirst( ".\\Pics\\*.jpg", &c_file )) == -1L )
		return NULL;
	else
	{
		// ���ڿ�����
		do
		{
			files[nFiles] = new char[strlen(dir) + strlen(c_file.name) + 1];
			strcpy(files[nFiles], dir);
			strcpy(files[nFiles] + strlen(dir), c_file.name);
			++nFiles;
		} while( _findnext( hFile, &c_file ) == 0 );
		_findclose(hFile);
	}
	
	for ( int i = 0; i < nFiles; i++ )
	{
		std::cout << files[i] << std::endl;
	}
	std::cout << std::endl;
	std::cout << "=================== read files ===================" << std::endl;
	return files;
}