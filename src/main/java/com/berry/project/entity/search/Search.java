package com.berry.project.entity.search;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Search {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "search_id")
  private Long searchId;

  @Column(nullable = false)
  private String keyword;

  @Column(nullable = false)
  private String detail;

  @Column(name = "jaso_keyword", nullable = false)
  private String jasoKeyword;

  @Column(name = "jaso_detail", nullable = false)
  private String jasoDetail;

  @Column(name = "lodge_id")
  private Long lodgeId;

}