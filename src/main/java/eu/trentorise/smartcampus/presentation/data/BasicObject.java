package eu.trentorise.smartcampus.presentation.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BasicObject implements Serializable {

	private String id;
	private long updateTime = -1L;
	private long version;
	private String user;
	
	public BasicObject() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "BasicObject [id=" + id + ", updateTime=" + updateTime
				+ ", version=" + version + ", user=" + user + "]";
	}
}