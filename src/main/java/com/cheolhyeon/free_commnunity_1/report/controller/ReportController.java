package com.cheolhyeon.free_commnunity_1.report.controller;

import com.cheolhyeon.free_commnunity_1.report.controller.request.ReportRequest;
import com.cheolhyeon.free_commnunity_1.report.controller.response.ReportResponse;
import com.cheolhyeon.free_commnunity_1.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/report")
    public ResponseEntity<ReportResponse> report(
            @RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.report(request));
    }
}
