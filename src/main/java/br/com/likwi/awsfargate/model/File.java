package br.com.likwi.awsfargate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"fileNumber"})}
)
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String fileNumber;

    private String customer;

    private float total;

    private long productId;

    private int quantity;
}
