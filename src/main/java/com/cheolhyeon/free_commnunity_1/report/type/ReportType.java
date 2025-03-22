package com.cheolhyeon.free_commnunity_1.report.type;

import lombok.Getter;

@Getter
public enum ReportType {
    POST,
    COMMENT;

    public static ReportType from(String reportType) {
        return switch (reportType.toLowerCase()) {
            case "post" -> ReportType.POST;
            case "comment" -> ReportType.COMMENT;
            default -> null;
        };
    }
}
