package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired OrderService orderService;
    @Autowired
    OrderRepository orderRepository;


    @Test
    public void 상품주문() throws Exception{

        Member member = createMember();
        Book book = createBook("수학의정석", 10000, 10);
        int orderCount=2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order getOrder = orderRepository.findOne(orderId);
        Assertions.assertEquals( OrderStatus.ORDER,getOrder.getStatus());
        Assertions.assertEquals(1,getOrder.getOrderItems().size());
        Assertions.assertEquals(20000,getOrder.getTotalPrice());
        Assertions.assertEquals(8,book.getStockQuantity());

    }

    @Test(expected = NotEnoughStockException.class)
    public void 재고수량초과() throws Exception{
        Member member = createMember();
        Book book = createBook("수학의정석", 10000, 10);
        int orderCount=11;
        orderService.order(member.getId(),book.getId(),orderCount);
        fail("재고 수량부족예외가 발생해야 한다");
    }

    @Test
    public void 주문취소(){
        Member member = createMember();
        Book book = createBook("수학의정석", 10000, 10);
        int orderCount=2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        orderService.cancelOrder(orderId);

        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL,getOrder.getStatus());
        assertEquals(10,book.getStockQuantity());
    }

    private Member createMember(){
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","강남구","06262"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name,int price,int stockQuantity){
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }

}