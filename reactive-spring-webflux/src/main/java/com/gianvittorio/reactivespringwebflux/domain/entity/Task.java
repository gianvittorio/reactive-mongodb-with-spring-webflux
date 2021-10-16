package com.gianvittorio.reactivespringwebflux.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "task")
public class Task {

    @Id
    private String id;

    @Field("pid")
    private String projectId;

    private String name;

    @Field("desc")
    private String description;

    private String ownerName;

    private long cost;

    @Version
    private Long version;
}
