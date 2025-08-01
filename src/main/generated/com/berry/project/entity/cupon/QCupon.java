package com.berry.project.entity.cupon;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCupon is a Querydsl query type for Cupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCupon extends EntityPathBase<Cupon> {

    private static final long serialVersionUID = -680864773L;

    public static final QCupon cupon = new QCupon("cupon");

    public final DateTimePath<java.time.OffsetDateTime> cuponEndDate = createDateTime("cuponEndDate", java.time.OffsetDateTime.class);

    public final NumberPath<Long> cuponId = createNumber("cuponId", Long.class);

    public final DateTimePath<java.time.OffsetDateTime> cuponRegDate = createDateTime("cuponRegDate", java.time.OffsetDateTime.class);

    public final NumberPath<Integer> cuponType = createNumber("cuponType", Integer.class);

    public final BooleanPath isValid = createBoolean("isValid");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QCupon(String variable) {
        super(Cupon.class, forVariable(variable));
    }

    public QCupon(Path<? extends Cupon> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCupon(PathMetadata metadata) {
        super(Cupon.class, metadata);
    }

}

