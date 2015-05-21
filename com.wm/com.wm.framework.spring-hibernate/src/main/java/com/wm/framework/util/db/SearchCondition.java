/**
 * SearchCondition.java
 *
 * 功  能： 这是一个与检索信息有关的类
 * 类名： SearchCondition
*/
package com.wm.framework.util.db;




/**
 * 本类是与检索信息有关的类
 * 
 * @author  
 * @version
 */
public class SearchCondition {
	private String field;//检索条件
	private CompareOperator operator;//比较操作
	private Object value;//检索条件要比较的对象
	
	public SearchCondition(String field, CompareOperator compareOperator, Object value){
		this.field = field;
		this.operator = compareOperator;
		this.value = value;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public CompareOperator getOperator() {
		return operator;
	}
	public void setOperator(CompareOperator operator) {
		this.operator = operator;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
