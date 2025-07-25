package com.berry.project.crawling;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrawledReviewService {

    private final CrawledReviewRepository repo;

    /** 크롤링 결과 추가 */
    @Transactional
    public CrawledReview save(CrawledReview review) {
        return repo.save(review);
    }

    /** 전체 크롤링된 리뷰 리스트 반환 */
    public List<CrawledReview> findAll() {
        return repo.findAll();
    }

    /** ID로 단일 조회 */
    public CrawledReview findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 없음: " + id));
    }

    /** 전체 삭제 (선택적 활용) */
    @Transactional
    public void deleteAll() {
        repo.deleteAll();
    }
}
