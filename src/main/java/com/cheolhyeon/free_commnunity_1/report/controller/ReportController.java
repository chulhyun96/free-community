package com.cheolhyeon.free_commnunity_1.report.controller;

import com.cheolhyeon.free_commnunity_1.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
}
