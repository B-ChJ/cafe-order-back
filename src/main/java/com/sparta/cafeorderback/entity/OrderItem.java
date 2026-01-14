package com.sparta.cafeorderback.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orderitems")
public class OrderItem extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

	@Column(nullable = false)
    private Long price;

	@Column(nullable = false)
    private int quantity = 1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

    public OrderItem(Menu menu, int quantity) {
        this.menu = menu;
        this.price = menu.getPrice();
        this.quantity = quantity;
    }

    void setOrder(Order order) {
        this.order = order;
    }
}
