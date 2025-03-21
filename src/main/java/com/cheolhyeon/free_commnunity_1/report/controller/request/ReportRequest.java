package com.cheolhyeon.free_commnunity_1.report.controller.request;

import com.cheolhyeon.free_commnunity_1.report.type.ReportReason;
import com.cheolhyeon.free_commnunity_1.report.type.ReportType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {
    private Long reportId;
    private Long targetId;
    private ReportReason reason;
    private ReportType reportType;

    @Builder
    private ReportRequest(Long reportId, Long targetId, ReportReason reason, ReportType reportType) {
        this.reportId = reportId;
        this.targetId = targetId;
        this.reason = reason;
        this.reportType = reportType;
    }
    public static ReportRequest of(Long reportId, Long targetId, ReportReason reason, ReportType reportType) {
        return new ReportRequest(reportId, targetId, reason, reportType);
    }
}
