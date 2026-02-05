package com.library.bookarte.borrow.repository;

import com.library.bookarte.borrow.dto.BorrowSearchFilterDto;
import com.library.bookarte.borrow.dto.response.MonthlyData;
import com.library.bookarte.borrow.entity.Borrow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BorrowRepositoryCustom {
    Page<Borrow> findAllBorrowByBorrowSearchFilter(BorrowSearchFilterDto borrowSearchFilterDto, Pageable pageable);
    List<MonthlyData> getRollingYearlyStatistics(Long bookId);
}
