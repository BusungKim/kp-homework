package com.kakaopay.homework.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "MONTHLY_MORTGAGE")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyMortgage {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @Getter
    @Column(name = "YEAR", nullable = false)
    private Integer year;

    @Setter
    @Getter
    @Column(name = "MONTH", nullable = false)
    private Integer month;

    @Setter
    @Getter
    @Column(name = "AMOUNT_100M", nullable = false)
    private Integer amount100M;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "INSTITUTE", nullable = false)
    private Institute institute;
}
