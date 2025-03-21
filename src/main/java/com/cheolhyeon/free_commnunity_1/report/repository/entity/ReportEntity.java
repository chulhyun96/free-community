package com.cheolhyeon.free_commnunity_1.report.repository.entity;

import com.cheolhyeon.free_commnunity_1.report.type.ReportType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportEntity {
    @Id
    private Long reportId;

    private String reporterName;
    private String reportedName;
    private String reason;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Builder
    private ReportEntity(Long reportId, String reporterName, String reportedName, String reason, ReportType reportType) {
        this.reportId = reportId;
        this.reporterName = reporterName;
        this.reportedName = reportedName;
        this.reason = reason;
        this.reportType = reportType;
    }
}
