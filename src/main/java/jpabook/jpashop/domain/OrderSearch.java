package jpabook.jpashop.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderSearch {
    private String memberName;
    private OrderStatus orderStatus;

    public OrderSearch() {
    }
}
