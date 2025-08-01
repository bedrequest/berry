package com.berry.project.entity.review;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReviewImage is a Querydsl query type for ReviewImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewImage extends EntityPathBase<ReviewImage> {

    private static final long serialVersionUID = 1115077694L;

    public static final QReviewImage reviewImage = new QReviewImage("reviewImage");

    public final DateTimePath<java.time.LocalDateTime> modDate = createDateTime("modDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> regDate = createDateTime("regDate", java.time.LocalDateTime.class);

    public final StringPath reviewFileName = createString("reviewFileName");

    public final NumberPath<Long> reviewFileSize = createNumber("reviewFileSize", Long.class);

    public final NumberPath<Long> reviewId = createNumber("reviewId", Long.class);

    public final StringPath reviewSaveDir = createString("reviewSaveDir");

    public final StringPath reviewUuid = createString("reviewUuid");

    public QReviewImage(String variable) {
        super(ReviewImage.class, forVariable(variable));
    }

    public QReviewImage(Path<? extends ReviewImage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReviewImage(PathMetadata metadata) {
        super(ReviewImage.class, metadata);
    }

}

