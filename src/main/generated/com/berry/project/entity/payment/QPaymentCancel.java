package com.berry.project.entity.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentCancel is a Querydsl query type for PaymentCancel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentCancel extends EntityPathBase<PaymentCancel> {

    private static final long serialVersionUID = -1352477849L;

    public static final QPaymentCancel paymentCancel = new QPaymentCancel("paymentCancel");

    public final NumberPath<Integer> cancelAmount = createNumber("cancelAmount", Integer.class);

    public final DateTimePath<java.time.OffsetDateTime> canceledAt = createDateTime("canceledAt", java.time.OffsetDateTime.class);

    public final StringPath cancelReason = createString("cancelReason");

    public final NumberPath<Long> paymentCancelId = createNumber("paymentCancelId", Long.class);

    public final StringPath paymentKey = createString("paymentKey");

    public final StringPath rawData = createString("rawData");

    public final StringPath transactionKey = createString("transactionKey");

    public QPaymentCancel(String variable) {
        super(PaymentCancel.class, forVariable(variable));
    }

    public QPaymentCancel(Path<? extends PaymentCancel> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentCancel(PathMetadata metadata) {
        super(PaymentCancel.class, metadata);
    }

}

