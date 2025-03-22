package com.cheolhyeon.free_commnunity_1.report.service;

import com.cheolhyeon.free_commnunity_1.comment.repository.CommentRepository;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.cheolhyeon.free_commnunity_1.post.repository.PostRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.report.controller.request.ReportRequest;
import com.cheolhyeon.free_commnunity_1.report.repository.ReportRepository;
import com.cheolhyeon.free_commnunity_1.report.repository.entity.ReportEntity;
import com.cheolhyeon.free_commnunity_1.report.type.ReportReason;
import com.cheolhyeon.free_commnunity_1.report.type.ReportType;
import com.cheolhyeon.free_commnunity_1.user.repository.UserRepository;
import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    ReportRepository reportRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ReportRedisRepository reportRedisRepository;

    @InjectMocks
    ReportService reportService;

    @Test
    @DisplayName("Report가 호출 될 경우 ReportEntity가 save 된다.")
    void saveReport() {
        //given
        ReportType type = ReportType.from("post");
        ReportReason reason = ReportReason.from("욕설");
        ReportEntity entity = ReportEntity.builder()
                .reportId(1L)
                .writerId(2L)
                .reportType(type)
                .reason(reason)
                .build();
        given(reportRepository.save(entity)).willReturn(entity);
        //when
        ReportEntity result = reportRepository.save(entity);

        //then
        assertThat(entity).extracting(
                ReportEntity::getReportId,
                ReportEntity::getReporterId,
                ReportEntity::getWriterId,
                ReportEntity::getReportType,
                ReportEntity::getReason
        ).containsExactly(
                result.getReportId(),
                result.getReporterId(),
                result.getWriterId(),
                result.getReportType(),
                result.getReason());
    }

    @Test
    @DisplayName("ReportType이 POST일 경우 PostEntity의 게시글이 변경된다.")
    void reportWhenReportTypeIsPost() {
        //given
        UserEntity mockUserEntity = mock(UserEntity.class);

        PostEntity post = mock(PostEntity.class);

        ReportRequest request = new ReportRequest(1L, 2L, 1L, "욕설", "post");
        ReportType type = ReportType.from("post");
        ReportReason reason = ReportReason.from("욕설");
        ReportEntity entity = ReportEntity.from(request, type, reason);

        given(reportRepository.save(any(ReportEntity.class)))
                .willReturn(entity);
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(mockUserEntity));


        //when
        reportService.report(request);

        //then
        then(post).should(times(1))
                .reported();
        then(reportRedisRepository).should(times(1))
                .report(anyLong(), any(Duration.class));
        then(commentRepository).should(never())
                .findById(anyLong());
        then(commentRepository).shouldHaveNoMoreInteractions();
        then(userRepository).shouldHaveNoMoreInteractions();
        then(reportRedisRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("ReportType이 COMMENT일 경우 CommentEntity의 게시글이 변경된다.")
    void reportWhenReportTypeIsComment() {
        //given
        UserEntity mockUserEntity = mock(UserEntity.class);
        CommentEntity mockCommentEntity = mock(CommentEntity.class);

        ReportRequest request = new ReportRequest(1L, 2L, 1L, "욕설", "comment");
        ReportType type = ReportType.from("comment");
        ReportReason reason = ReportReason.from("욕설");
        ReportEntity entity = ReportEntity.from(request, type, reason);

        given(reportRepository.save(any(ReportEntity.class)))
                .willReturn(entity);
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(mockCommentEntity));
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(mockUserEntity));


        //when
        reportService.report(request);

        //then
        then(mockCommentEntity).should(times(1))
                .reported();
        then(reportRedisRepository).should(times(1))
                .report(anyLong(), any(Duration.class));
        then(postRepository).should(never())
                .findById(anyLong());
        then(userRepository).should(times(1))
                .findById(anyLong());

        then(postRepository).shouldHaveNoMoreInteractions();
        then(userRepository).shouldHaveNoMoreInteractions();
        then(reportRedisRepository).shouldHaveNoMoreInteractions();
    }
}