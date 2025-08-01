package com.berry.project.entity.qna;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCustomerIqBoard is a Querydsl query type for CustomerIqBoard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCustomerIqBoard extends EntityPathBase<CustomerIqBoard> {

    private static final long serialVersionUID = 429269445L;

    public static final QCustomerIqBoard customerIqBoard = new QCustomerIqBoard("customerIqBoard");

    public final com.berry.project.entity.QTimeBase _super = new com.berry.project.entity.QTimeBase(this);

    public final NumberPath<Long> bno = createNumber("bno", Long.class);

    public final StringPath category = createString("category");

    public final StringPath comment = createString("comment");

    public final DateTimePath<java.time.LocalDateTime> commentRegDate = createDateTime("commentRegDate", java.time.LocalDateTime.class);

    public final StringPath content = createString("content");

    public final BooleanPath isSecret = createBoolean("isSecret");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath title = createString("title");

    public final StringPath userEmail = createString("userEmail");

    public QCustomerIqBoard(String variable) {
        super(CustomerIqBoard.class, forVariable(variable));
    }

    public QCustomerIqBoard(Path<? extends CustomerIqBoard> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCustomerIqBoard(PathMetadata metadata) {
        super(CustomerIqBoard.class, metadata);
    }

}

