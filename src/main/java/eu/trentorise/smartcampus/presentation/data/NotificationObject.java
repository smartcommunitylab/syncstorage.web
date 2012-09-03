package eu.trentorise.smartcampus.presentation.data;

import java.util.Map;

public class NotificationObject {

	private String id;
	private String title;
	private String description;
	private String type;
	private String user;
	private Map<String, Object> content;
	private long timestamp;
	private boolean starred;
	private String[] labelIds;
	private String funnelId;
	private EntityObject[] entities;

	private boolean readed;

	public NotificationObject() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Map<String, Object> getContent() {
		return content;
	}

	public void setContent(Map<String, Object> content) {
		this.content = content;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isStarred() {
		return starred;
	}

	public void setStarred(boolean starred) {
		this.starred = starred;
	}

	public String[] getLabels() {
		return labelIds;
	}

	public void setLabels(String[] labelIds) {
		this.labelIds = labelIds;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getLabelIds() {
		return labelIds;
	}

	public void setLabelIds(String[] labelIds) {
		this.labelIds = labelIds;
	}

	public String getFunnelId() {
		return funnelId;
	}

	public void setFunnelId(String funnelId) {
		this.funnelId = funnelId;
	}

	public EntityObject[] getEntities() {
		return entities;
	}

	public void setEntities(EntityObject[] entities) {
		this.entities = entities;
	}

	public boolean isReaded() {
		return readed;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}

}
