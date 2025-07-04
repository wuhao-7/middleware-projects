package com.acme.middleware.rpc.service.discovery;

import com.acme.middleware.rpc.serializer.Serializer;
import com.acme.middleware.rpc.service.ServiceInstance;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认实现
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.o
 */
public class FileSystemServiceDiscovery implements ServiceDiscovery{

    private final Serializer serializer = Serializer.Default;

    private File rootDirectory;

    @Override
    public void initialize(Map<String, Object> config) {
        rootDirectory = new File(System.getProperty("java.io.tmpdir"));
    }

    @Override
    public void register(ServiceInstance serviceInstance) {
        String serviceName = serviceInstance.getServiceName();
        File serviceDirectory = new File(rootDirectory,serviceName);
        File serviceInstanceFile = new File(serviceDirectory,serviceInstance.getId());
        try {
            byte[] bytes = serializer.serialize(serviceInstance);
            FileUtils.writeByteArrayToFile(serviceInstanceFile,bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deregister(ServiceInstance serviceInstance) {
        String serviceName = serviceInstance.getServiceName();
        File serviceDirectory = new File(rootDirectory, serviceName);
        File serviceInstanceFile = new File(serviceDirectory,serviceInstance.getId());
        FileUtils.deleteQuietly(serviceInstanceFile);
    }

    @Override
    public List<ServiceInstance> getServiceInstances(String serviceName) {
        File serviceDirectory = new File(rootDirectory, serviceName);
        Collection<File> files = FileUtils.listFiles(serviceDirectory,null,false);

        return (List)files.stream().map(file->{
            try {
                byte[] bytes = FileUtils.readFileToByteArray(file);
                return serializer.deserialize(bytes,ServiceInstance.class);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }).collect(Collectors.toList());
    }

    @Override
    public void close() {
        FileUtils.deleteQuietly(rootDirectory);
    }
}
