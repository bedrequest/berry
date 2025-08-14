package com.berry.project.service.review;

import com.berry.project.dto.review.TagCountDTO;
import com.berry.project.entity.review.ReviewTag;

import java.util.List;

public interface ReviewTagService {
  List<ReviewTag> getAllTags();

  List<TagCountDTO> getTagCountsByLodge(Long lodgeId);
}
