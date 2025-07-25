package com.berry.project.service.review;

import com.berry.project.entity.review.ReviewTag;
import com.berry.project.repository.review.ReviewTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewTagServiceImpl implements ReviewTagService {
    private final ReviewTagRepository tagRepo;

    @Override
    public List<ReviewTag> getAllTags() {
        return tagRepo.findAll();
    }
}