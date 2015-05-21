package com.wm.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/***
 * 
 * @author wangmeng
 * 
 */
public class ZipUtil {

	private static final Logger LOGGER = Logger.getLogger(ZipUtil.class);

	/**
	 * 功能:压缩多个文件成一个zip文件
	 * 
	 * @param srcfiles
	 *            ：源文件列表
	 * @param zipfile
	 *            ：压缩后的文件
	 * @throws Exception
	 */
	public static void zipFiles(String fileFullPath, String zipFullPath) throws Exception {
		ArchiveOutputStream os = null;
		try {
			final OutputStream out = new FileOutputStream(zipFullPath);
			os = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, out);

			LOGGER.debug("压缩：" + fileFullPath);
			File file = new File(fileFullPath);
			os.putArchiveEntry(new ZipArchiveEntry(file.getName()));
			IOUtils.copy(new FileInputStream(file), os);
			os.closeArchiveEntry();
			os.close();
		} catch (IOException e) {
			LOGGER.error("", e);
			throw e;
		} finally {
			if (os != null) {
				os.closeArchiveEntry();
				os.close();
			}
		}
		LOGGER.debug("压缩成功：" + zipFullPath);
	}

	public static void zipFiles(List<String> fileFullPaths, String zipFullPath) throws Exception {
		try {
			final OutputStream out = new FileOutputStream(zipFullPath);
			ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, out);

			for (String fileFullPath : fileFullPaths) {
				LOGGER.debug("压缩：" + fileFullPath);
				File file = new File(fileFullPath);
				os.putArchiveEntry(new ZipArchiveEntry(file.getName()));
				IOUtils.copy(new FileInputStream(file), os);
				os.closeArchiveEntry();
			}
			os.close();
		} catch (IOException e) {
			LOGGER.error("", e);
			throw e;
		}
		LOGGER.debug("压缩成功：" + zipFullPath + "\n压缩文件数：" + fileFullPaths.size());
	}

	/**
	 * 把一个zip文件解压到一个指定的目录中
	 * 
	 * @param zipFullPath
	 *            zip文件抽象地址
	 * @param outFolder
	 *            目录绝对地址
	 * @throws Exception
	 * @throws IOException
	 */
	public static void unZipToFolder(String zipFullPath, String outFolder) throws Exception, IOException {
		File zipfile = new File(zipFullPath);
		if (zipfile.exists()) {
			try {
				String zipFileName = zipfile.getName();
				if (!zipFileName.toLowerCase().endsWith(".zip")) {
					throw new Exception("非zip格式文件");
				}

				outFolder = outFolder + File.separator;
				FileUtils.forceMkdir(new File(outFolder));

				ZipFile zf = new ZipFile(zipfile, "GBK");
				Enumeration<ZipArchiveEntry> zipArchiveEntrys = zf.getEntries();
				while (zipArchiveEntrys.hasMoreElements()) {
					ZipArchiveEntry zipArchiveEntry = (ZipArchiveEntry) zipArchiveEntrys.nextElement();
					if (zipArchiveEntry.isDirectory()) {
						FileUtils.forceMkdir(new File(outFolder + zipArchiveEntry.getName() + File.separator));
					} else {
						InputStream in = zf.getInputStream(zipArchiveEntry);
						OutputStream out = FileUtils.openOutputStream(new File(outFolder + zipArchiveEntry.getName()));
						IOUtils.copy(in, out);
					}
				}
			} catch (ZipException e) {
				LOGGER.error("", e);
				throw e;
			} catch (IOException e) {
				LOGGER.error("", e);
				throw e;
			}
		} else {
			String info = "指定的解压文件不存在：" + zipFullPath;
			LOGGER.info(info);
			throw new Exception(info);
		}
	}

	/**
	 * 获取zip文件内的文件字节码
	 * 
	 * @param zipFileFullPath
	 * @param entryFileInnerPath
	 * @return
	 */
	public byte[] getEntryBytes(String zipFileFullPath, String entryFileInnerPath) {
		byte[] bytes = null;
		try {
			java.util.zip.ZipFile file = new java.util.zip.ZipFile(zipFileFullPath);
			ZipEntry entry = file.getEntry(entryFileInnerPath); // 假如压缩包里的文件名是1.xml
			InputStream in = file.getInputStream(entry);
			bytes = FileUtil.convertStreamToByte(in);
		} catch (IOException e) {
			LOGGER.error("getEntryBytes异常", e);
		}
		return bytes;
	}

	public static void main(String[] args) {
		ZipUtil t = new ZipUtil();
		String zipFileFullPath = "D:/data-work/mapTiles/data/cache_5.0-dpi_96-original/7c" + "/10_0006_0003_192.168.23.12_1_2013_09_03_22_21_36.zip";
		String entryFileInnerPath = "map/map/P_256_5082FA5CFIX/458984/3/7/483x996.png";
		byte[] bytes = t.getEntryBytes(zipFileFullPath, entryFileInnerPath);
		LOGGER.info(bytes.length);
	}
}
