package com.acme.distributed.transaction.base;

import com.acme.distributed.transaction.acid.LocalTransactionSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Queued Transaction Sample
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
@SpringBootApplication
@RestController
public class QueuedTransactionSample {

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @GetMapping("/send/message/{message}")
    public Boolean send(@PathVariable String message){
        kafkaTemplate.send("transaction",message);
        return true;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(LocalTransactionSample.class)
                .profiles("tx")
                .run(args);
    }
}
