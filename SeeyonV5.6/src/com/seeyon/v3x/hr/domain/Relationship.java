package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;
import java.util.Date;

public class Relationship extends BaseModel implements Serializable {
	private static final long serialVersionUID = 7763903921325236630L;
	private String relationship;
	private String name;
	private String organization;
	private String post;
	private Date birthday;
	private int political_position;
	private Long member_id;
	private String cardid;
	private String tel;

	public String getCardid() {
		return cardid;
	}

	public void setCardid(String cardid) {
		this.cardid = cardid;
	}

	public Long getMember_id() {
		return this.member_id;
	}

	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}

	public Date getBirthday() {
		return this.birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public int getPolitical_position() {
		return this.political_position;
	}

	public void setPolitical_position(int political_position) {
		this.political_position = political_position;
	}

	public String getPost() {
		return this.post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getRelationship() {
		return this.relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

}
