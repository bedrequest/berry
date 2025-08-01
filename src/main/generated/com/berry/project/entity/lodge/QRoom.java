package com.berry.project.entity.lodge;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRoom is a Querydsl query type for Room
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoom extends EntityPathBase<Room> {

    private static final long serialVersionUID = 456888251L;

    public static final QRoom room = new QRoom("room");

    public final StringPath info = createString("info");

    public final NumberPath<Long> lodgeId = createNumber("lodgeId", Long.class);

    public final NumberPath<Integer> maxCount = createNumber("maxCount", Integer.class);

    public final NumberPath<Integer> rentPrice = createNumber("rentPrice", Integer.class);

    public final StringPath rentTime = createString("rentTime");

    public final NumberPath<Long> roomId = createNumber("roomId", Long.class);

    public final StringPath roomName = createString("roomName");

    public final NumberPath<Integer> standardCount = createNumber("standardCount", Integer.class);

    public final StringPath stayOption = createString("stayOption");

    public final NumberPath<Integer> stayPrice = createNumber("stayPrice", Integer.class);

    public final StringPath stayTime = createString("stayTime");

    public final NumberPath<Integer> stockCount = createNumber("stockCount", Integer.class);

    public QRoom(String variable) {
        super(Room.class, forVariable(variable));
    }

    public QRoom(Path<? extends Room> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoom(PathMetadata metadata) {
        super(Room.class, metadata);
    }

}

