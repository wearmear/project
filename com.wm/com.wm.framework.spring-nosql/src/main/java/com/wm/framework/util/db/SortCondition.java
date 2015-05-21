package com.wm.framework.util.db;

/**
 * 描述：排序条件<br>
 * 作者：王猛 <br>
 * 修改日期：2015年3月23日下午2:09:42 <br>
 * E-mail:  <br>
 */
public class SortCondition {
	private String field;// 排序的列
	private OrderType orderType = OrderType.Asc;// 排序方式,默认升序

	/**
	 * 
	 * @param field	要排序的字段
	 */
	public SortCondition(String field) {
		this.field = field;
	}
	
	/**
	 * 
	 * @param field
	 *            要排序的字段
	 * @param orderType
	 *            排序规则
	 */
	public SortCondition(String field, OrderType orderType) {
		this.field = field;
		this.orderType = orderType;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
}
