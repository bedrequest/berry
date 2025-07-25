package com.berry.project.entity.cupon;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Table(name="cupon")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cupon {
  @Id
  @Column(name="cupon_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long cuponId;

  @Column(name="user_id")
  private long userId;

  @Column(name="cupon_type")
  private int cuponType;

  @CreationTimestamp
  @Column(name="cupon_reg_date")
  private LocalDateTime cuponRegDate;

  @Column(name="cupon_end_date")
  private LocalDateTime cuponEndDate;

  @Column(name="is_valid", columnDefinition = "BOOLEAN DEFAULT TRUE")
  private boolean isValid;
}
