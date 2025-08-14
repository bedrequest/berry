package com.berry.project.repository.user;

import com.berry.project.entity.user.QUser;
import com.berry.project.entity.user.User;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static com.berry.project.entity.user.QUser.user;


@Slf4j
public class UserCustomRepositoryImpl implements UserCustomRepository {
  // 초기화
  private final JPAQueryFactory queryFactory;

  public UserCustomRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }


  /** pageUser(String keyword, Pageable pageable, String sortType)
   *   - User 의 Record 를 Page<User> 로 변환
   * */
  @Override
  public Page<User> pageUser(String keyword, Pageable pageable) {

    // 검색
    BooleanExpression predicate = anyOfNonNull(
      user.userName.containsIgnoreCase(keyword),
      user.userEmail.containsIgnoreCase(keyword),
      user.userPhone.containsIgnoreCase(keyword),
      user.provider.containsIgnoreCase(keyword),
      eqLong(user.userId, keyword) // 숫자면 userId 일치를 위한 메서드
    );

    /* 검색 2 - 동적 조건 적용 (null이면 where에서 자동 무시) */
//    BooleanExpression predicate =
//        contains(user.userName, kw)
//            .or(contains(user.userEmail, kw))
//            .or(contains(user.userPhone, kw))
//            .or(contains(user.provider, kw))
//            .or(contains(user.userUid, kw))
//            .or(eqIfNumber(user.userId, kw));


    // 쿼리 작성 및 페이지 적용
    List<User> result = queryFactory
        .selectFrom(user)
        .where(predicate)
        .orderBy(pageableOfSortInPageUser(pageable.getSort(), user)) // 화이트리스트 매핑 사용
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 필터링된 전체 데이터 개수 조회
    Long total = queryFactory
        .select(user.count())
        .from(user)
        .where(predicate)
        .fetchOne();

    return new PageImpl<>(result, pageable, (total == null) ? 0 : total);
  }


  /** (헬퍼 함수) anyOfNonNull() - 여러 BooleanExpression 을 OR 로 묶되 null 인 것은 건너뛰어 NPE 를 피하는 함수
   *
   *  > 가변인자 파라미터 (vargs; variable arguments) exprs 를 의미하며 가변 인자는 함수나 메서드에서
   *   매개변수의 개수가 고정되지 않고, 필요에 따라 동적으로 변하는 인자를 받을 수 있도록 하는 기능
   *
   * */
  private BooleanExpression anyOfNonNull(BooleanExpression... exprs){
    BooleanExpression cond = null;

    for(BooleanExpression b : exprs){
      if(b != null) cond = (cond == null) ? b : cond.or(b);
    }

    return cond;
  }


  /** (헬퍼 함수) pageableOfSortInPageUser() - 화이트리스트 */
  private OrderSpecifier<?>[] pageableOfSortInPageUser(Sort sort, QUser user){
    // 초기화
    List<OrderSpecifier<?>> list = new ArrayList<>();

    for(Sort.Order o : sort){
      Order dir = o.isAscending() ? Order.ASC : Order.DESC;

      // switch는 뷰에서 온 Value (e.g., LATEST-SIGN-UP) 이 아니라 정렬에 사용할 엔티티 필드명을 받아야 함
      switch(o.getProperty()){
        // 정렬 기준을 추가하고 싶다면 case 를 추가 하면 됨
        case "regDate" -> list.add(new OrderSpecifier<>(dir, user.regDate));
        case "lastLogin" -> list.add(new OrderSpecifier<>(dir, user.lastLogin));
        default -> { break; }
      }
    }

    return list.toArray(new OrderSpecifier<?>[0]);
  }

  /** (헬퍼 함수) eqLong() - 숫자로 파싱되면 eq 조건 아닌 경우 null 을 반환 */
  private BooleanExpression eqLong(NumberPath<Long> path, String kw){
    if(kw == null) { return null; }

    try{ return path.eq(Long.parseLong(kw)); }

    catch(NumberFormatException e){ return null; }
  }


  /** (헬퍼 함수) contains() - kw 가 null 이면 null 을 반환 (WHERE 에서 무시됨) */
  private BooleanExpression contains(StringPath path, String kw){
    return (kw == null) ? null : path.containsIgnoreCase(kw);
  }

}
