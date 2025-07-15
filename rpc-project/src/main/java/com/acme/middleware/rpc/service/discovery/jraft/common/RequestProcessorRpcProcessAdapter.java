package com.acme.middleware.rpc.service.discovery.jraft.common;

import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.entity.Task;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Internal Adapter from {@link RequestProcessor} to {@link RpcProcessor}
 *
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class RequestProcessorRpcProcessAdapter<T extends Serializable, R extends Serializable> implements RpcProcessor<T>{
    private final static Logger logger = LoggerFactory.getLogger(RequestProcessorRpcProcessAdapter.class);

    private final JRaftServer server;

    private final RequestProcessor<T,R> requestProcessor;

    public RequestProcessorRpcProcessAdapter(JRaftServer server, RequestProcessor<T, R> requestProcessor) {
        this.server = server;
        this.requestProcessor = requestProcessor;
    }

    @Override
    public void handleRequest(RpcContext rpcCtx, T data) {
        RequestContext requestContext = new RequestContext(data, getNode(), getFsm());

        RequestContextClosure closure = new RequestContextClosure(requestContext, (status) -> {
            if (!status.isOk()) {
                logger.warn("Closure status is : {}", status);
                return;
            }
            rpcCtx.sendResponse(requestProcessor.process(requestContext, status));
            logger.info("Registration request has been handled , status : {}", status);
        });

        if (!isLeader()) {
            handlerNotLeaderError(closure);
            return;
        }

        Task task = new Task();
        task.setData(requestContext.serialize());
        task.setDone(closure);
        getNode().apply(task);
        logger.info("The task of '{}' has been applied , data : {}", requestContext.getDataType(), requestContext.getData());

    }

    private boolean isLeader() {
        return getFsm().isLeader();
    }


    private void handlerNotLeaderError(final Closure closure) {
        logger.error("No Leader node : {}", getNode().getNodeId());
        closure.run(new Status(RaftError.EPERM, "Not leader"));
    }

    @Override
    public String interest() {
        return requestProcessor.getRequestType();
    }

    private Node getNode(){
        return this.server.getNode();
    }

    private JRaftStateMachine getFsm(){
        return this.server.getFsm();
    }
}
