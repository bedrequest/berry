package com.berry.project.entity.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReservation is a Querydsl query type for Reservation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReservation extends EntityPathBase<Reservation> {

    private static final long serialVersionUID = -778892077L;

    public static final QReservation reservation = new QReservation("reservation");

    public final StringPath bookingStatus = createString("bookingStatus");

    public final DateTimePath<java.time.OffsetDateTime> endDate = createDateTime("endDate", java.time.OffsetDateTime.class);

    public final NumberPath<Integer> guestsAmount = createNumber("guestsAmount", Integer.class);

    public final StringPath orderId = createString("orderId");

    public final NumberPath<Long> reservationId = createNumber("reservationId", Long.class);

    public final DateTimePath<java.time.OffsetDateTime> reservationRegDate = createDateTime("reservationRegDate", java.time.OffsetDateTime.class);

    public final StringPath reservationType = createString("reservationType");

    public final NumberPath<Long> roomId = createNumber("roomId", Long.class);

    public final DateTimePath<java.time.OffsetDateTime> startDate = createDateTime("startDate", java.time.OffsetDateTime.class);

    public final NumberPath<Integer> totalAmount = createNumber("totalAmount", Integer.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QReservation(String variable) {
        super(Reservation.class, forVariable(variable));
    }

    public QReservation(Path<? extends Reservation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReservation(PathMetadata metadata) {
        super(Reservation.class, metadata);
    }

}

