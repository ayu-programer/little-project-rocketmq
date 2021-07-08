package com.hm.little.project.rocketmq.admin.dto;

/**
 * @author <a href="mailto:little@163.com">little</a>
 * version: 1.0
 * Description:房间描述信息
 **/
public class AdminRoomDescription {

    /**
     * 面积
     */
    private String area;

    /**
     * 宽高
     */
    private String bed;

    /**
     * 早餐的份数
     */
    private Integer breakfast;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public Integer getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(Integer breakfast) {
        this.breakfast = breakfast;
    }
}