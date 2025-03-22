package com.cheolhyeon.free_commnunity_1.report.service;

import com.cheolhyeon.free_commnunity_1.comment.repository.CommentRepository;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.cheolhyeon.free_commnunity_1.post.repository.PostRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.report.controller.request.ReportRequest;
import com.cheolhyeon.free_commnunity_1.report.controller.response.ReportResponse;
import com.cheolhyeon.free_commnunity_1.report.repository.ReportRedisRepository;
import com.cheolhyeon.free_commnunity_1.report.repository.ReportRepository;
import com.cheolhyeon.free_commnunity_1.report.repository.entity.ReportEntity;
import com.cheolhyeon.free_commnunity_1.report.type.ReportReason;
import com.cheolhyeon.free_commnunity_1.report.type.ReportType;
import com.cheolhyeon.free_commnunity_1.user.repository.UserRepository;
import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import com.cheolhyeon.free_commnunity_1.user.service.UserBanService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReportRedisRepository reportRedisRepository;
    private final UserBanService userBanService;

    private static final Duration TTL = Duration.ofDays(1);

    public ReportResponse report(ReportRequest request) {
        ReportType type = getReportType(request);

        UserEntity userEntity = userRepository.findById(request.getWriterId())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다"));
        Long reportCount = reportRedisRepository.report(userEntity.getId(), TTL);
        boolean isBan = userBanService.ban(userEntity.getId(), reportCount);

        ReportReason reason = ReportReason.from(request.getReason());
        ReportEntity entity = reportRepository.save(ReportEntity.from(request, type, reason));
        return ReportResponse.from(entity, isBan);
    }

    private ReportType getReportType(ReportRequest request) {
        ReportType type = ReportType.from(request.getReportType());
        if (type == ReportType.POST) {
            postRepository.findById(request.getContentId())
                    .ifPresentOrElse(PostEntity::reported, () -> {
                        throw new EntityNotFoundException("해당 게시글을 찾을 수 없습니다");
                    });
        }
        if (type == ReportType.COMMENT) {
            commentRepository.findById(request.getContentId())
                    .ifPresentOrElse(CommentEntity::reported, () -> {
                        throw new EntityNotFoundException("해당 댓글을 찾을 수 없습니다");
                    });
        }
        return type;
    }
}
