package com.acme.distributed.transaction.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionMessageService transactionMessageService;
    @Transactional
    public Boolean updateAmount(Long txId, Long sellerId,Long buyerId,Long amount){

        if (transactionMessageService.hasProcessedTransaction(txId, sellerId, amount)) {
            logger.warn("The transaction[id :{}] for seller[id : {}] has been processed", txId, sellerId);
            return false;
        }
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

        transactionMessageService.addTransactionMessage(txId, sellerId, amount);
        transactionMessageService.addTransactionMessage(txId, buyerId, amount);

        return true;
    }
}
