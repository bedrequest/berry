package com.berry.project.entity.lodge;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLodge is a Querydsl query type for Lodge
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLodge extends EntityPathBase<Lodge> {

    private static final long serialVersionUID = 1273082111L;

    public static final QLodge lodge = new QLodge("lodge");

    public final StringPath businessCall = createString("businessCall");

    public final NumberPath<Integer> facility = createNumber("facility", Integer.class);

    public final StringPath intro = createString("intro");

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final StringPath lodgeAddr = createString("lodgeAddr");

    public final NumberPath<Long> lodgeId = createNumber("lodgeId", Long.class);

    public final StringPath lodgeName = createString("lodgeName");

    public final StringPath lodgeType = createString("lodgeType");

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public QLodge(String variable) {
        super(Lodge.class, forVariable(variable));
    }

    public QLodge(Path<? extends Lodge> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLodge(PathMetadata metadata) {
        super(Lodge.class, metadata);
    }

}

