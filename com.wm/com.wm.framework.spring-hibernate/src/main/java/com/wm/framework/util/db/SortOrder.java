package com.wm.framework.util.db;


/**描述：<br>
 * 作者：王小欢 <br>
 * 修改日期：2014年2月11日下午5:22:32 <br>
 * E-mail:  <br> 
 */
public enum SortOrder {
	Asc(1, "asc"),//升序排列
	Desc(2, "desc");//降序排列
	
	private int constant;//排序方式标记
	private String name;//排序方式名称
	
	private SortOrder(int status, String name){
		this.constant = status;
		this.name = name;
	}
	
	public int getConstant(){
		return this.constant;
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		return name;
	}
	
	public static SortOrder getInstance(int mode)
    {
        SortOrder result = null;
        for(SortOrder value : values()) {
            if(value.constant == mode) {
                result = value;
                break;
            }
        }
        return result;
    }

    public static SortOrder getInstance(String name)
    {
        SortOrder result = null;
        for(SortOrder value : values()) {
            if(value.name.equalsIgnoreCase(name)) {
                result = value;
                break;
            }
        }
        return result;
    }
}
