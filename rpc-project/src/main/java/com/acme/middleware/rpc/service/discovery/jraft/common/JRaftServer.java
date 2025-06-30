package com.acme.middleware.rpc.service.discovery.jraft.common;

import com.acme.middleware.rpc.service.discovery.InMemoryJRaftServiceFactory;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.RaftGroupService;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.RaftRpcServerFactory;
import com.alipay.sofa.jraft.rpc.RpcServer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ServiceLoader;

/**
 *
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class JRaftServer {

    private RaftGroupService raftGroupService;

    private Node node;

    private JRaftStateMachine fsm;

    public JRaftServer(final String dataPath, final String groupId, final PeerId serverId,
                       final NodeOptions nodeOptions) throws IOException {
       //init raft data path,it contains log,meta,snapshot
        FileUtils.forceMkdir(new File(dataPath));

        // here use same Rpc server for raft and business, It also can be seperated generally
        final RpcServer rpcServer  = RaftRpcServerFactory.createRaftRpcServer(serverId.getEndpoint());

        //init state machine
        this.fsm = new JRaftStateMachine();

        ServiceLoader<RequestProcessor> requestProcessors = ServiceLoader.load(RequestProcessor.class,getClass().getClassLoader());
        for (RequestProcessor requestProcessor: requestProcessors){
            // Register RpcProcess to RpcServer
            rpcServer.registerProcessor(requestProcessor.adapt(this));
            // Register RequestProcessor to FSM
            this.fsm.registerRequestProcessor(requestProcessor);
        }

        // set fsm to nodeOptions
        nodeOptions.setFsm(this.fsm);
        // set the InMemoryJRaftServiceFactory
        nodeOptions.setServiceFactory(new InMemoryJRaftServiceFactory());

        // set storage path (log,meta,snapshot)
        // log must
        nodeOptions.setLogUri(dataPath + File.separator + "log");
        //meta must
        nodeOptions.setRaftMetaUri(dataPath + File.separator + "raft_meta");
        // init raft group service framework
        this.raftGroupService = new RaftGroupService(groupId,serverId,nodeOptions,rpcServer);
        // start raft node
        this.node = this.raftGroupService.start();
        this.fsm.setNode(this.node);
    }

    public JRaftStateMachine getFsm(){
        return this.fsm;
    }
    public Node getNode(){
        return this.node;
    }

}
