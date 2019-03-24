package com.kakaopay.homework.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "INSTITUTE")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Institute {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @Getter
    @Column(name = "NAME", nullable = false)
    private String name;

    @Setter
    @Getter
    @Column(name = "CODE", nullable = false)
    private String code;

    @JsonIgnore
    @Setter
    @Getter
    @OneToMany(mappedBy = "institute")
    @Builder.Default
    private List<MonthlyMortgage> monthlyMortgageList = new ArrayList<>();
}
