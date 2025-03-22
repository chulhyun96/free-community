package com.cheolhyeon.free_commnunity_1.report.repository.entity;

import com.cheolhyeon.free_commnunity_1.report.controller.request.ReportRequest;
import com.cheolhyeon.free_commnunity_1.report.type.ReportReason;
import com.cheolhyeon.free_commnunity_1.report.type.ReportType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private Long reporterId;
    private Long writerId;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Builder
    private ReportEntity(Long reportId, Long reporterId, Long writerId, ReportReason reason, ReportType reportType) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.writerId = writerId;
        this.reason = reason;
        this.reportType = reportType;
    }

    public static ReportEntity from(ReportRequest request, ReportType reportType, ReportReason reason) {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.reporterId = request.getReporterId();
        reportEntity.writerId = request.getWriterId();
        reportEntity.reason = reason;
        reportEntity.reportType = reportType;
        return reportEntity;
    }
}
