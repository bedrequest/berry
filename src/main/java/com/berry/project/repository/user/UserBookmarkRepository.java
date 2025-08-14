package com.berry.project.repository.user;

import com.berry.project.entity.user.UserBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBookmarkRepository extends JpaRepository<UserBookmark, Long> {
  Optional<UserBookmark> findByUserIdAndLodgeId(Long userId, Long lodge_id);

  List<UserBookmark> findByUserIdOrderByRegDateDesc(Long userId);

  void deleteByUserId(Long userid);
}
