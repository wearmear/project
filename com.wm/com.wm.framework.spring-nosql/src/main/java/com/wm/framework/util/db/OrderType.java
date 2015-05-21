package com.wm.framework.util.db;

/**
 * 描述：排序规则 <br>
 * 作者：王猛 <br>
 * 修改日期：2015年3月23日下午2:09:59 <br>
 * E-mail: <br>
 */
public enum OrderType {
	Asc(1), // 升序排列
	Desc(-1);// 降序排列

	private OrderType(int value) {
		this.value = value;
	}

	private int value;// 排序方式标记

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
