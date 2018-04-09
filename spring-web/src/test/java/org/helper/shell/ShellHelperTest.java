package org.helper.shell;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zongzhehu on 17-2-6.
 */
public class ShellHelperTest {
    private static final Logger logger = LoggerFactory.getLogger(ShellHelperTest.class);

    @Test
    public void testRunLinuxShell() throws Exception {
        String path = "/home/zongzhehu/github/helper/target/classes/file/123.sh";
        String rst = ShellHelper.runLinuxShell(path);
        logger.info("{}", rst);
    }

    /**
     * 路径测试
     */
    @Test
    public void testPathTest() {
        String p1 = Thread.currentThread().getContextClassLoader().getResource("file/123.sh").toString();
        String p2 = System.getProperty("user.dir");
        // String p3 = ServletContext.getRealPath("/");
        logger.info("{}", p1);
        logger.info("{}", p2);
    }

    @Test
    public void execShellCommand() throws Exception {
        //String ls = ShellHelper.execShellCommand("chmod 755 /home/hzz/github/run.sh");
        String result = ShellHelper.execShellCommand("/home/hzz/github/run.sh 111", true);
        logger.info("result:{}", result);
    }

}