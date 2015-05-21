package com.wm.common.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesExpand extends Properties {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(PropertiesExpand.class);
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
	private synchronized void saveFile(String path, String comments) throws IOException {
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
	public synchronized void loadByAbsolutePath(String absolutePath) throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(absolutePath));
		BufferedReader bf = new BufferedReader(new InputStreamReader(in, charsetName));
		this.load(bf);
		in.close();
		this.print();
	}

	/**
	 * 相对项目目录加载properties文件,开头必须是目录分隔符
	 * 
	 * @param relativePath
	 *            相对路径
	 * @return
	 * @throws IOException
	 */
	public synchronized void loadByRelativePath(String relativePath) throws IOException {
		try {
			// InputStream is=this.getClass().getResourceAsStream(relativePath);
			InputStream is = ClassLoader.getSystemResourceAsStream(relativePath);
			InputStreamReader isr = new InputStreamReader(is, charsetName);
			BufferedReader bf = new BufferedReader(isr);
			this.load(bf);
			if (is != null) {
				is.close();
			}
			this.print();
		} catch (UnsupportedEncodingException e) {
			LOGGER.warn("读取文件不支持的编码格式:" + charsetName);
			throw e;
		} catch (FileNotFoundException e) {
			LOGGER.warn("找不到指定文件:" + relativePath);
			throw e;
		} catch (IOException e) {
			LOGGER.warn("读取文件异常", e);
			throw e;
		}
	}

	public void print() {
		LOGGER.debug("------------print in");
		Enumeration<?> enu = this.propertyNames();
		while (enu.hasMoreElements()) {
			Object key = enu.nextElement();
			LOGGER.debug(key + ":" + this.getProperty(key.toString()));
		}
		LOGGER.debug("------------print out");
	}

	public static void main(String[] args) throws Exception {
		PropertiesExpand pe = new PropertiesExpand();
		String path = "/resources/config/distance/DistanceMultiComparisionConfig.properties";
		pe.loadByRelativePath(path);
		pe.print();
	}

}
