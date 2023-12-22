package br.com.likwi.awsfargate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.UUID;

@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "code" }) })
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(length = 32, nullable = false)
    private String name;

    @Column(length = 24, nullable = false)
    private String model;

    @Column(length = 8, nullable = false, updatable = false)
    private String code;

    private float price;

    @Column(length = 10, nullable = true)
    private String color;

    @PrePersist
    public void prePersist() {
        this.model= this.model.concat("_exemplo@PrePersist");
        if (this.code == null)
            this.code = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public void setId(long id) {
        this.id = id;
    }

}
