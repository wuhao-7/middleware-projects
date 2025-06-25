package com.acme.distributed.transaction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 交易服务
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
@Service
public class TransactionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public Long addTransaction(Long sellerId,Long buyerId,Long amount){
        Long txId = System.currentTimeMillis()/ 1000;
        jdbcTemplate.execute("INSERT INTO transactions (xid,seller_id,buyer_id,amount)  values (?,?,?,?) ",
                (PreparedStatementCallback<Void>) ps -> {
                        ps.setLong(1,txId);
                        ps.setLong(2,sellerId);
                        ps.setLong(3,buyerId);
                        ps.setLong(4,amount);
                        ps.executeUpdate();
                        return null;
                });
        System.out.println(txId);
        return txId;
    }
}
