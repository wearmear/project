package com.wm.common.exception;

/***
 * 描述：参数错误<br>
 * 作者：王叶 <br>
 * 修改日期：2014-7-22下午2:51:10 <br>
 * E-mail: <br>
 */
@SuppressWarnings("serial")
public class ParameterException extends CustomException {
	public ParameterException() {
		super(ExceptionConstant.ERROR_PARAM);
	}

	public ParameterException(String ms) {
		super(ms);
	}
}
