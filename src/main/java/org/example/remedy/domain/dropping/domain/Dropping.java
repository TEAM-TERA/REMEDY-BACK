package org.example.remedy.domain.dropping.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collation = "dropping")
public class Dropping {
    @Id
    private String droppingId;

    private Long userId;

    private String songId;

    private String content;

    private Date expiryDate;
}
