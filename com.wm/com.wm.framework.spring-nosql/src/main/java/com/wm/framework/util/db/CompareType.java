package com.wm.framework.util.db;

/**
 * 描述：查询规则<br>
 * 作者：王猛 <br>
 * 修改日期：2015年3月23日下午2:11:25 <br>
 * E-mail: <br>
 */
public enum CompareType {
	Equal("$et"), //等于
	UnEqual("$ne"),//不等于
	Like("_like"), // 模糊查询
//	In("$in"), // in
//	NotIn("not in"),
	MoreThan("$gt"), // 大于
	LessThan("$lt"), // 小于
//	IsNull("is"), 
	Inverse("$not"); //非
//	OR("$or");

	private CompareType(String value) {
		this.value = value;
	}

	private String value;// 操作

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
