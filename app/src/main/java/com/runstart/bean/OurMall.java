package com.runstart.bean;

/**
 * Created by zhouj on 2017-10-08.
 */

public class OurMall {
    /**
     * 定义OurMall的字段属性
     */
    public String commodityImage;
    public String commodityName;
    public String commodityPrice;
    /**
     * 定义OurMall字段的Getter/Setter
     */
    public String getCommodityImage() {
        return commodityImage;
    }

    public void setCommodityImage(String commodityImage) {
        this.commodityImage = commodityImage;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityPrice() {
        return commodityPrice;
    }

    public void setCommodityPrice(String commodityPrice) {
        this.commodityPrice = commodityPrice;
    }
}
