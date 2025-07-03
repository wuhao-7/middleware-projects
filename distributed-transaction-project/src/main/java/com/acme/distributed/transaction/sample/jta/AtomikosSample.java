package com.acme.distributed.transaction.sample.jta;
//
//import com.atomikos.icatch.jta.UserTransactionImp;
//import com.atomikos.jdbc.AtomikosDataSourceBean;

import javax.transaction.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JTA开源框架 - Atomikos
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class AtomikosSample {
//    public static void main(String[] args) throws SystemException, NotSupportedException, SQLException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
//        // MySQL 本地数据库
//        String jdbcURL1 = "jdbc:mysql://127.0.0.1:3306/test";
//        AtomikosDataSourceBean atomikosDataSourceBean1 = getAtomikosDataSourceBean(jdbcURL1);
//
//        // MySQL 在 Docker 容器
//        String jdbcURL2 = "jdbc:mysql://127.0.0.1:13306/test";
//        AtomikosDataSourceBean atomikosDataSourceBean2 = getAtomikosDataSourceBean(jdbcURL2);
//
//        UserTransaction userTransaction = new UserTransactionImp();
//        // user 方法未关联 start
//        userTransaction.begin();
//        // Atomikos 在 JDBC Connection 接口上实现动态代理，拦截 enlist 方法，包括:
//        // createStatement , prepareStatement 以及 prepareCall 方法
//        insertUser(atomikosDataSourceBean1.getConnection());
//        insertUser(atomikosDataSourceBean2.getConnection());
//        // commit 分别执行 XAResource end, prepare 以及commit操作
//        userTransaction.commit();
//
//        atomikosDataSourceBean1.close();
//        atomikosDataSourceBean2.close();
//
//    }
//
//    private static void insertUser(Connection connection) throws SQLException {
//        String sql = "INSERT INTO user(name) VALUE (?);";
//        String userName = "admin";
//        // 创建 PreparedStatement
//        PreparedStatement preparedStatement1 = connection.prepareStatement(sql);
//        preparedStatement1.setString(1, userName);
//        preparedStatement1.executeUpdate();
//    }
//
//    private static AtomikosDataSourceBean getAtomikosDataSourceBean(String jdbcURL) {
//        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
//        ds.setUniqueResourceName(jdbcURL);
//        ds.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
//        Properties p = new Properties();
//        p.setProperty("user", "root");
//        p.setProperty("password", "12233");
//        p.setProperty("URL", jdbcURL);
//        ds.setXaProperties(p);
//        ds.setPoolSize(5);
//        return ds;
//    }
}
