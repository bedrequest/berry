package com.berry.project.service.review;

import com.berry.project.dto.review.TagCountDTO;
import com.berry.project.entity.review.ReviewTag;
import com.berry.project.repository.review.ReviewTagMappingRepository;
import com.berry.project.repository.review.ReviewTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewTagServiceImpl implements ReviewTagService {
  private final ReviewTagRepository reviewTagRepository;
  private final ReviewTagMappingRepository reviewTagMappingRepository;


  @Override
  public List<ReviewTag> getAllTags() {
    return reviewTagRepository.findAll();
  }

  @Override
  public List<TagCountDTO> getTagCountsByLodge(Long lodgeId) {
    // DB에서 모든 태그 카운트를 가져온 뒤, 내림차순 정렬해서 상위 3개만 리턴
    return reviewTagMappingRepository.countTagsByLodge(lodgeId).stream()
        .sorted(Comparator.comparingLong(TagCountDTO::count).reversed())
        .limit(3)
        .toList();
  }

}