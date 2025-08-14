package com.berry.project.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagMaskDecoder {
  private final String[] strArray = {
      "깨끗해요", "경치가 좋아요", "인테리어가 좋아요",
      "친절해요", "방음이 좋아요", "대중교통이 편해요",
      "아이와 가기 좋아요", "즐길거리가 많아요", "조용히 쉬기 좋아요"
  };

  public int encode(List<String> tags) {
    int answer = 0;
    for (int i = 0; i < strArray.length; i++)
      if (tags.contains(strArray[i])) answer |= (1 << i);

    return answer;
  }

  public String get(int idx) {
    return strArray[idx];
  }

  public List<String> get(List<Integer> idxList) {
    return idxList.stream().map(i -> strArray[i]).toList();
  }

  public List<String> decode(int mask) {
    List<String> answer = new ArrayList<>();

    int count = 1;
    for (String tag : strArray) {
      if ((mask & count) > 0) answer.add(tag);
      count <<= 1;
    }

    return answer;
  }

  public List<Integer> decodeAsNumber(int mask) {
    List<Integer> answer = new ArrayList<>();

    int count = 1;
    for (int number = 1; number <= strArray.length; number++) {
      if ((mask & count) > 0) answer.add(number);
      count <<= 1;
    }
    return answer;
  }
}
