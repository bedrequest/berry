package com.berry.project.crawling;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCrawledReview is a Querydsl query type for CrawledReview
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCrawledReview extends EntityPathBase<CrawledReview> {

    private static final long serialVersionUID = -1054114779L;

    public static final QCrawledReview crawledReview = new QCrawledReview("crawledReview");

    public final StringPath aiSummary = createString("aiSummary");

    public final StringPath content = createString("content");

    public final NumberPath<Long> crawledId = createNumber("crawledId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> lodgeId = createNumber("lodgeId", Long.class);

    public final NumberPath<Integer> rating = createNumber("rating", Integer.class);

    public final NumberPath<Integer> reportedCount = createNumber("reportedCount", Integer.class);

    public final NumberPath<Long> reservationId = createNumber("reservationId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QCrawledReview(String variable) {
        super(CrawledReview.class, forVariable(variable));
    }

    public QCrawledReview(Path<? extends CrawledReview> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCrawledReview(PathMetadata metadata) {
        super(CrawledReview.class, metadata);
    }

}

