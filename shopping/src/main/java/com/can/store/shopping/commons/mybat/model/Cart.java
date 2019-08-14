package com.can.store.shopping.commons.mybat.model;

/**
 * 购物车信息详情
 * 2019.08.14
 */
public class Cart {
    private Integer id = null;
    private Long userId;
    private Long goodId;
    private Integer quantity;
    private Integer isChosen = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getIsChosen() {
        return isChosen;
    }

    public void setIsChosen(Integer isChosen) {
        this.isChosen = isChosen;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Cart{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", goodId=").append(goodId);
        sb.append(", quantity=").append(quantity);
        sb.append(", isChosen=").append(isChosen);
        sb.append('}');
        return sb.toString();
    }
}
