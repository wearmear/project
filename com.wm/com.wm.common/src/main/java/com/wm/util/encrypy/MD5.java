package com.wm.util.encrypy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class MD5 {
	/**
	 * 利用MD5算法加密
	 * 
	 * @param seqs
	 *            输入的密码
	 * @return 加密后的密码
	 */
	public static String encrypt(String str) {
		try {
			// 采用MD5算法加密
			MessageDigest md5Code = MessageDigest.getInstance("md5");
			byte[] bTmp = md5Code.digest(str.getBytes());
			// 采用Base64算法将加密后的byte[]转换成string
			BASE64Encoder base64 = new BASE64Encoder();
			str = base64.encode(bTmp);
			return str;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

}
