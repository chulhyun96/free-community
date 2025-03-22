package com.cheolhyeon.free_commnunity_1.report.controller.response;

import com.cheolhyeon.free_commnunity_1.report.repository.entity.ReportEntity;
import com.cheolhyeon.free_commnunity_1.report.type.ReportReason;
import com.cheolhyeon.free_commnunity_1.report.type.ReportType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportResponse {
    private final Long reporterId;
    private final Long writerId;
    private final ReportReason reason;
    private final ReportType reportType;
    private final boolean isBanned;

    @Builder
    private ReportResponse(Long reporterId, Long writerId, ReportReason reason, ReportType reportType, boolean isBanned) {
        this.reporterId = reporterId;
        this.writerId = writerId;
        this.reason = reason;
        this.reportType = reportType;
        this.isBanned = isBanned;
    }

    public static ReportResponse from(ReportEntity report, boolean isBanned) {
        return new ReportResponse(
                report.getReportId(),
                report.getWriterId(),
                report.getReason(),
                report.getReportType(),
                isBanned
        );
    }
}
