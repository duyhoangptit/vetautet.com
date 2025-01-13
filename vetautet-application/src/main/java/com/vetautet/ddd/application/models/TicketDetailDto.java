package com.vetautet.ddd.application.models;

import lombok.Data;

import java.util.Date;

@Data
public class TicketDetailDto {
    private Long id;
    private String name;
    private String description;
    private int stockInitial;
    private int stockAvailable;
    private boolean isStockPrepared;
    private Long priceOriginal;
    private Long priceFlash;
    private Date saleStartTime;
    private Date saleEndTime;
    private int status;
    private Long activityId;
    private Date updatedAt;
    private Date createdAt;
    private Long version;
}
