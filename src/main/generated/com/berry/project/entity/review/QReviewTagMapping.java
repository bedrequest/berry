package com.berry.project.entity.review;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReviewTagMapping is a Querydsl query type for ReviewTagMapping
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewTagMapping extends EntityPathBase<ReviewTagMapping> {

    private static final long serialVersionUID = -1416736047L;

    public static final QReviewTagMapping reviewTagMapping = new QReviewTagMapping("reviewTagMapping");

    public final NumberPath<Long> mappingId = createNumber("mappingId", Long.class);

    public final NumberPath<Long> reviewId = createNumber("reviewId", Long.class);

    public final NumberPath<Long> tagId = createNumber("tagId", Long.class);

    public QReviewTagMapping(String variable) {
        super(ReviewTagMapping.class, forVariable(variable));
    }

    public QReviewTagMapping(Path<? extends ReviewTagMapping> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReviewTagMapping(PathMetadata metadata) {
        super(ReviewTagMapping.class, metadata);
    }

}

