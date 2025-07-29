package com.berry.project.entity.lodge;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRoomImg is a Querydsl query type for RoomImg
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoomImg extends EntityPathBase<RoomImg> {

    private static final long serialVersionUID = 406598152L;

    public static final QRoomImg roomImg = new QRoomImg("roomImg");

    public final NumberPath<Long> roomId = createNumber("roomId", Long.class);

    public final NumberPath<Long> roomImgId = createNumber("roomImgId", Long.class);

    public final StringPath roomImgUrl = createString("roomImgUrl");

    public QRoomImg(String variable) {
        super(RoomImg.class, forVariable(variable));
    }

    public QRoomImg(Path<? extends RoomImg> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoomImg(PathMetadata metadata) {
        super(RoomImg.class, metadata);
    }

}

