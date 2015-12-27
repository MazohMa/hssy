package com.xpg.hssy.bean;

/**
 * @author Mazoh 2015 09 27
 * @version 2.4
 * @description 动态资讯
 */
public class DynamicInfo {

    private String coverImg;//动态图片
    private String title;//动态标题
    private String content;//动态文章内容
    private String createTime;//动态文章创建时间
    private String viewCount;//文章浏览次数
    private String issueTime;//文章更新时间
    private String id;//文章ID
    private String link;//网页链接

    public boolean isTop() {
        return isTop;
    }

    public void setIsTop(boolean isTop) {
        this.isTop = isTop;
    }

    private boolean isTop;//图片是否在顶部显示

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
