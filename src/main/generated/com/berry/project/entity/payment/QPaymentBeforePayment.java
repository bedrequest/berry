package com.berry.project.entity.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentBeforePayment is a Querydsl query type for PaymentBeforePayment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentBeforePayment extends EntityPathBase<PaymentBeforePayment> {

    private static final long serialVersionUID = -483896326L;

    public static final QPaymentBeforePayment paymentBeforePayment = new QPaymentBeforePayment("paymentBeforePayment");

    public final NumberPath<Long> cuponId = createNumber("cuponId", Long.class);

    public final NumberPath<Long> cuponPrice = createNumber("cuponPrice", Long.class);

    public final StringPath customerKey = createString("customerKey");

    public final StringPath method = createString("method");

    public final StringPath orderId = createString("orderId");

    public final StringPath orderName = createString("orderName");

    public final DateTimePath<java.time.OffsetDateTime> orderRegDate = createDateTime("orderRegDate", java.time.OffsetDateTime.class);

    public final NumberPath<Long> paymentId = createNumber("paymentId", Long.class);

    public final NumberPath<Long> pbpTotalAmount = createNumber("pbpTotalAmount", Long.class);

    public final NumberPath<Long> strikePrice = createNumber("strikePrice", Long.class);

    public QPaymentBeforePayment(String variable) {
        super(PaymentBeforePayment.class, forVariable(variable));
    }

    public QPaymentBeforePayment(Path<? extends PaymentBeforePayment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentBeforePayment(PathMetadata metadata) {
        super(PaymentBeforePayment.class, metadata);
    }

}

