package com.gianvittorio.reactivespringwebflux.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultByStartDateAndCost {

    private String id;

    private Long total;
}
