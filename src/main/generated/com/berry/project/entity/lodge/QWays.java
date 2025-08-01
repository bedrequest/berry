package com.berry.project.entity.lodge;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWays is a Querydsl query type for Ways
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWays extends EntityPathBase<Ways> {

    private static final long serialVersionUID = 457024068L;

    public static final QWays ways = new QWays("ways");

    public final StringPath content = createString("content");

    public final NumberPath<Long> lodgeId = createNumber("lodgeId", Long.class);

    public final NumberPath<Long> waysId = createNumber("waysId", Long.class);

    public QWays(String variable) {
        super(Ways.class, forVariable(variable));
    }

    public QWays(Path<? extends Ways> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWays(PathMetadata metadata) {
        super(Ways.class, metadata);
    }

}

