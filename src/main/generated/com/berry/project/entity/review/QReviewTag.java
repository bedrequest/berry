package com.berry.project.entity.review;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReviewTag is a Querydsl query type for ReviewTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewTag extends EntityPathBase<ReviewTag> {

    private static final long serialVersionUID = 488320829L;

    public static final QReviewTag reviewTag = new QReviewTag("reviewTag");

    public final NumberPath<Long> tagId = createNumber("tagId", Long.class);

    public final StringPath tagName = createString("tagName");

    public QReviewTag(String variable) {
        super(ReviewTag.class, forVariable(variable));
    }

    public QReviewTag(Path<? extends ReviewTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReviewTag(PathMetadata metadata) {
        super(ReviewTag.class, metadata);
    }

}

