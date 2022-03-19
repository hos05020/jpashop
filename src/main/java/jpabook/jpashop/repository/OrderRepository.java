package jpabook.jpashop.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.*;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static jpabook.jpashop.domain.QMember.*;
import static jpabook.jpashop.domain.QOrder.*;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    private JPAQueryFactory queryFactory;

    @Autowired
    public OrderRepository(EntityManager em) {
        queryFactory=new JPAQueryFactory(em);
    }

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class,id);
    }

    public List<Order> findAll(OrderSearch orderSearch){
        return queryFactory.selectFrom(order)
                .join(order.member, member)
                .where(memberNameEq(orderSearch.getMemberName()),orderStatusEq(orderSearch.getOrderStatus()))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression memberNameEq(String memberName) {
        if(StringUtils.hasText(memberName)){
            return member.name.like(memberName);
        }
        else{
            return null;
        }
    }

    private BooleanExpression orderStatusEq(OrderStatus status) {
        if(status!=null){
            return order.status.eq(status);
        }
        else{
            return null;
        }
    }


    public List<Order> findAllWithMemberDelivery() {
       return em.createQuery("select o from Order o join fetch o.member m join fetch o.delivery d",Order.class)
                .getResultList();
    }

    public List<Order> findAllWithItem() {
            return em.createQuery("select distinct o from Order o join fetch o.member m"+
                    " join fetch o.delivery d"+
                    " join fetch o.orderItems oi"+
                    " join fetch oi.item i",Order.class)
                    .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset,int limit){
        return em.createQuery("select o from Order o"+
                " join fetch o.member m" +
                " join fetch o.delivery d",Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
