package org.helper.common.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:application-context.xml")
public class ZookeeperHelperTest {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperHelperTest.class);
    public static final String CONN_STRING = "10.88.65.156:8212,10.88.106.110:8212,10.88.141.161:8212";
    private static CuratorFramework client;

    @Before
    public void setUp() throws Exception {
        client = CuratorFrameworkFactory.newClient(CONN_STRING, new RetryNTimes(3, 5000));
        client.start();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void createTest() {
//        String result = zkClient.create("zongzhe/hu", "hell world".getBytes());
//        logger.info("result:{}", result);
    }

    @Test
    public void getDataTest() {
//        String data = zkClient.getData("zongzhe/hu");
//        logger.info("data:{}", data);
    }

    @Test
    public void WatcherTest() {

    }

}