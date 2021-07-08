package com.hm.little.project.rocketmq.admin.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author <a href="mailto:little@163.com">little</a>
 * version: 1.0
 * Description:酒店房间详情dto
 **/
public class AdminHotelRoom {

    /**
     * 用户手机号
     */
    private String phoneNumber;

    /**
     * 房间id
     */
    private Long id;

    /**
     * "id":"4009",
     * "title":"豪华客房",
     * "pcate":"1981",
     * "thumb_url":"https://weapp-1303909892.file.myqcloud.com//image/20201221/6e222a7cc34f48db.jpg",
     * <p>
     * 房间名称
     */
    private String title;

    /**
     * 店铺id
     */
    private Long pcate;

    /**
     * 商品图片
     */
    private String thumbUrl;

    /**
     * 房间详细信息
     */
    private AdminRoomDescription roomDescription;

    /**
     * 房间图片信息
     */
    private List<AdminRoomPicture> goods_banner;

    /**
     * 参考价格
     */
    private BigDecimal marketprice;

    /**
     * 实际价格
     */
    private BigDecimal productprice;

    /**
     * 商品的数量
     */
    private Integer total;

    private Integer totalcnf;

    /**
     * 创建时间 unix时间
     */
    private Long createtime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public AdminRoomDescription getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(AdminRoomDescription roomDescription) {
        this.roomDescription = roomDescription;
    }

    public List<AdminRoomPicture> getGoods_banner() {
        return goods_banner;
    }

    public void setGoods_banner(List<AdminRoomPicture> goods_banner) {
        this.goods_banner = goods_banner;
    }

    public BigDecimal getMarketprice() {
        return marketprice;
    }

    public void setMarketprice(BigDecimal marketprice) {
        this.marketprice = marketprice;
    }

    public BigDecimal getProductprice() {
        return productprice;
    }

    public void setProductprice(BigDecimal productprice) {
        this.productprice = productprice;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPcate() {
        return pcate;
    }

    public void setPcate(Long pcate) {
        this.pcate = pcate;
    }

    public Integer getTotalcnf() {
        return totalcnf;
    }

    public void setTotalcnf(Integer totalcnf) {
        this.totalcnf = totalcnf;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}