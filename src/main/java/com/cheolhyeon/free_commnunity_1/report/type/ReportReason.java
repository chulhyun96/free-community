package com.cheolhyeon.free_commnunity_1.report.type;

import lombok.Getter;

@Getter
public enum ReportReason {
    SWEAR("욕설"),
    INAPPROPRIATE_AD("인격 모독"),
    MACRO("매크로"),
    PERSONAL_INSULT("타인 비하");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }

    public static ReportReason from(String description) {
        return switch (description) {
            case "욕설" -> SWEAR;
            case "인격 모독" -> INAPPROPRIATE_AD;
            case "매크로" -> MACRO;
            case "타인 비하" -> PERSONAL_INSULT;
            default -> null;
        };
    }
}
