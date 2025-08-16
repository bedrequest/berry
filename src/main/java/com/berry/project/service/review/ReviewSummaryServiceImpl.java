package com.berry.project.service.review;

import com.berry.project.dto.review.ReviewChatMessageDTO;
import com.berry.project.dto.review.ReviewChatRequestDTO;
import com.berry.project.dto.review.ReviewChatResponseDTO;
import com.berry.project.dto.review.ReviewSummaryDTO;
import com.berry.project.entity.review.Review;
import com.berry.project.entity.review.ReviewSummary;
import com.berry.project.repository.review.ReviewRepository;
import com.berry.project.repository.review.ReviewSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewSummaryServiceImpl implements ReviewSummaryService {
    private final ReviewRepository reviewRepository;
    private final ReviewSummaryRepository reviewSummaryRepository;
    private final WebClient webClient;

    private static final int MIN_LEN     = 50;
    private static final int MAX_LEN     = 1000;
    private static final int SAMPLE_SIZE = 50;

    @Override
    @Transactional
    public ReviewSummaryDTO findByLodgeIdOrGenerate(Long lodgeId) {
        return reviewSummaryRepository.findByLodgeId(lodgeId)
                .map(e -> ReviewSummaryDTO.builder()
                        .lodgeId(e.getLodgeId())
                        .summaryText(e.getSummaryText())
                        .summaryUpdate(e.getSummaryUpdate())
                        .build()
                )
                .orElse(null);
    }

    @Override
    @Transactional
    public ReviewSummaryDTO generateSummary(Long lodgeId) {
        List<Review> all = reviewRepository.findByLodgeId(lodgeId);
        if (all.isEmpty()) {
            throw new IllegalArgumentException("리뷰가 없습니다: " + lodgeId);
        }

        // 1) 필터링 & 샘플링
        List<Review> filtered = all.stream()
                .filter(r -> {
                    int len = Optional.ofNullable(r.getContent()).orElse("").length();
                    return len >= MIN_LEN && len <= MAX_LEN;
                })
                .collect(Collectors.toList());
        List<Review> sample = filtered.isEmpty() ? all : filtered;
        Collections.shuffle(sample);
        sample = sample.subList(0, Math.min(sample.size(), SAMPLE_SIZE));

        // 2) 리뷰 텍스트 합치기
        String combined = sample.stream()
                .map(Review::getContent)
                .collect(Collectors.joining("\n\n"));

        // 3) 프롬프트 메시지 구성
        List<ReviewChatMessageDTO> messages = List.of(
                new ReviewChatMessageDTO(
                        "system",
                        "당신은 숙소 리뷰 전문가입니다. 이 숙소의 가장 매력적인 3가지 장점과 추천포인트를 뽑아, " +
                                "고객이 마치 직접 경험하는 듯한 생생한 어조로 뽑아 소개해 주세요. 절대 번호 매기기 넘버링(1, 2, 3)을 사용하지 말고, " +
                                "각 문장 끝에 `<br/>` 태그를 붙여 주세요. **3~4문장**, **약 100~140단어** 분량의 한 단락으로 절대 초과하지 말고, 꼭 자연스럽게 작성해 문장을 마무리해주세요."
                ),
                new ReviewChatMessageDTO(
                        "user",
                        "아래 리뷰들을 참고해, 이 숙소의 핵심 장점과 추천 포인트를 요약해 주세요.\n\"\"\"\n" +
                                combined +
                                "\n\"\"\"\n" +
                                "각 문장 끝에 `<br/>` 태그를 넣고, 3~4문장, 약 100~140단어 분량의 한 단락으로 절대 초과하지 말고 꼭 자연스럽게 작성해 문장을 마무리해주세요."
                )

        );

        // 4) 요청 DTO 생성
        ReviewChatRequestDTO req = new ReviewChatRequestDTO(
                "gpt-3.5-turbo-16k",
                messages,
                0.7
        );

        String summary;
        try {
            // 5) ChatGPT API 호출
            ReviewChatResponseDTO res = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(ReviewChatResponseDTO.class)
                    .block();
            summary = res.getChoices().get(0).getMessage().getContent().trim();

            summary = summary.replaceAll("\\.\\s+", ".<br/>");
            if (summary.endsWith("<br/>")) {
                summary = summary.substring(0, summary.length() - 5);
            }

        } catch (WebClientResponseException.TooManyRequests ex) {
            // 6) 429 처리: 기존 요약 있으면 반환, 없으면 안내문구 반환
            Optional<ReviewSummary> existing = reviewSummaryRepository.findByLodgeId(lodgeId);
            if (existing.isPresent()) {
                ReviewSummary e = existing.get();
                return ReviewSummaryDTO.builder()
                        .lodgeId(e.getLodgeId())
                        .summaryText(e.getSummaryText())
                        .summaryUpdate(e.getSummaryUpdate())
                        .build();
            } else {
                String fallback = "AI 요약이 일시적으로 제한되었습니다. 잠시 후 다시 시도해주세요.";
                return ReviewSummaryDTO.builder()
                        .lodgeId(lodgeId)
                        .summaryText(fallback)
                        .summaryUpdate(LocalDateTime.now())
                        .build();
            }
        }

        // 7) 요약 성공 시에만 DB에 저장(upsert)
        reviewSummaryRepository.findByLodgeId(lodgeId)
                .ifPresent(x -> reviewSummaryRepository.deleteByLodgeId(lodgeId));
        ReviewSummary entity = ReviewSummary.builder()
                .lodgeId(lodgeId)
                .summaryText(summary)
                .summaryUpdate(LocalDateTime.now())
                .build();
        reviewSummaryRepository.save(entity);

        // 8) DTO 반환
        return ReviewSummaryDTO.builder()
                .lodgeId(lodgeId)
                .summaryText(summary)
                .summaryUpdate(entity.getSummaryUpdate())
                .build();
    }





    @Override
    @Transactional
    public void deleteByLodgeId(Long lodgeId) {
        reviewSummaryRepository.deleteByLodgeId(lodgeId);
    }
}
