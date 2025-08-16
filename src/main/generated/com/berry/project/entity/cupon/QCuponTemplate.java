package com.berry.project.entity.cupon;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCuponTemplate is a Querydsl query type for CuponTemplate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCuponTemplate extends EntityPathBase<CuponTemplate> {

    private static final long serialVersionUID = 1226168725L;

    public static final QCuponTemplate cuponTemplate = new QCuponTemplate("cuponTemplate");

    public final NumberPath<Long> ctId = createNumber("ctId", Long.class);

    public final DateTimePath<java.time.OffsetDateTime> cuponEndDate = createDateTime("cuponEndDate", java.time.OffsetDateTime.class);

    public final StringPath cuponImgName = createString("cuponImgName");

    public final NumberPath<Long> cuponPrice = createNumber("cuponPrice", Long.class);

    public final StringPath cuponTitle = createString("cuponTitle");

    public final NumberPath<Integer> cuponType = createNumber("cuponType", Integer.class);

    public final NumberPath<Long> qty = createNumber("qty", Long.class);

    public final NumberPath<Long> theMinimumAmount = createNumber("theMinimumAmount", Long.class);

    public QCuponTemplate(String variable) {
        super(CuponTemplate.class, forVariable(variable));
    }

    public QCuponTemplate(Path<? extends CuponTemplate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCuponTemplate(PathMetadata metadata) {
        super(CuponTemplate.class, metadata);
    }

}

