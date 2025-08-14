package com.berry.project.entity.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -785277955L;

    public static final QUser user = new QUser("user");

    public final com.berry.project.entity.QTimeBase _super = new com.berry.project.entity.QTimeBase(this);

    public final StringPath birthday = createString("birthday");

    public final StringPath customerKey = createString("customerKey");

    public final BooleanPath isAdult = createBoolean("isAdult");

    public final BooleanPath isEmailCertified = createBoolean("isEmailCertified");

    public final BooleanPath isMobileCertified = createBoolean("isMobileCertified");

    public final DateTimePath<java.time.LocalDateTime> lastLogin = createDateTime("lastLogin", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath password = createString("password");

    public final StringPath provider = createString("provider");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath userEmail = createString("userEmail");

    public final NumberPath<Integer> userFavoriteTag = createNumber("userFavoriteTag", Integer.class);

    public final StringPath userGrade = createString("userGrade");

    public final NumberPath<Integer> userGradePoint = createNumber("userGradePoint", Integer.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath userName = createString("userName");

    public final StringPath userPhone = createString("userPhone");

    public final BooleanPath userTermOption = createBoolean("userTermOption");

    public final StringPath userUid = createString("userUid");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

