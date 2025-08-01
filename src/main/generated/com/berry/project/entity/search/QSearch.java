package com.berry.project.entity.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSearch is a Querydsl query type for Search
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSearch extends EntityPathBase<Search> {

    private static final long serialVersionUID = -786682595L;

    public static final QSearch search = new QSearch("search");

    public final StringPath detail = createString("detail");

    public final StringPath jasoDetail = createString("jasoDetail");

    public final StringPath jasoKeyword = createString("jasoKeyword");

    public final StringPath keyword = createString("keyword");

    public final NumberPath<Long> lodgeId = createNumber("lodgeId", Long.class);

    public final NumberPath<Long> searchId = createNumber("searchId", Long.class);

    public QSearch(String variable) {
        super(Search.class, forVariable(variable));
    }

    public QSearch(Path<? extends Search> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSearch(PathMetadata metadata) {
        super(Search.class, metadata);
    }

}

