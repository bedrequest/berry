package com.berry.project.entity.review;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = -1076500707L;

    public static final QReview review = new QReview("review");

    public final StringPath aiSummary = createString("aiSummary");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> lodgeId = createNumber("lodgeId", Long.class);

    public final NumberPath<Integer> rating = createNumber("rating", Integer.class);

    public final NumberPath<Integer> reportedCount = createNumber("reportedCount", Integer.class);

    public final NumberPath<Long> reservationId = createNumber("reservationId", Long.class);

    public final NumberPath<Long> reviewId = createNumber("reviewId", Long.class);

    public final StringPath userEmail = createString("userEmail");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QReview(String variable) {
        super(Review.class, forVariable(variable));
    }

    public QReview(Path<? extends Review> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReview(PathMetadata metadata) {
        super(Review.class, metadata);
    }

}

