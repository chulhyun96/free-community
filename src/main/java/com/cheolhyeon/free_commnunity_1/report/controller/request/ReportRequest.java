package com.cheolhyeon.free_commnunity_1.report.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {
    private Long reporterId;
    private Long writerId;
    private Long contentId;
    private String reason;
    private String reportType;

    @Builder
    public ReportRequest(Long reporterId, Long writerId, Long contentId, String reason, String reportType) {
        this.reporterId = reporterId;
        this.writerId = writerId;
        this.contentId = contentId;
        this.reason = reason;
        this.reportType = reportType;
    }
}
