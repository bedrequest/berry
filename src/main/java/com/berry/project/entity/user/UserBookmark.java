package com.berry.project.entity.user;

import com.berry.project.entity.TimeBase;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_bookmark")
@Builder
public class UserBookmark extends TimeBase {
  @Id
  @Column(name = "user_bookmark_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userBookmarkId;
  @Column(nullable = false)
  private Long userId;
  @Column(nullable = false)
  private Long lodgeId;


}
