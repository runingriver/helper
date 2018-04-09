package org.helper.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.helper.model.FinancialTest;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialTestDao {

    Integer selectCount();

    List<FinancialTest> selectByPage(@Param("start") int start, @Param("end") int end);

}
