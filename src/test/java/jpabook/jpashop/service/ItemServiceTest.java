package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {

    @Autowired ItemService itemService;
    @Autowired EntityManager em;

    @Test
    public void 상품_등록() throws Exception {
        //given
        Book book = new Book();
        book.setName("JPA BOOK");
        book.setPrice(10000);
        book.setAuthor("jenny");
        book.setStockQuantity(10);

        //when
        itemService.saveItem(book);
        Item findItem = itemService.findOne(book.getId());
        //then
        em.flush();
        assertEquals(book, findItem);


    }

    @Test(expected = NotEnoughStockException.class)
    @Rollback(false)
    public void 상품_재고_음수_예외() throws Exception {
        //given
        Book book = new Book();
        book.setName("JPA BOOK");
        book.setPrice(10000);
        book.setAuthor("jenny");
        book.setStockQuantity(10);
        itemService.saveItem(book);
        //em.flush();

        //when
        book.removeStock(11);

        //then
        fail("예외가 발생해야 한다");

    }

    @Test
    //@Rollback(false)
    public void 상품_재고_증가() throws Exception {
        //given
        Book book = new Book();
        book.setName("JPA BOOK");
        book.setPrice(10000);
        book.setAuthor("jenny");
        book.setStockQuantity(10);
        System.out.println("=========================");
        itemService.saveItem(book); //insert 문 저장
        System.out.println("=========================");

        //when
        book.addStock(10); //update 문 저장
        System.out.println("=========================");
        //then
        em.flush();
        System.out.println("============flush=============");
        assertEquals(20, itemService.findOne(book.getId()).getStockQuantity());

    }


}