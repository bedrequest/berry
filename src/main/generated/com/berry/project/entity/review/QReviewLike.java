package com.berry.project.entity.review;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReviewLike is a Querydsl query type for ReviewLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewLike extends EntityPathBase<ReviewLike> {

    private static final long serialVersionUID = -2042153900L;

    public static final QReviewLike reviewLike = new QReviewLike("reviewLike");

    public final NumberPath<Long> likesNo = createNumber("likesNo", Long.class);

    public final NumberPath<Long> reviewId = createNumber("reviewId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QReviewLike(String variable) {
        super(ReviewLike.class, forVariable(variable));
    }

    public QReviewLike(Path<? extends ReviewLike> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReviewLike(PathMetadata metadata) {
        super(ReviewLike.class, metadata);
    }

}

