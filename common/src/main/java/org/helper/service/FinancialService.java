package org.helper.service;

import org.helper.model.FinancialTest;

import java.util.List;

public interface FinancialService {

    int getCount();

    List<FinancialTest> getByPage(int start, int count);


}
