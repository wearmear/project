package com.wm.common.lang;

import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wm.common.exception.ParameterException;

/**
 * 
 * Title 字符串处理工具类<br>
 * Description <br>
 * 
 * @Company SuperMap Software Co., Ltd.<br>
 * @Copyright Copyright (c) 2013<br>
 * 
 * @version 1.0.0,2013-6-23
 * @since JDK1.6
 * @author zhangguoping QQ:346258091 mailto:zhangguoping@supermap.com
 * 
 */
public class StringUtil {
	private static String[] charsetArray = new String[] { "UTF-8", "GBK",
			"GB2312" };

	/**
	 * 将转换过一次的乱码字符串解码
	 * 
	 * @param s
	 *            乱码字符串
	 * @return 解码后的中文
	 * @author wangmeng
	 */
	public static String matchesString(String s) {
		String buffer = s;
		if (s != null && !"".equals(s.trim())) {
			String regex = ".*?[\\u4e00-\\u9fa5].*?|[{][\\s]*[}]";
			boolean flag = s.matches(regex);

			int i = 0;
			// 处理浏览器发送的请求
			while (!flag && i < charsetArray.length) {
				try {
					String newStr = new String(s.getBytes("ISO8859-1"),
							charsetArray[i]);
					if (i == 0) {
						// 缓存第一次的请求
						buffer = newStr;
					}
					i++;
					flag = newStr.matches(regex);
					if (flag) {
						return newStr;
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			if (!flag) {
				// 首先使用转码的方式测试
				try {
					String str = null;
					if (s.contains("%22") || s.contains("%7B")) {
						str = java.net.URLDecoder.decode(s, "UTF-8");
					} else {
						str = s;
					}
					flag = str.matches(regex);
					if (flag) {
						return str;
					} else if (buffer.indexOf("%22") != -1
							&& str.indexOf("{") != -1) {
						return str;
					}
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
		}
		return buffer;
	}

	/**
	 * 将字母转换位阿拉伯数字
	 * 
	 * @param character
	 *            字母
	 * @return 阿拉伯数字
	 * @author wangmeng
	 */
	public static int charToInt(String character) {
		return character.toUpperCase().hashCode() - 64;
	}

	/**
	 * 将String数组转int数组
	 * 
	 * @param arr
	 *            String数组
	 * @return int数组
	 * @author wangmeng
	 */
	public static Integer[] convert(String[] arr) {
		Integer[] result = new Integer[arr.length];
		for (int i = 0; i < arr.length; i++) {
			String item = arr[i];
			if (null != item && "".equals(item)) {
				result[i] = new Integer(Integer.parseInt(arr[i]));
			} else {
				result[i] = null;
			}
		}
		return result;
	}

	/**
	 * 获取随机不重复文件夹名称
	 * 
	 * @return
	 */
	public static String getRandomFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS");
		String cacheName = sdf.format(new Date()) + "."
				+ new Random().nextInt(10000);
		return cacheName;
	}

	/**
	 * null值转空串
	 * 
	 * @param input
	 * @return
	 */
	public static String null2EmptyString(String input) {
		if (input == null) {
			return "";
		} else {
			return input;
		}
	}

	/**
	 * 数字转汉字<br>
	 * 注意：暂时只支持零和正整数，且不超过1亿
	 * 
	 * @param number
	 * @return
	 * @throws Exception
	 */
	public static String number2Chinese(int number) throws ParameterException {
		if (number < 0) {
			throw new ParameterException("输入数字错误");
		}

		if (number == 0) {
			return "零";
		} else {
			// 处理正整数
			String input = ("" + number).trim();
			int len = input.length();

			String num = "";
			String num2 = "";
			if (len > 4) {
				num = input.substring(0, len - 4);
				num2 = input.substring(len - 4, len);
				num = dashu(num);
				num2 = xiaoshu(num2);
				if (num.substring(num.length() - 1, num.length()).equals(
						num2.substring(0, 1))) {
					num = num.substring(0, num.length() - 1);
				}
				return num + num2;
			} else {
				return xiaoshu(input);
			}
		}
	}

	private static String dashu(String input) {
		String s1 = "零一二三四五六七八九";
		String s2 = "零个十百千亿";
		String num = input;
		String unit = "";
		String result = "";
		String num1 = "";
		int len = num.length();
		int n = 0;
		int tag = 0;
		for (int i = 0; i < len - 1; i++) {
			n = Integer.parseInt(num.substring(i, i + 1));
			if (n == 0) {
				if (tag == 1) {
					tag = 1;
					continue;
				} else {
					tag = 1;
					result = result.concat("零");
					continue;
				}
			}
			num1 = s1.substring(n, n + 1);
			n = len - i;
			unit = s2.substring(n, n + 1);
			result = result.concat(num1).concat(unit);
			tag = 0;
		}
		n = Integer.parseInt(num.substring(len - 1, len));
		if (n != 0) {
			num = s1.substring(n, n + 1);
			result = result.concat(num) + "万";
		} else {
			if (tag == 1) {
				n = result.length();
				result = result.substring(0, n - 1) + "万零";
			} else {
				result = result + "万零";
			}
		}
		return result;
	}

	private static String xiaoshu(String input) {
		String s1 = "零一二三四五六七八九";
		String s2 = "零个十百千";
		String num = input;
		String unit = "";
		String result = "";
		String num1 = "";
		int len = num.length();
		int n = 0;
		int tag = 0;
		for (int i = 0; i < len - 1; i++) {
			n = Integer.parseInt(num.substring(i, i + 1));
			if (n == 0) {
				if (tag == 1) {
					tag = 1;
					continue;
				} else {
					tag = 1;
					result = result.concat("零");
					continue;
				}
			}
			num1 = s1.substring(n, n + 1);
			n = len - i;
			unit = s2.substring(n, n + 1);
			if (len == 2 && num1.equals("一")) {
				result = result.concat(unit);
			} else {
				result = result.concat(num1).concat(unit);
			}
			tag = 0;
		}
		n = Integer.parseInt(num.substring(len - 1, len));
		if (n != 0) {
			num = s1.substring(n, n + 1);
			result = result.concat(num);
		} else {
			if (tag == 1) {
				n = result.length();
				result = result.substring(0, n - 1);
			}
		}
		return result;
	}

	/**
	 * 去掉汉字乱码
	 * 
	 * @param input
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String cleanISOMessy(String input)
			throws UnsupportedEncodingException {
		if (input == null || "".equals(input)) {
			return input;
		}
		String result = null;
		boolean messy = hasMessyCode(input);
		if (messy) {
			result = new String(input.getBytes("iso-8859-1"), "gbk");
			messy = hasMessyCode(result);
			if (messy) {
				result = new String(input.getBytes("iso-8859-1"), "utf-8");
				messy = hasMessyCode(result);
				if (messy) {
					// 处理失败
					return input;
				}
			}
		}
		return result;
	}

	/**
	 * 判断是否含有乱码
	 * 
	 * @param word
	 * @return
	 */
	public static boolean hasMessyCode(String word) {
		boolean messyCode = false;
		char[] chars = word.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char character = chars[i];
			UnicodeBlock block = UnicodeBlock.of(character);
			// 正常字符只含有拉丁，汉字，全角字符
			if (!block.equals(UnicodeBlock.BASIC_LATIN)
					&& !block.equals(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
					&& !block
							.equals(UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)) {
				// 判断为乱码
				messyCode = true;
				return messyCode;
			}
		}
		return messyCode;
	}

	/**
	 * 方法名称: hasCn<br>
	 * 描述：判断字符串中是否包含中文 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年11月6日下午3:07:37
	 * 
	 * @param keyword
	 * @return
	 */
	public static boolean isAllCn(String keyword) {
		Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");
		Matcher matcher = pattern.matcher(keyword);
		return matcher.matches();
	}

	public static boolean isHasCn(String keyword) {
		Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher matcher = pattern.matcher(keyword);
		return matcher.find();
	}
}
