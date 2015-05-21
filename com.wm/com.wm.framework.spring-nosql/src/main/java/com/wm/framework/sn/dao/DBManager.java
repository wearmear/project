package com.wm.framework.sn.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sequoiadb.base.Sequoiadb;
import com.sequoiadb.base.SequoiadbDatasource;
import com.sequoiadb.base.SequoiadbOption;
import com.sequoiadb.exception.BaseException;
import com.sequoiadb.net.ConfigOptions;
import com.wm.common.exception.DBException;
import com.wm.common.util.PropertiesExpand;

public class DBManager {

	private static final Logger log = LogManager.getLogger(DBManager.class);

	private final static PropertiesExpand pe = new PropertiesExpand();
	static {
		String relativePath = "nosql.properties";
		try {
			pe.loadByRelativePath(relativePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 连接ip和端口
	 */
	private static String connString = pe.getProperty("connString");
	/**
	 * 账号
	 */
	private static String username = pe.getProperty("username");
	/**
	 * 密码
	 */
	private static String password = pe.getProperty("password");

	private static SequoiadbDatasource datasource;

	private static Semaphore semaphore = new Semaphore(1);

	private static void initDatasource() {
		ArrayList<String> urls = new ArrayList<String>();
		urls.add(connString);

		ConfigOptions configOptions = new ConfigOptions(); // 定义连接选项
		configOptions.setConnectTimeout(500); // 设置若连接失败，超时时间（ms）
		configOptions.setMaxAutoConnectRetryTime(0); // 设置若连接失败，重试次数

		// 以下设置的都是 SequoiadbOption 的默认值
		SequoiadbOption datasourceOption = new SequoiadbOption(); // 定义连接池选项
		datasourceOption.setMaxConnectionNum(500); // 设置连接池最大连接数
		datasourceOption.setInitConnectionNum(2); // 初始化连接池时，创建连接的数量
		datasourceOption.setDeltaIncCount(2); // 当池中没有可用连接时，增加连接的数量
		datasourceOption.setMaxIdeNum(2); // 周期清理多余的空闲连接时，应保留连接的数量
		datasourceOption.setTimeout(5 * 1000); // 当已使用的连接数到达设置的最大连接数时（500），请求连接的等待时间。
		datasourceOption.setAbandonTime(10 * 60 * 1000); // 连接存活时间，当连接空闲时间超过连接存活时间，将被连接池丢弃
		datasourceOption.setRecheckCyclePeriod(1 * 60 * 1000); // 清除多余空闲连接的周期
		datasourceOption.setRecaptureConnPeriod(10 * 60 * 1000); // 检测并取回异常地址的周期

		datasource = new SequoiadbDatasource(urls, username, password, configOptions, datasourceOption); // 创建连接池
	}

	public static synchronized Sequoiadb getConnection() {
		Sequoiadb sdb = null;
		try {
			if (null == datasource) {
				initDatasource();
			}
			sdb = datasource.getConnection();
		} catch (BaseException ex) {
			log.error("", new DBException(ex.getMessage()));
			initDatasource();
		} catch (InterruptedException ex) {
			log.error("", new DBException(ex.getMessage()));
		}

		if (sdb == null) {
			while (sdb == null) {
				try {
					semaphore.tryAcquire(1, 2, TimeUnit.SECONDS);
				} catch (InterruptedException ex) {
					log.error("", new DBException(ex.getMessage()));
				}
				try {
					sdb = datasource.getConnection();
				} catch (BaseException | InterruptedException ex) {
					log.error("", new DBException(ex.getMessage()));
					initDatasource();
				}
			}
			semaphore.release();
			return sdb;
		}
		return sdb;
	}

	// public static Sequoiadb getConnection() throws DBException {
	// Sequoiadb connection = null;
	// if (null == datasource) {
	// initDatasource();
	// }
	// try {
	// connection = datasource.getConnection();
	// } catch (InterruptedException e) {
	// DBException dbe = new DBException(e.getMessage());
	// log.error("", dbe);
	// throw dbe;
	// }
	//
	// return connection;
	// }

	public static synchronized Sequoiadb getTrancationConnection() throws DBException {
		Sequoiadb conn = getConnection();
		conn.beginTransaction();
		return conn;
	}

	public static synchronized void closeConnection(Sequoiadb connection) {
		if (null != datasource) {
			if (null != connection) {
				datasource.close(connection);
			}
		}
	}

	public static void main(String[] args) throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		System.out.println(conn);
	}
}
