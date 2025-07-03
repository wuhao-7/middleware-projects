package com.acme.distributed.transaction.sample.acid;

import com.acme.distributed.transaction.service.TransactionService;
import com.acme.distributed.transaction.service.UserService;
import com.acme.distributed.transaction.web.WebMvcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Local Transactional Sample
 *
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
@RestController
@EnableAutoConfiguration
@EnableTransactionManagement(proxyTargetClass = true)
@Import({TransactionService.class, UserService.class, WebMvcConfiguration.class})
public class LocalTransactionSample {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @GetMapping("/transactions/{sellerId}/{buyerId}/{amount}")
    @Transactional
    public Boolean tx(@PathVariable Long sellerId, @PathVariable Long buyerId, @PathVariable Long amount) {
        Long txId = transactionService.addTransaction(sellerId, buyerId, amount);
        transactionService.addTransaction(sellerId, buyerId, amount);
        userService.updateAmount(txId,sellerId, buyerId, amount);
        applicationEventPublisher.publishEvent("tx-object");
        return true;
    }


    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(Object obj) {
        System.out.println(obj);
    }


    public static void main(String[] args) {
        new SpringApplicationBuilder(LocalTransactionSample.class)
                .profiles("test")
                .run(args);
    }
}
