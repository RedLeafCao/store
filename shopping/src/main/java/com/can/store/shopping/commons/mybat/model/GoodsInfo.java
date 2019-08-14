package com.can.store.shopping.commons.mybat.model;

/**
 * 商品详细信息
 * 2019.08.14
 */
public class GoodsInfo {

    private Integer id = null;
    //    goods_name.id+主要规格名
    private Integer goodsNameId;
    //    物品编号 如G0001
    private String sn;
    //    ena13条码,ena8条码,custmz:自定义
    private String snType;
    //    goods_name.category_id
    private Integer categoryId;
    //    goods_name.category_id_1
    private Integer categoryId1;
    //    goods_name.category_id_2
    private Integer categoryId2;
    //    goods_brand.id
    private Integer brandId = null;
    //    provider.id;
    private Integer providerId;
    //    goods_name.is_virtual
    private Integer isVirtual;
    //    是否展示到B端采购商城, 0-不显示，1-显示
    private Integer show2b;
    //    对B端销售价
    private Double price;
    //    对B端市场价
    private Double marketPrice;
    //    对B端折后价
    private Double discountPrice;
    //    厂商的进货价
    private Double purchasePrice;
    //    是否展示到C端积分商城, 0-不显示，1-显示
    private Integer show2c;
    //    可以兑换的积分数。如果为0，那么按订单价的10倍计算
    private Integer integralNum;
    //    对C端客户销售的积分价格
    private Integer priceIntegral = null;
    //    对C端客户销售的折后积分价格
    private Integer discountIntegral = null;
    //    单位g
    private Integer weight = null;
    //    单位mm
    private Integer length = null;
    //    单位mm
    private Integer height = null;
    //    单位mm
    private Integer width = null;
    //    颜色十六进制编码
    private String color = null;
    //    创建时间
    private Long createdAt;
    //    修改时间
    private Long updatedAt = null;
    //    商品数量
    private Integer stockNum = null;
    //    销量
    private Integer salesNum = null;
    //    积分兑换的数量
    private Integer exchangeNum = null;
    //    0:正常,1:无效
    private Integer status = null;
    //    伪删除：0=未删除，1=已删除
    private Integer deleted = null;
    //    物品名称
    private String goodsName;
    //    图片地址
    private String imageUrl;
    //    商品简单描述
    private String description = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGoodsNameId() {
        return goodsNameId;
    }

    public void setGoodsNameId(Integer goodsNameId) {
        this.goodsNameId = goodsNameId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSnType() {
        return snType;
    }

    public void setSnType(String snType) {
        this.snType = snType;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategoryId1() {
        return categoryId1;
    }

    public void setCategoryId1(Integer categoryId1) {
        this.categoryId1 = categoryId1;
    }

    public Integer getCategoryId2() {
        return categoryId2;
    }

    public void setCategoryId2(Integer categoryId2) {
        this.categoryId2 = categoryId2;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public Integer getIsVirtual() {
        return isVirtual;
    }

    public void setIsVirtual(Integer isVirtual) {
        this.isVirtual = isVirtual;
    }

    public Integer getShow2b() {
        return show2b;
    }

    public void setShow2b(Integer show2b) {
        this.show2b = show2b;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Integer getShow2c() {
        return show2c;
    }

    public void setShow2c(Integer show2c) {
        this.show2c = show2c;
    }

    public Integer getIntegralNum() {
        return integralNum;
    }

    public void setIntegralNum(Integer integralNum) {
        this.integralNum = integralNum;
    }

    public Integer getPriceIntegral() {
        return priceIntegral;
    }

    public void setPriceIntegral(Integer priceIntegral) {
        this.priceIntegral = priceIntegral;
    }

    public Integer getDiscountIntegral() {
        return discountIntegral;
    }

    public void setDiscountIntegral(Integer discountIntegral) {
        this.discountIntegral = discountIntegral;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getStockNum() {
        return stockNum;
    }

    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
    }

    public Integer getSalesNum() {
        return salesNum;
    }

    public void setSalesNum(Integer salesNum) {
        this.salesNum = salesNum;
    }

    public Integer getExchangeNum() {
        return exchangeNum;
    }

    public void setExchangeNum(Integer exchangeNum) {
        this.exchangeNum = exchangeNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GoodsInfo{");
        sb.append("id=").append(id);
        sb.append(", goodsNameId=").append(goodsNameId);
        sb.append(", sn='").append(sn).append('\'');
        sb.append(", snType='").append(snType).append('\'');
        sb.append(", categoryId=").append(categoryId);
        sb.append(", categoryId1=").append(categoryId1);
        sb.append(", categoryId2=").append(categoryId2);
        sb.append(", brandId=").append(brandId);
        sb.append(", providerId=").append(providerId);
        sb.append(", isVirtual=").append(isVirtual);
        sb.append(", show2b=").append(show2b);
        sb.append(", price=").append(price);
        sb.append(", marketPrice=").append(marketPrice);
        sb.append(", discountPrice=").append(discountPrice);
        sb.append(", purchasePrice=").append(purchasePrice);
        sb.append(", show2c=").append(show2c);
        sb.append(", integralNum=").append(integralNum);
        sb.append(", priceIntegral=").append(priceIntegral);
        sb.append(", discountIntegral=").append(discountIntegral);
        sb.append(", weight=").append(weight);
        sb.append(", length=").append(length);
        sb.append(", height=").append(height);
        sb.append(", width=").append(width);
        sb.append(", color='").append(color).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", stockNum=").append(stockNum);
        sb.append(", salesNum=").append(salesNum);
        sb.append(", exchangeNum=").append(exchangeNum);
        sb.append(", status=").append(status);
        sb.append(", deleted=").append(deleted);
        sb.append(", goodsName='").append(goodsName).append('\'');
        sb.append(", imageUrl='").append(imageUrl).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
