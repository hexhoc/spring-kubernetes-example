package com.example.coffeeservice.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coffee {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type="org.hibernate.type.UUIDCharType")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    private String coffeeName;

    @Enumerated(EnumType.STRING)
    private CoffeeStyleEnum coffeeStyle;

    @Column(unique = true)
    private String upc;

    private BigDecimal price;
    /**
     * Min on hand qty - used to trigger brew coffee
     */
    private Integer minOnHand;
    private Integer quantityToBrew;
    private Integer quantityOnHand;


    public boolean isNew() {
        return this.id == null;
    }

}
