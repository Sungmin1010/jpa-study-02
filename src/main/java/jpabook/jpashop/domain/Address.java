package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

@Embeddable //내장타입
@Getter
public class Address {

    private String city;

    private String street;

    private String zipcode;

    protected Address() { //JPA 스펙상 객체 생성시 리플렉션 값은 기술을 사용하기 위해 기본 생성자 필요
    }

    public Address(String city, String street, String zipcode) { //값 타입은 변경 불가능하도록 설계
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
