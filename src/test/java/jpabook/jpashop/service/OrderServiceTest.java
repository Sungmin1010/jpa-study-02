package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();

        int bookPrice = 1000;
        int bookStockQuantity = 100;
        Book book = createBook(bookPrice, bookStockQuantity, "테스트 책이름");

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);


        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 상품 가격 * 수량 이다", bookPrice * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량 만큼 재고가 줄어야 한다", bookStockQuantity - orderCount, book.getStockQuantity());

    }

    private Book createBook(int bookPrice, int bookStockQuantity, String name) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(bookPrice);
        book.setStockQuantity(bookStockQuantity);
        em.persist(book);
        return book;
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook(1000, 10, "테스트 이름");

        int OrderCount = 11;
        
        //when
        orderService.order(member.getId(), item.getId(), OrderCount);
        
        //then
        fail("재고 수량 부족 예외가 발생해야 한다.");

    }

    @Test
    public void 상품취소() throws Exception {
        //given
        Member member = createMember();
        Book item = createBook(1000, 10, "테스트 상품 이름");
        int OrderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), OrderCount);

        //when
        orderService.cancelOrder(orderId);


        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("주문 취소시 상태는 CANCLE이다.", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문 취소시 재고는 그만큼 증가해야 한다. ", 10, item.getStockQuantity());

    }

    private Member createMember(){
        Member member = new Member();
        member.setName("사용자1");
        member.setAddress(new Address("경기도", "거리", "123-123"));
        em.persist(member);
        return member;
    }



}