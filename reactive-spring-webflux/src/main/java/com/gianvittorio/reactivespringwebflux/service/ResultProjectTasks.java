package com.gianvittorio.reactivespringwebflux.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultProjectTasks {

    private String _id;
    private String name;
    private String taskName;
    private String taskOwnerName;
}
