package net.lylab.vicp.web.orm.model;

import java.util.Date;

/**
 *
 * 用户授权医生实体类
 * 
 * @author ZhuBinfeng
 * @version 1.0 2015/10/10
 */
public class AuthDoctor {
	private Integer auth_doctor_id;

	private Integer user_id;

	private Integer doctor_type;

	private Integer doctor_id;

	private Date auth_time;

	private Date expired_time;

	public Integer getAuth_doctor_id() {
		return auth_doctor_id;
	}

	public void setAuth_doctor_id(Integer auth_doctor_id) {
		this.auth_doctor_id = auth_doctor_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getDoctor_type() {
		return doctor_type;
	}

	public void setDoctor_type(Integer doctor_type) {
		this.doctor_type = doctor_type;
	}

	public Integer getDoctor_id() {
		return doctor_id;
	}

	public void setDoctor_id(Integer doctor_id) {
		this.doctor_id = doctor_id;
	}

	public Date getAuth_time() {
		return auth_time;
	}

	public void setAuth_time(Date auth_time) {
		this.auth_time = auth_time;
	}

	public Date getExpired_time() {
		return expired_time;
	}

	public void setExpired_time(Date expired_time) {
		this.expired_time = expired_time;
	}
}