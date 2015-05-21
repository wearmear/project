package com.wm.framework.util.db;

/**描述：<br>
 * 作者：王小欢 <br>
 * 修改日期：2014年2月11日下午5:22:28 <br>
 * E-mail:  <br> 
 */
public enum CompareOperator {
	Equal("="),//相等
	Like("like"),//模糊查询
	In("in"),//in
	NotIn("not in"),
	MoreThan(">="),//大于
	LessThan("<="),//小于
	IsNull("is"),
	NotEqual("!="),
	OR("or");
	private String operator;//操作
	public String getOperator() {
		return operator;
	}

	private CompareOperator(String operator){
		this.operator = operator;
	}
	
	public String toString(){
		return operator;
	}
}
