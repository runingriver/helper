package org.helper.common.zookeeper;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * zk使用
 */
@Component
public class ZookeeperHelper {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperHelper.class);
    private String connString;
    private final Map<String, ZKClient> clientMap = Maps.newConcurrentMap();

    @PostConstruct
    public void init() {
        connString = "10.88.65.156:8212,10.88.106.110:8212,10.88.141.161:8212";
        //connString = FileProperty.getPropertyValues("zookeeper.address");
    }

    @PreDestroy
    public void closeZkClient() {
        for (ZKClient zkClient : clientMap.values()) {
            try {
                zkClient.getZkCuratorClient().close();
            } catch (Exception e) {
                logger.error("close Curator client exception.", e);
            }
        }
        logger.info("zkClients closed.");
    }

    public ZKClient getDefaultZKClient() {
        if (clientMap.containsKey(connString)) {
            return clientMap.get(connString);
        }
        ZKClient zkClient = new ZKClient(connString);
        clientMap.put(connString, zkClient);
        return zkClient;
    }

    public ZKClient getZKClient(String address) {
        Preconditions.checkArgument(StringUtils.isNotBlank(address), "address is illegal.");

        if (clientMap.containsKey(address)) {
            return clientMap.get(address);
        }
        ZKClient zkClient = new ZKClient(address);
        clientMap.put(address, zkClient);
        return zkClient;
    }

    public static class ZKClient {
        private String connectString;
        private CuratorFramework client;

        public ZKClient(String address) {
            this.connectString = address;
            client = CuratorFrameworkFactory.newClient(address, new RetryNTimes(3, 5000));
            client.start();
        }

        public CuratorFramework getZkCuratorClient() {
            return client;
        }

        public String getConnectString() {
            return connectString;
        }

        public String create(String path, byte[] data) {
            try {
                String result = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, data);
                return result;
            } catch (Exception e) {
                logger.error("create node exception.", e);
            }
            return "";
        }

        public String getData(String path) {
            byte[] bytes = new byte[0];
            try {
                bytes = client.getData().forPath(path);
            } catch (Exception e) {
                logger.error("get zk node data exception.", e);
            }
            return new String(bytes);
        }

        public void setData(String path, byte[] data) {
            try {
                client.setData().forPath(path, data);
            } catch (Exception e) {
                logger.error("set zk node data exception.", e);
            }
        }

        public void setNodeWatcher(String path, CuratorWatcher curatorWatcher) {
            Preconditions.checkArgument(StringUtils.isNotBlank(path), "path is null");
            try {
                client.getData().usingWatcher(curatorWatcher).forPath(path);
            } catch (Exception e) {
                logger.error("set {} watcher exception.", path, e);
            }
        }

        public void setChildrenWatcher(String path, CuratorWatcher curatorWatcher) {
            try {
                client.getChildren().usingWatcher(curatorWatcher).forPath(path);
            } catch (Exception e) {
                logger.error("set {} watcher exception.", path, e);
            }
        }

        public void setConnWatcher(ConnectionStateListener connWatcher) {
            client.getConnectionStateListenable().addListener(connWatcher);
        }
    }
}
