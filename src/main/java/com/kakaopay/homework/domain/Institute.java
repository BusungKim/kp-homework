package com.kakaopay.homework.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class Institute {

    @Id
    @GeneratedValue
    private long id;
    @Setter
    @Getter
    @Column(name = "NAME", nullable = false)
    private String name;
    @Setter
    @Getter
    @Column(name = "CODE", nullable = false)
    private String code;
    @Setter
    @Getter
    @OneToMany(mappedBy = "institute")
    private List<MonthlyMortgage> monthlyMortgageList = new ArrayList<>();

    @Builder
    public Institute(final String name, final String code, final List<MonthlyMortgage> monthlyMortgageList) {
        this.name = name;
        this.code = code;
        this.monthlyMortgageList = monthlyMortgageList;
    }
}
