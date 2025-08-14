package com.berry.project.entity.review;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReviewSummary is a Querydsl query type for ReviewSummary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewSummary extends EntityPathBase<ReviewSummary> {

    private static final long serialVersionUID = -1626763831L;

    public static final QReviewSummary reviewSummary = new QReviewSummary("reviewSummary");

    public final NumberPath<Long> lodgeId = createNumber("lodgeId", Long.class);

    public final NumberPath<Long> summaryId = createNumber("summaryId", Long.class);

    public final StringPath summaryText = createString("summaryText");

    public final DateTimePath<java.time.LocalDateTime> summaryUpdate = createDateTime("summaryUpdate", java.time.LocalDateTime.class);

    public QReviewSummary(String variable) {
        super(ReviewSummary.class, forVariable(variable));
    }

    public QReviewSummary(Path<? extends ReviewSummary> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReviewSummary(PathMetadata metadata) {
        super(ReviewSummary.class, metadata);
    }

}

