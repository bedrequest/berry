package com.berry.project.handler.payment;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * OrderIdGenerator() - orderId 중복 방지를 위한 고유 ID 생성기
 * <p>
 * > Spring Bean으로 관리되어 Singleton 을 보장하며 orderId 의 중복 생성을 방지
 */
@Component
public class OrderIdGenerator {

  private final AtomicLong counter = new AtomicLong(0);

  /**
   * 고유한 orderId 생성
   * 형식: order_timestamp_counter_uuid
   * <p>
   * 고유한 orderId 를 return
   */
  public String generateOrderId() {
    long timestamp = System.currentTimeMillis();
    long count = counter.incrementAndGet();
    String uuid = UUID.randomUUID().toString().substring(0, 8);

    return String.format("order_%d_%d_%s", timestamp, count, uuid);
  }
}
