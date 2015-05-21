package com.wm.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Properties;

public class PropertiesExpand extends Properties {
	private static final long serialVersionUID = 1L;
	private String charsetName = "UTF-8";

	public PropertiesExpand() {
	}

	public PropertiesExpand(String charsetName) {
		this.charsetName = charsetName;
	}

	/**
	 * 保存properties文件
	 * 
	 * @param path
	 *            绝对路径
	 * @throws Exception
	 */
	public synchronized void saveFile(String path) throws IOException {
		saveFile(path, "");
	}

	/**
	 * 保存properties文件
	 * 
	 * @param path
	 *            绝对路径
	 * @param comments
	 *            说明,注释的形式写入文件内部
	 * @throws Exception
	 */
	private synchronized void saveFile(String path, String comments)
			throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(path);
		this.store(fileOutputStream, comments);
		fileOutputStream.close();
	}

	/**
	 * 加载properties文件
	 * 
	 * @param filePath
	 *            绝对路径
	 * @return
	 * @throws IOException
	 */
	public synchronized void loadByAbsolutePath(String absolutePath)
			throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(
				absolutePath));
		BufferedReader bf = new BufferedReader(new InputStreamReader(in,
				charsetName));
		this.load(bf);
		in.close();
	}

	/**
	 * 相对classes目录加载properties文件
	 * 
	 * @param relativePath
	 *            相对classes目录的properties文件路径，支持相对classes目录的符号"../"
	 * @return
	 * @throws IOException
	 */
	public synchronized void loadByRelativePath(String relativePath)
			throws IOException {
		String parentTag = "../";
		if (-1 != relativePath.indexOf(parentTag)) {
			String absolutePath = null;
			String lastRelativePath = relativePath;
			String classesPath = this.getClass().getResource("/").getPath();
			File file = new File(classesPath);
			while (-1 != lastRelativePath.indexOf(parentTag)) {
				lastRelativePath = lastRelativePath.replaceFirst(parentTag, "");
				file = file.getParentFile();
			}
			String absoluteFolder = file.getAbsolutePath();
			if (-1 != absoluteFolder.indexOf("%")) {
				absoluteFolder = URLDecoder.decode(absoluteFolder,
						this.charsetName);
			}
			absolutePath = absoluteFolder + File.separator + lastRelativePath;
			this.loadByAbsolutePath(absolutePath);
		} else {
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream(relativePath);
			// InputStream is=this.getClass().getResourceAsStream(relativePath);
			// InputStream is = ClassLoader
			// .getSystemResourceAsStream(relativePath);
			InputStreamReader isr = new InputStreamReader(is, charsetName);
			BufferedReader bf = new BufferedReader(isr);
			this.load(bf);
			if (is != null) {
				is.close();
			}
		}
	}
}
