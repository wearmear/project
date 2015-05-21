package com.wm.common.io.fileupload;

public class UploadFileParam {
	String uploadFolder = "upload";
	String[] allowExtensions = { ".xls", ".xlsx" };
	String uploadCache = "upload/cache";
	String fileEncoding = "UTF-8";
	int maxSize = 1024 * 1024 * 2;
	int sizeThreshold = 1024 * 1024 * 1;
	int fileNumMax = 1;
}
