package com.sparta.cafeorderback.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "pointhistory")
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(nullable = false)
    private Long amount;

	@Column
    private String type;

	@Column(name = "charged_at")
    private LocalDateTime chargedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;

    public PointHistory(Users user, Long amount, String type) {
        this.user = user;
        this.amount = amount;
        this.type = type;
        this.chargedAt = LocalDateTime.now();
    }
}
