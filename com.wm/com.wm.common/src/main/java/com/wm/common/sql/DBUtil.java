package com.wm.common.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.supermap.sql.config.DBConfig;

/**
 * 数据库查询公共类
 * 
 * @author wangmeng
 * 
 */
public class DBUtil {

	private static Logger LOGGER = Logger.getLogger(DBUtil.class);
	private static PreparedStatement ps;
	private static Connection conn = null;

	static {
		if (null != DBConfig.DRIVER) {
			try {
				Class.forName(DBConfig.DRIVER);
				conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USER,
						DBConfig.PASSWORD);
				LOGGER.info("isConnection: " + !conn.isClosed());
			} catch (ClassNotFoundException e) {
				LOGGER.error(null, e);
			} catch (SQLException e) {
				LOGGER.error(null, e);
			}
		}
	}

	/**
	 * 从数据库获取数据
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public JSONArray findByPage(int pageNum, int pageSize, String sql) {
		LOGGER.info("执行findByPage\npageNum:" + pageNum + "\tpageSize:"
				+ pageSize + "\nsql:" + sql);
		JSONArray list = new JSONArray();
		try {
			if (conn.isClosed()) {
				this.reConnection(DBConfig.RECONNECTION_COUNT,
						DBConfig.RECONNECTION_MILLIS);
			}
			sql = buildPageableSql(pageNum, pageSize, sql);
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()) {
				JSONObject item = new JSONObject();
				for (int i = 1; i <= columnCount; i++) {
					LOGGER.debug("md.getColumnName(i):"+md.getColumnName(i));
					LOGGER.debug("rs.getObject(i):"+rs.getObject(i));
					item.put(md.getColumnName(i), rs.getObject(i).toString());
				}
				list.add(item);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			LOGGER.error("执行异常: " + sql, e);
			return list;
		} catch (Exception e) {
			LOGGER.error("执行异常: " + sql, e);
			return list;
		}
		LOGGER.info("findByPage查询结果:\n" + list);
		return list;
	}

	public JSONArray findByRow(int startRow, int endRow, String sql) {
		LOGGER.info("执行findByPage\n" + startRow + "\t" + endRow + "\n" + sql);
		JSONArray list = new JSONArray();
		try {
			if (conn.isClosed()) {
				this.reConnection(DBConfig.RECONNECTION_COUNT,
						DBConfig.RECONNECTION_MILLIS);
			}
			sql = buildSqlByRow(startRow, endRow, sql);
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()) {
				JSONObject item = new JSONObject();
				for (int i = 1; i <= columnCount; i++) {
					item.put(md.getColumnName(i), rs.getObject(i));
				}
				list.add(item);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			LOGGER.error("执行异常: " + sql, e);
			return list;
		} catch (Exception e) {
			LOGGER.error("执行异常: " + sql, e);
			return list;
		}
		LOGGER.info("findByPage查询结果:\n" + list);
		return list;
	}

	/**
	 * 
	 * @param sql
	 * @return
	 */
	public long findTotalCount(String sql) {
		LOGGER.info("执行findTotalCount, sql:\n" + sql);
		long totalCount = 0;
		sql = buildTotalSql(sql);
		try {
			if (conn.isClosed()) {
				this.reConnection(DBConfig.RECONNECTION_COUNT,
						DBConfig.RECONNECTION_MILLIS);
			}
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				totalCount = rs.getLong(1);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			LOGGER.error("执行异常: " + sql, e);
			return totalCount;
		} catch (Exception e) {
			LOGGER.error("执行异常: " + sql, e);
			return totalCount;
		}
		LOGGER.info("totalCount查询结果 : " + totalCount);
		return totalCount;
	}

	/**
	 * 更新
	 * 
	 * @param sql
	 * @return influencedCount
	 */
	public int update(String sql) {
		LOGGER.info("执行update, sql:\n" + sql);
		int influencedCount = 0;
		try {
			ps = conn.prepareStatement(sql);
			influencedCount = ps.executeUpdate(sql);
			ps.close();
		} catch (SQLException e) {
			LOGGER.error(null, e);
		}
		LOGGER.info("update结果,influencedCount : " + influencedCount);
		return influencedCount;
	}

	private String buildPageableSql(int pageNum, int pageSize, String sql) {
		int startRow = (pageNum - 1) * pageSize;
		int endRow = pageNum * pageSize;
		String pageableSql = buildSqlByRow(startRow, endRow, sql);
		LOGGER.info("pageableSql : " + pageableSql);
		return pageableSql;
	}
	
	private String buildSqlByRow(int startRow, int endRow, String sql) {
		String pageableSql = "SELECT * FROM (SELECT ROWNUM AS rowno, search_table.* from ("
				+ sql
				+ ") search_table where ROWNUM<="
				+ endRow
				+ ") table_alias WHERE table_alias.rowno > " + startRow;
		LOGGER.info("pageableSql : " + pageableSql);
		return pageableSql;
	}

	private String buildTotalSql(String sql) {
		String countSql = "select count(*) from (" + sql + ")";
		LOGGER.info("countSql : " + countSql);
		return countSql;
	}

	/**
	 * 重新链接
	 * 
	 * @param reconnectionCount
	 *            重连次数
	 * @param millis
	 *            重连间隔时间,单位毫秒
	 */
	private synchronized void reConnection(int reconnectionCount, long millis) {
		int i = 1;
		while (true) {
			try {
				conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USER,
						DBConfig.PASSWORD);
				if (!conn.isClosed()) {
					break;
				}
			} catch (SQLException e) {
				LOGGER.error(null, e);
			}
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				LOGGER.error(null, e);
			}
			if (i == reconnectionCount) {
				break;
			}
			i++;
		}
	}

	/**
	 * 关闭数据库连接
	 */
	public synchronized void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				LOGGER.error(null, e);
			} finally {
				conn = null;
			}
		}
	}

	public static void main(String[] args) {
		DBUtil db = new DBUtil();
		String sql = "select t.* from OSP_USER t where  username='sjpc' and password='123456'";
		db.findByPage(1, 10, sql);
	}
}
