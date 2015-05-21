package com.wm.common.io;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/***
 * 
 * @author wangmeng
 *
 */
public class FileUtil {
	private static Logger LOGGER = Logger.getLogger(FileUtil.class);

	/**
	 * 根据行号和指定读取行数读取文件
	 * 
	 * @param filePath
	 *            要操作的文件
	 * @param lineNumber
	 *            【可选。默认1】第几行
	 * @param lineSize
	 *            【可选。默认值文件的总行数】行数
	 * @return 查询到的数据集合，查询不到数据返回空集合
	 * @throws IOException
	 */
	public static List<String> readByLineNum(final String filePath, Integer lineNumber, Integer lineSize) throws IOException, Exception {
		if (lineNumber == null) {
			lineNumber = 1;
		}

		List<String> result = new ArrayList<String>();
		File file = new File(filePath);
		InputStream is = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		if (!file.exists()) {
			file.createNewFile();
		}
		LineNumberReader lnr = new LineNumberReader(isr);

		String instring;
		while ((instring = lnr.readLine()) != null) {
			if (lnr.getLineNumber() >= lineNumber) {
				// instring = instring.replaceAll("[\\s*\t\n\r]", "");
				result.add(instring);
				if (lineSize != null && (lnr.getLineNumber() - lineNumber + 1) >= lineSize) {
					break;
				}
			}
		}
		is.close();
		return result;
	}

	public static List<String> convertStreamToList(InputStream is) throws IOException {
		if (is != null) {
			List<String> list = new ArrayList<String>();
			String line;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					// sb.append(line);
					list.add(line);
				}
			} finally {
				is.close();
			}
			return list;
		} else {
			return null;
		}
	}

	public static String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public static final byte[] convertStreamToByte(InputStream inStream) throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}

	public static void write(String file, String txt) {
		try {
			FileOutputStream os = new FileOutputStream(new File(file), true);
			os.write((txt + "\r\n").getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件复制
	 * 
	 * @param srcfile
	 *            源文件全路径
	 * @param destfile
	 *            复制目的位置全路径
	 * @throws IOException
	 */
	public static void copyFile(String srcfile, String destfile) throws IOException // 使用FileInputStream和FileOutStream
	{
		File file = new File(destfile);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileInputStream fi = new FileInputStream(srcfile);
		FileOutputStream fo = new FileOutputStream(destfile);
		byte data[] = new byte[fi.available()];
		fi.read(data);
		fo.write(data);
		fi.close();
		fo.close();
		LOGGER.debug("文件复制成功\n" + "srcfile:" + srcfile + "\ndestfile:" + destfile);
	}

	public static void main(String[] args) {
		String srcfile = "d:/a.smwu";
		String destfile = "d:/a/b.smwu";
		try {
			copyFile(srcfile, destfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
