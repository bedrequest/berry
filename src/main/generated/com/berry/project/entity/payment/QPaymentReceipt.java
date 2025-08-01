package com.berry.project.entity.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentReceipt is a Querydsl query type for PaymentReceipt
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentReceipt extends EntityPathBase<PaymentReceipt> {

    private static final long serialVersionUID = 1554934507L;

    public static final QPaymentReceipt paymentReceipt = new QPaymentReceipt("paymentReceipt");

    public final DateTimePath<java.time.OffsetDateTime> approvedAt = createDateTime("approvedAt", java.time.OffsetDateTime.class);

    public final StringPath lastTransactionKey = createString("lastTransactionKey");

    public final StringPath method = createString("method");

    public final StringPath orderId = createString("orderId");

    public final StringPath orderName = createString("orderName");

    public final StringPath paymentKey = createString("paymentKey");

    public final StringPath rawData = createString("rawData");

    public final DateTimePath<java.time.OffsetDateTime> requestedAt = createDateTime("requestedAt", java.time.OffsetDateTime.class);

    public final StringPath status = createString("status");

    public final NumberPath<Long> totalAmount = createNumber("totalAmount", Long.class);

    public final StringPath type = createString("type");

    public QPaymentReceipt(String variable) {
        super(PaymentReceipt.class, forVariable(variable));
    }

    public QPaymentReceipt(Path<? extends PaymentReceipt> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentReceipt(PathMetadata metadata) {
        super(PaymentReceipt.class, metadata);
    }

}

