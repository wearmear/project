package com.wm.framework.util.db;


/**
 * 描述：<br>
 * 作者：王猛 <br>
 * 修改日期：2015年3月23日下午1:46:53 <br>
 * E-mail: <br>
 */
public class SearchCondition {
	private String field;// 检索条件
//	private List<Condition> conditions = new ArrayList<Condition>();
	private CompareType compareType;// 比较操作
	private Object value;// 检索条件要比较的对象

	/**
	 * 定义一个查询条件
	 * 
	 * @param field
	 * @param compareOperator
	 * @param value
	 */
	public SearchCondition(String field, CompareType compareType, Object value) {
		this.field = field;
		this.compareType = compareType;
		this.value = value;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	public CompareType getCompareType() {
		return compareType;
	}

	public void setCompareType(CompareType compareType) {
		this.compareType = compareType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
