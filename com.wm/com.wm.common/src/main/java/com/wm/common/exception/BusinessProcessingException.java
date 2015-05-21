package com.wm.common.exception;

/***
 * 描述：业务处理错误<br>
 * 作者：王叶 <br>
 * 修改日期：2014-7-22下午2:51:22 <br>
 * E-mail: <br>
 */
@SuppressWarnings("serial")
public class BusinessProcessingException extends CustomException {
	public BusinessProcessingException(String ms) {
		super(ms);
	}
}
