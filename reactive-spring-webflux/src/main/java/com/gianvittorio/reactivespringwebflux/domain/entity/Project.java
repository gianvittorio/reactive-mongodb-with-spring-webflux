package com.gianvittorio.reactivespringwebflux.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "project")
public class Project {

    @Id
    private String id;

    private String name;

    private String code;

    @Field("desc")
    private String description;

    private String startDate;

    private String endDate;

    @Field("cost")
    private Long estimatedCost;

    private List<String> countryList;

    @Version
    private Long version;
}
