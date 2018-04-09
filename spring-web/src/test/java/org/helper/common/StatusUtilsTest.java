package org.helper.common;

import org.helper.entity.ClientStatus;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;


public class StatusUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(StatusUtilsTest.class);
    @Test
    public void getClientStatus() throws Exception {
        ClientStatus helper = StatusUtils.getClientStatus("helper");
        logger.info(helper.toString());
    }

}