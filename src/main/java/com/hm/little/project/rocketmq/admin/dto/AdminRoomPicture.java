package com.hm.little.project.rocketmq.admin.dto;

/**
 * @author <a href="mailto:little@163.com">little</a>
 * version: 1.0
 * Description:房间图片信息
 **/
public class AdminRoomPicture {

    /**
     * 图片id
     */
    private Integer id;

    /**
     * 图片地址
     */
    private String url;

    private String src;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}