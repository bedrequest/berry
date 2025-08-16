package com.berry.project.entity.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserBookmark is a Querydsl query type for UserBookmark
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserBookmark extends EntityPathBase<UserBookmark> {

    private static final long serialVersionUID = -2106959789L;

    public static final QUserBookmark userBookmark = new QUserBookmark("userBookmark");

    public final com.berry.project.entity.QTimeBase _super = new com.berry.project.entity.QTimeBase(this);

    public final NumberPath<Long> lodgeId = createNumber("lodgeId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> userBookmarkId = createNumber("userBookmarkId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserBookmark(String variable) {
        super(UserBookmark.class, forVariable(variable));
    }

    public QUserBookmark(Path<? extends UserBookmark> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserBookmark(PathMetadata metadata) {
        super(UserBookmark.class, metadata);
    }

}

