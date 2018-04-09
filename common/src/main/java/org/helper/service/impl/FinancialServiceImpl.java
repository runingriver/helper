package org.helper.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.helper.dao.FinancialTestDao;
import org.helper.model.FinancialTest;
import org.helper.service.FinancialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinancialServiceImpl implements FinancialService {
    private static final Logger logger = LoggerFactory.getLogger(FinancialServiceImpl.class);

    @Resource
    FinancialTestDao financialTestDao;

    @Override
    public List<FinancialTest> getByPage(int start, int count) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
