package com.acme.distributed.transaction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 交易服务
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public Boolean updateAmount(Long sellerId,Long buyerId,Long amount){
        jdbcTemplate.execute("UPDATE users  SET amt_sold = amt_sold + ? where id = ?",
                (PreparedStatementCallback<Void>) ps -> {
                    ps.setLong(1,amount);
                    ps.setLong(2,sellerId);
                    ps.executeUpdate();
                    return null;
            });

        jdbcTemplate.execute("UPDATE users  SET  amt_bought = amt_bought + ? where id = ?",
                (PreparedStatementCallback<Void>) ps -> {
                    ps.setLong(1,amount);
                    ps.setLong(2,buyerId);
                    ps.executeUpdate();
                    return null;
                });

        return true;
    }
}
