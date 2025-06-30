package com.acme.middleware.rpc.service.discovery.jraft.common;

import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JRaft 状态机
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class JRaftStateMachine extends StateMachineAdapter {

    private static final Logger logger = LoggerFactory.getLogger(JRaftStateMachine.class);

    private final AtomicLong leaderTerm = new AtomicLong(-1);

    private final Map<String, RequestProcessor> requestProcessorRepository = new HashMap<>();

    private Node node;
    public boolean isLeader(){
        return this.leaderTerm.get() > 0;
    }
    @Override
    public void onApply(Iterator iterator) {
        while(iterator.hasNext()){
            RequestContext requestContext = null;
            RequestContextClosure closure = null;
            if(iterator.done() != null){
                closure = (RequestContextClosure) iterator.done();
                requestContext = closure.getRequestContext();
                logger.info("The closure with operation[{}] at the Leader node [{}]", requestContext, node);
            }else{
                final ByteBuffer data = iterator.getData();
                requestContext = RequestContext.deserialize(data,node,this);
                logger.info("This closure with operation [{}] at the Follower node [{}]",requestContext,node);
            }

            if(requestContext != null){
                if(closure != null){
                    closure.run(Status.OK());
                }else{// Follower
                    String requestType = requestContext.getDataType();
                    // locate RequestProcessor
                    RequestProcessor requestProcessor = requestProcessorRepository.get(requestType);
                    requestProcessor.process(requestContext, Status.OK());
                    logger.info("Locate the RequestProcessor[class: '{}'] by the request type: '{}'",
                            requestProcessor.getClass().getName(), requestType);
                    // TODO skip the read operation in the follower node
                }
            }
            iterator.next();
        }
    }
    @Override
    public void onLeaderStart(long term) {
        this.leaderTerm.set(term);
        super.onLeaderStart(term);
    }

    @Override
    public void onLeaderStop(Status status) {
        this.leaderTerm.set(-1);
        super.onLeaderStop(status);
    }

    public void setNode(Node node){
        this.node = node;
    }

    public Node getNode(){
        return node;
    }

    public void registerRequestProcessor(RequestProcessor requestProcessor){
        requestProcessorRepository.put(requestProcessor.getRequestType(), requestProcessor);
    }
}
