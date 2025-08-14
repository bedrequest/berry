package com.berry.project.entity.cupon;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Table(name = "cupon")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cupon {
  @Id
  @Column(name = "cupon_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long cuponId;

  @Column(name = "user_id")
  private long userId;

  @Column(name = "cupon_type")
  private int cuponType;

  @CreationTimestamp
  @Column(name = "cupon_reg_date", columnDefinition = "TIMESTAMP")
  private OffsetDateTime cuponRegDate;

  @Column(name = "cupon_end_date", columnDefinition = "TIMESTAMP")
  private OffsetDateTime cuponEndDate;

  @Column(name = "is_valid", columnDefinition = "BOOLEAN DEFAULT TRUE")
  private boolean isValid;

}
