package com.seeyon.v3x.online;

import com.seeyon.ctp.common.constants.Constants.LoginUserOnlineSubState;
import com.seeyon.ctp.common.constants.Constants.LoginUserState;
import com.seeyon.ctp.login.online.OnlineUser;
import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;

public class OnlineUserModel extends BaseModel implements Serializable {
	private static final long serialVersionUID = 910063119409420128L;
	private Long id;
	private String name;
	private String loginName;
	private String loginType;
	private LoginUserState state;
	private LoginUserOnlineSubState onlineSubState;
	private String departmentName;
	private String postName;
	private boolean isPluralist = false;
	private Long loginAccountId;
	private boolean isInternal = true;

	public Long getLoginAccountId() {
		return this.loginAccountId;
	}

	public void setLoginAccountId(Long loginAccountId) {
		this.loginAccountId = loginAccountId;
	}

	public OnlineUserModel(OnlineUser user) {
		this.id = user.getInternalId();
		this.isInternal = user.isInternal();
		this.loginName = user.getLoginName();
		this.loginType = user.getLoginType();
		this.name = user.getName();
		this.departmentName = user.getDepartmentSimpleName();
		this.postName = user.getPostName();
		this.state = user.getState();
		this.onlineSubState = user.getOnlineSubState();
		this.loginAccountId = user.getCurrentAccountId();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDepartmentName() {
		return this.departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginType() {
		return this.loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPostName() {
		return this.postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public boolean isPluralist() {
		return this.isPluralist;
	}

	public void setPluralist(boolean isPluralist) {
		this.isPluralist = isPluralist;
	}

	public LoginUserState getState() {
		return this.state;
	}

	public void setState(LoginUserState state) {
		this.state = state;
	}

	public LoginUserOnlineSubState getOnlineSubState() {
		return this.onlineSubState;
	}

	public void setOnlineSubState(LoginUserOnlineSubState onlineSubState) {
		this.onlineSubState = onlineSubState;
	}

	public boolean isInternal() {
		return this.isInternal;
	}

	public void setInternal(boolean isInternal) {
		this.isInternal = isInternal;
	}
}
