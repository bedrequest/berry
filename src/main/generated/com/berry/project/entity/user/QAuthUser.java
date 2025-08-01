package com.berry.project.entity.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuthUser is a Querydsl query type for AuthUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthUser extends EntityPathBase<AuthUser> {

    private static final long serialVersionUID = -868060987L;

    public static final QAuthUser authUser = new QAuthUser("authUser");

    public final com.berry.project.entity.QTimeBase _super = new com.berry.project.entity.QTimeBase(this);

    public final NumberPath<Long> authId = createNumber("authId", Long.class);

    public final StringPath authRole = createString("authRole");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QAuthUser(String variable) {
        super(AuthUser.class, forVariable(variable));
    }

    public QAuthUser(Path<? extends AuthUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuthUser(PathMetadata metadata) {
        super(AuthUser.class, metadata);
    }

}

