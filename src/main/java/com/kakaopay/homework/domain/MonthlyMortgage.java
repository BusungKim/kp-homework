package com.kakaopay.homework.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class MonthlyMortgage {

    @Id
    @GeneratedValue
    private long id;
    @Setter
    @Getter
    @Column(name = "YEAR", nullable = false)
    private int year;
    @Setter
    @Getter
    @Column(name = "MONTH", nullable = false)
    private int month;
    @Setter
    @Getter
    @Column(name = "AMOUNT_100M", nullable = false)
    private double amount100M;
    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "INSTITUTE", nullable = false)
    private Institute institute;

    @Builder
    public MonthlyMortgage(final int year, final int month, final double amount100M, final Institute institute) {
        this.year = year;
        this.month = month;
        this.amount100M = amount100M;
        this.institute = institute;
    }
}
