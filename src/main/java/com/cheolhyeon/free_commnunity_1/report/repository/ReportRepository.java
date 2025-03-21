package com.cheolhyeon.free_commnunity_1.report.repository;

import com.cheolhyeon.free_commnunity_1.report.repository.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
}
