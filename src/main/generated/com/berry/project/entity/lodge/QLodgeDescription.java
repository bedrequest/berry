package com.berry.project.entity.lodge;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLodgeDescription is a Querydsl query type for LodgeDescription
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLodgeDescription extends EntityPathBase<LodgeDescription> {

    private static final long serialVersionUID = -171756163L;

    public static final QLodgeDescription lodgeDescription = new QLodgeDescription("lodgeDescription");

    public final StringPath content = createString("content");

    public final NumberPath<Long> lodgeDescriptionId = createNumber("lodgeDescriptionId", Long.class);

    public final NumberPath<Long> lodgeId = createNumber("lodgeId", Long.class);

    public final StringPath title = createString("title");

    public QLodgeDescription(String variable) {
        super(LodgeDescription.class, forVariable(variable));
    }

    public QLodgeDescription(Path<? extends LodgeDescription> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLodgeDescription(PathMetadata metadata) {
        super(LodgeDescription.class, metadata);
    }

}

