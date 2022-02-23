package com.example.inventoryservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeOrderLineDto {

    private UUID id = null;
    private Integer version = null;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
    private OffsetDateTime createdDate = null;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
    private OffsetDateTime lastModifiedDate = null;

    private UUID coffeeId;

    private String upc;
    private String coffeeName;
    private String coffeeStyle;

    @NotNull
    @Positive
    private Integer orderQuantity;

    @Null
    private Integer quantityAllocated;

    @JsonFormat(shape= JsonFormat.Shape.STRING)
    private BigDecimal price;
}
