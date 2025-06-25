package com.acme.distributed.transaction;

import com.mysql.cj.jdbc.MysqlXADataSource;
import com.mysql.cj.jdbc.MysqlXid;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Mysql XA resource 样例
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class MySqlXaResourceSample {
    public static void main(String[] args) throws SQLException, XAException {
        String sql  = "INSERT INTO user(id,name) VALUE (?,?);";
        int userId = 1;
        String userName  = "root";

        //mysql 本地
        String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/test";
        //获取本地mysql connection
        XAConnection xaConnection1 = getXaConnection(jdbcUrl);
        XAResource xaResource = xaConnection1.getXAResource();
        Xid xid1 = new MysqlXid(new byte[]{1}, new byte[]{2}, 1);
        //事务资源管理器关联资源管理器
        xaResource.start(xid1,XAResource.TMNOFLAGS);

        Connection connection = xaConnection1.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,userId);
        preparedStatement.setString(2,userName);
        preparedStatement.executeUpdate();

        xaResource.end(xid1,XAResource.TMSUCCESS);



        //mysql docker 容器
        String jdbcUrl2 = "jdbc:mysql://127.0.0.1:13306/test";
        //获取本地mysql connection
        XAConnection xaConnection2 = getXaConnection(jdbcUrl2);
        XAResource xaResource2 = xaConnection2.getXAResource();
        Xid xid2 = new MysqlXid(new byte[]{11}, new byte[]{22}, 2);
        //事务资源管理器关联资源管理器
        xaResource2.start(xid2,XAResource.TMNOFLAGS);
        Connection connection2 = xaConnection2.getConnection();
        PreparedStatement preparedStatement2 = connection2.prepareStatement(sql);
        preparedStatement2.setInt(1,userId);
        preparedStatement2.setString(2,userName);
        preparedStatement2.executeUpdate();
        xaResource2.end(xid2,XAResource.TMSUCCESS);


        //两阶段提交
        // 第一阶段
        int result = xaResource.prepare(xid1);
        int result2 = xaResource2.prepare(xid2);

        // 第二阶段提交
        if(XAResource.XA_OK == result && XAResource.XA_OK == result2){
            xaResource.commit(xid1,false);
            xaResource2.commit(xid2,false);
        }else {
            xaResource.rollback(xid1);
            xaResource2.rollback(xid2);
        }



        xaConnection1.close();
        xaConnection2.close();

    }

    private static XAConnection getXaConnection(String jdbcUrl) throws SQLException {
        String userName = "root";
        String password = "12233";
        MysqlXADataSource xaDataSource = new MysqlXADataSource();
        xaDataSource.setURL(jdbcUrl);
        return xaDataSource.getXAConnection(userName,password);
    }
}

