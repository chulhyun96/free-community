package com.cheolhyeon.free_commnunity_1.report.controller;

import com.cheolhyeon.free_commnunity_1.report.controller.request.ReportRequest;
import com.cheolhyeon.free_commnunity_1.report.controller.response.ReportResponse;
import com.cheolhyeon.free_commnunity_1.report.service.ReportService;
import com.cheolhyeon.free_commnunity_1.report.type.ReportReason;
import com.cheolhyeon.free_commnunity_1.report.type.ReportType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    ReportService reportService;


    @Test
    @DisplayName("게시글을 신고할 경우 ReportResponse가 반환된다.")
    void report() throws Exception {
        //given
        ReportRequest request = ReportRequest.builder()
                .reporterId(1L)
                .writerId(2L)
                .contentId(3L)
                .reason("욕설")
                .reportType("POST")
                .build();

        ReportResponse response = ReportResponse.builder()
                .reporterId(request.getReporterId())
                .writerId(request.getWriterId())
                .reason(ReportReason.SWEAR)
                .reportType(ReportType.POST)
                .build();

        given(reportService.report(any(ReportRequest.class)))
                .willReturn(response);

        //when
        mockMvc.perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reporterId").value(response.getReporterId()))
                .andExpect(jsonPath("$.writerId").value(response.getWriterId()))
                .andExpect(jsonPath("$.reason").value(response.getReason().toString()))
                .andExpect(jsonPath("$.reportType").value(response.getReportType().toString()))
                .andDo(print());
        //then
    }

}