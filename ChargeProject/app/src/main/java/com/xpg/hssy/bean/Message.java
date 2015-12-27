package com.xpg.hssy.bean;

public class Message {
	private String id;
	private String userid;
	private Integer type;
	private String title;
	private String content;
	private Long createTime;
	private Long noticeTime;
	private String typeId;
	private Integer status; // 0未读、1已读、2全部

	public final static int UNREAD = 0;
	public final static int HAVE_READ = 1;
	public final static int ALL = 2;

	public final static int MESSAGE_TYPE_ORDER=1;
	public final static int MESSAGE_TYPE_CHARGE_START=2;
	public final static int MESSAGE_TYPE_PUBLIC_MARKER=3;
	public final static int MESSAGE_TYPE_CHARGE_TIME_LINE=4;

	public Message(String id, String userid, int type, String title,
			String content, Long createTime, Long noticeTime, String typeId,
			int status) {
		this.id = id;
		this.userid = userid;
		this.type = type;
		this.title = title;
		this.content = content;
		this.createTime = createTime;
		this.noticeTime = noticeTime;
		this.typeId = typeId;
		this.status = status;
	}

	public Message(String id, int status) {
		this.id = id;
		this.status = status;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(Long noticeTime) {
		this.noticeTime = noticeTime;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
