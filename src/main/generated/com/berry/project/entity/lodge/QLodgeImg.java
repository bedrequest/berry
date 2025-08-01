package com.berry.project.entity.lodge;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLodgeImg is a Querydsl query type for LodgeImg
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLodgeImg extends EntityPathBase<LodgeImg> {

    private static final long serialVersionUID = 1828018756L;

    public static final QLodgeImg lodgeImg = new QLodgeImg("lodgeImg");

    public final NumberPath<Long> lodgeId = createNumber("lodgeId", Long.class);

    public final NumberPath<Long> lodgeImgId = createNumber("lodgeImgId", Long.class);

    public final StringPath lodgeImgUrl = createString("lodgeImgUrl");

    public QLodgeImg(String variable) {
        super(LodgeImg.class, forVariable(variable));
    }

    public QLodgeImg(Path<? extends LodgeImg> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLodgeImg(PathMetadata metadata) {
        super(LodgeImg.class, metadata);
    }

}

