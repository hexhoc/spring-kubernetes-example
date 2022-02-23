package com.example.orderservice.dto;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class CoffeeOrderPagedList extends PageImpl<CoffeeOrderDto> {
    public CoffeeOrderPagedList(List<CoffeeOrderDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public CoffeeOrderPagedList(List<CoffeeOrderDto> content) {
        super(content);
    }
}
