package com.can.store.shopping.dto;

import io.swagger.annotations.ApiModelProperty;

public class MerchantOrderDelivery {
    @ApiModelProperty("拥有者id")
    private Long userId;
    @ApiModelProperty("订单编号")
    private Long orderNo;
    @ApiModelProperty("物流单号")
    private Long deliverNo;

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getDeliverNo() {
        return deliverNo;
    }

    public void setDeliverNo(Long deliverNo) {
        this.deliverNo = deliverNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
