package com.berry.project.entity.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDeactivatedUser is a Querydsl query type for DeactivatedUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeactivatedUser extends EntityPathBase<DeactivatedUser> {

    private static final long serialVersionUID = -391413015L;

    public static final QDeactivatedUser deactivatedUser = new QDeactivatedUser("deactivatedUser");

    public final com.berry.project.entity.QTimeBase _super = new com.berry.project.entity.QTimeBase(this);

    public final StringPath dReason = createString("dReason");

    public final StringPath dUserEmail = createString("dUserEmail");

    public final NumberPath<Long> dUserId = createNumber("dUserId", Long.class);

    public final StringPath dUserName = createString("dUserName");

    public final StringPath dUserPhone = createString("dUserPhone");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QDeactivatedUser(String variable) {
        super(DeactivatedUser.class, forVariable(variable));
    }

    public QDeactivatedUser(Path<? extends DeactivatedUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeactivatedUser(PathMetadata metadata) {
        super(DeactivatedUser.class, metadata);
    }

}

