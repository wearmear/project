package com.wm.framework.sn.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import com.wm.framework.util.db.CompareType;
import com.wm.framework.util.db.SearchCondition;
import com.wm.framework.util.db.SortCondition;

public class DBUtil {
	public static Long buildLongID() {
		Long time = System.currentTimeMillis();
		int offset = 1000;
		int random = new Random().nextInt(offset);
		Long result = time * offset + random;
		return result;
	}

	public static BSONObject buildOrderBy(List<SortCondition> sortConditions) {
		BSONObject bsonOrderBy = null;
		if (null != sortConditions) {
			bsonOrderBy = new BasicBSONObject();
			for (int i = 0; i < sortConditions.size(); i++) {
				SortCondition sortCondition = sortConditions.get(i);
				bsonOrderBy.put(sortCondition.getField(), sortCondition.getOrderType().getValue());
			}
		}
		return bsonOrderBy;
	}

	public static BSONObject buildCondition(SearchCondition searchCondition) {
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		searchConditions.add(searchCondition);
		return buildCondition(searchConditions);
	}

	public static BSONObject buildCondition(List<SearchCondition> searchConditions) {
		BSONObject bsonMatcher = null;
		if (null != searchConditions) {
			bsonMatcher = new BasicBSONObject();
			for (int i = 0; i < searchConditions.size(); i++) {
				SearchCondition searchCondition = searchConditions.get(i);
				BSONObject bsonCondition = null;
				if (null != searchCondition) {
					bsonCondition = new BasicBSONObject();
					Object conditionValue = searchCondition.getValue();
					if (CompareType.Like.equals(searchCondition.getCompareType())) {
						Pattern obj = Pattern.compile(conditionValue.toString(), Pattern.CASE_INSENSITIVE);
						bsonMatcher.put(searchCondition.getField(), obj);
					} else {
						if (bsonMatcher.containsField(searchCondition.getField())) {
							bsonCondition.put(searchCondition.getCompareType().getValue(), conditionValue);
							for (int j = i - 1; j >= 0; j--) {
								if (searchConditions.get(j).getField().equals(searchCondition.getField())) {
									searchCondition = searchConditions.get(j);
									break;
								}
							}
							bsonCondition.put(searchCondition.getCompareType().getValue(), searchCondition.getValue());
							bsonMatcher.put(searchCondition.getField(), bsonCondition);
						} else {
							bsonCondition.put(searchCondition.getCompareType().getValue(), conditionValue);
							bsonMatcher.put(searchCondition.getField(), bsonCondition);
						}
					}
				}

			}
		}
		return bsonMatcher;
	}
	
	public static BSONObject buildSelector(List<String> searchFields) {
		BSONObject bsonSelector = null;
		if (null != searchFields) {
			bsonSelector = new BasicBSONObject();
			for (int i = 0; i < searchFields.size(); i++) {
				bsonSelector.put(searchFields.get(i), null);
			}
		}
		return bsonSelector;
	}
	
	public static String processSql(String sql, Object... params) {
		if (null != params) {
			for (int i = 0; i < params.length; i++) {
				Object param = params[i];
				if (null != param) {
					String value = null;
					if (param instanceof List) {
						@SuppressWarnings("unchecked")
						List<Object> paramList = (List<Object>) param;
						sql = processSql(sql, paramList.toArray());
						continue;
					} else if (param instanceof String) {
						value = "'" + param.toString() + "'";
					} else {
						value = param.toString();
					}
					sql = sql.replaceFirst("\\?", value);
				}
			}
		}
		return sql;
	}
	
	
}
