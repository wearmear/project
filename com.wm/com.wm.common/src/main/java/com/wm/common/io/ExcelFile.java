package com.wm.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelFile {
	private static final Logger LOGGER = Logger.getLogger(ExcelFile.class);

	public ExcelFile(String filePath) throws IOException {
		this.filePath = filePath;
		File file = new File(filePath);
		this.init(file);
	}

	public ExcelFile(File file) throws IOException {
		init(file);
	}

	private void init(File file) throws FileNotFoundException, IOException {
		String fileName = file.getName();
		String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
		try {
			if ("xls".equals(extension)) {
				workbook = new HSSFWorkbook(new FileInputStream(file));
			} else if ("xlsx".equals(extension)) {
				// 构造 XSSFWorkbook 对象，strPath 传入文件路径
				workbook = new XSSFWorkbook(new FileInputStream(file));
			} else {
				String warn = "不支持的文件类型";
				LOGGER.warn(warn);
				throw new IOException(warn);
			}
			filePath = file.getAbsolutePath();
		} catch (FileNotFoundException e) {
			String warn = "找不到已上传的文件：" + file.getAbsolutePath();
			LOGGER.warn(warn);
			throw e;
		} catch (IOException e) {
			String warn = "初始化工作表时发生错误";
			LOGGER.warn(warn, e);
			throw e;
		}
	}

	private Workbook workbook;
	private String filePath;

	// private HSSFWorkbook hwb;// xls 2003
	// private XSSFWorkbook xwb;// xlsx 2007

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 对外提供读取excel 的方法
	 * */
	public List<List<Object>> readExcel() {
		return this.readExcel(null, null, null);
	}

	/**
	 * 读取excel内容
	 * 
	 * @param sheetIndex
	 *            sheet索引
	 * @param rowIndexStart
	 *            行开始索引，读取包括当前行索引
	 * @param rowIndexEnd
	 *            行结束索引，不读取包括当前行索引
	 * @return
	 * @throws IOException
	 */
	public List<List<Object>> readExcel(Integer sheetIndex, Integer rowIndexStart, Integer rowIndexEnd) {
		if (null != workbook) {
			if (workbook instanceof HSSFWorkbook) {
				return read2003Excel(sheetIndex, rowIndexStart, rowIndexEnd);
			} else if (workbook instanceof XSSFWorkbook) {
				return read2007Excel(sheetIndex, rowIndexStart, rowIndexEnd);
			} else {
				LOGGER.warn("不支持的文件类型: " + filePath);
				return null;
			}
		} else {
			LOGGER.warn("workbook不能为null");
			return null;
		}
	}

	/**
	 * 读取 office 2003 excel
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private List<List<Object>> read2003Excel(Integer sheetIndex, Integer rowIndexStart, Integer rowIndexEnd) {
		if (null == sheetIndex) {
			sheetIndex = 0;
		}
		List<List<Object>> list = new LinkedList<List<Object>>();
		HSSFWorkbook hwb = (HSSFWorkbook) workbook;
		HSSFSheet sheet = hwb.getSheetAt(sheetIndex);
		Object value = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		if (null == rowIndexStart) {
			rowIndexStart = sheet.getFirstRowNum();
		}
		if (null == rowIndexEnd) {
			rowIndexEnd = sheet.getPhysicalNumberOfRows();
		}
		for (int i = rowIndexStart; i < rowIndexEnd; i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			List<Object> linked = new LinkedList<Object>();
			for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
				cell = row.getCell(j);
				if (cell == null) {
					linked.add(null);
					continue;
				}
				// DecimalFormat df = new DecimalFormat("0");// 格式化 number
				// String
				// // 字符
				// SimpleDateFormat sdf = new SimpleDateFormat(
				// "yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
				// DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
				switch (cell.getCellType()) {
				// case XSSFCell.CELL_TYPE_STRING:
				// value = cell.getStringCellValue();
				// break;
				case XSSFCell.CELL_TYPE_NUMERIC:
					double num = cell.getNumericCellValue();
					BigDecimal bd = new BigDecimal(num);
					value = bd.toPlainString();
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					value = cell.getBooleanCellValue();
					break;
				case XSSFCell.CELL_TYPE_BLANK:
					value = "";
					break;
				default:
					value = cell.toString();
				}
				// if (value == null || "".equals(value)) {
				// continue;
				// }
				linked.add(value);
			}
			list.add(linked);
		}
		return list;
	}

	/**
	 * 读取Office 2007 excel
	 * */
	private List<List<Object>> read2007Excel(Integer sheetIndex, Integer rowIndexStart, Integer rowIndexEnd) {
		List<List<Object>> list = new LinkedList<List<Object>>();
		// 读取第一章表格内容
		XSSFWorkbook xwb = (XSSFWorkbook) workbook;
		XSSFSheet sheet = xwb.getSheetAt(sheetIndex);
		Object value = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		if (null == rowIndexStart) {
			rowIndexStart = sheet.getFirstRowNum();
		}
		if (null == rowIndexEnd) {
			rowIndexEnd = sheet.getPhysicalNumberOfRows();
		}
		for (int i = rowIndexStart; i < rowIndexEnd; i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			List<Object> linked = new LinkedList<Object>();
			for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
				cell = row.getCell(j);
				if (cell == null) {
					linked.add(null);
					continue;
				}
				// DecimalFormat df = new DecimalFormat("0");// 格式化 number
				// String
				// // 字符
				// SimpleDateFormat sdf = new SimpleDateFormat(
				// "yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
				// DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
				switch (cell.getCellType()) {
				// case XSSFCell.CELL_TYPE_STRING:
				// value = cell.getStringCellValue();
				// break;
				case XSSFCell.CELL_TYPE_NUMERIC:
					double num = cell.getNumericCellValue();
					BigDecimal bd = new BigDecimal(num);
					value = bd.toPlainString();
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					value = cell.getBooleanCellValue();
					break;
				case XSSFCell.CELL_TYPE_BLANK:
					value = "";
					break;
				default:
					value = cell.toString();
				}
				// if (value == null || "".equals(value)) {
				// continue;
				// }
				linked.add(value);
			}
			list.add(linked);
		}
		return list;
	}

	public void saveExcel() {
		FileOutputStream fileOut = null;
		try {
			try {
				fileOut = new FileOutputStream(this.filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				this.workbook.write(fileOut);
				LOGGER.debug("保存成功");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != fileOut) {
				try {
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		String filePath = "D:/data-work/record/project/haier/distance 测距/question/" +
				"轮渡/" +
				"轮渡案例.xlsx"; // 文件绝对路径
		ExcelFile ef = null;
		try {
			ef = new ExcelFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int sheetIndex = 0;
		// int rowIndexStart = 2;
		// int rowIndexEnd = rowIndexStart + 1000;

		List<List<Object>> attributesList = ef.readExcel(sheetIndex, 0, 1);
		List<Object> attributes = attributesList.get(0);
		System.out.println(JSONArray.fromObject(attributes));
//		Workbook w = new HSSFWorkbook();
//		System.out.println(w instanceof XSSFWorkbook);
	}
}