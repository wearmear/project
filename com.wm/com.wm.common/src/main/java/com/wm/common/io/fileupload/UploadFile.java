package com.wm.common.io.fileupload;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.wm.common.exception.ParameterException;

public class UploadFile {
	private static final Logger LOGGER = Logger.getLogger(UploadFile.class);

	/**
	 * 文件上传
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ParameterException
	 * @throws ServletException
	 * @throws IOException
	 */
	public File upload(HttpServletRequest request, UploadFileParam ufp)
			throws ParameterException {
		File uploadFile = null;

		// 判断
		if (!ServletFileUpload.isMultipartContent(request)) {
			String warn = "缺少上传的文件！";
			throw new ParameterException(warn);
		}

		// 处理文件
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// Set factory constraints
		factory.setSizeThreshold(ufp.sizeThreshold);

		// 初始化上传文件的缓存目录,必须是绝对路径
//		String projectPath = this.getServletConfig().getServletContext()
//				.getRealPath("/");
		String classesPath = new File(UploadFile.class.getResource("/").getPath())
		        .getAbsolutePath().replaceAll("%20", " ");
		String projectPath = classesPath.substring(0, classesPath.length()
		        - "WEB-INF/classes".length());
		String cacheFolderPath = projectPath + ufp.uploadCache;
		File cacheFolder = new File(cacheFolderPath);
		if (!cacheFolder.isDirectory()) {
			cacheFolder.mkdirs();
		}
		factory.setRepository(cacheFolder);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		upload.setHeaderEncoding(ufp.fileEncoding);
		// Set overall request size constraint
		upload.setSizeMax(ufp.maxSize);

		// Parse the request
		List<FileItem> items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			String warn = "解析上传文件时发生异常！";
			LOGGER.warn(warn, e);
			throw new ParameterException(warn);
		}

		// Process the uploaded items
		Iterator<FileItem> iter = items.iterator();
		int fileNum = 0;
		while (iter.hasNext()) {
			FileItem fileItem = iter.next();
			
			if (!fileItem.isFormField()) {
				int suffixIndex = fileItem.getName().lastIndexOf(".");
				String suffix = fileItem.getName().substring(suffixIndex);
				
				boolean isAllowUpload = false;
				for (String allowExtension : ufp.allowExtensions) {
					if (suffix.equalsIgnoreCase(allowExtension)) {
						isAllowUpload = true;
						break;
					}
				}
				if (!isAllowUpload) {
					String warn = "不允许的上传文件类型：" + suffix;
					LOGGER.warn(warn);
					throw new ParameterException(warn);
				}

				String fileName = fileItem.getName().substring(0, suffixIndex);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String fileFullName = fileName + "_" + sdf.format(new Date())
						+ suffix;
				String uploadFolderPath = projectPath + ufp.uploadFolder;
				File uploadFolder = new File(uploadFolderPath);
				if (!uploadFolder.isDirectory()) {
					uploadFolder.mkdirs();
				}
				uploadFile = new File(uploadFolderPath + "/" + fileFullName);
				try {
					fileItem.write(uploadFile);
					LOGGER.info("文件上传成功:" + fileFullName);
				} catch (Exception e) {
					String warn = "文件上传失败:" + fileName;
					LOGGER.warn(warn, e);
					throw new ParameterException(warn);
				}

				fileNum++;
				if (fileNum>=ufp.fileNumMax) {
					break;
				}
			}
		}

		return uploadFile;
	}
}
