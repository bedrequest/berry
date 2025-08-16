package com.berry.project.entity.user;

import com.berry.project.entity.TimeBase;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "auth_user")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthUser extends TimeBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "auth_id")
  private Long authId;

  @Column(nullable = false, name = "user_id")
  private Long userId;

  @Column(nullable = false, name = "auth_role")
  private String authRole;


}
