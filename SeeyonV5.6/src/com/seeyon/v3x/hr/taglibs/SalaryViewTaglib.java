package com.seeyon.v3x.hr.taglibs;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.hr.webmodel.WebProperty;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.collections.CollectionUtils;

public class SalaryViewTaglib extends BodyTagSupport {
	private static final long serialVersionUID = 7052244567816556768L;
	public static final String TAG_NAME = "salaryViewTag";
	private List<WebProperty> properties;
	private String language;
	private String model;
	private String readonly;

	private String attachments;// 自定义的标签标示附件

	public void setProperties(List<WebProperty> properties) {
		this.properties = properties;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public void init() {
		this.properties = null;
		this.language = null;
		this.model = null;
		this.readonly = "";
		this.attachments="false";
	}

	public void release() {
		super.release();
	}

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public int doEndTag() throws JspException {
		if (CollectionUtils.isNotEmpty(this.properties)) {
			try {
				JspWriter out = this.pageContext.getOut();
				out.println("<tr>");
				if ("staff".equals(this.model)) {
					out.println("<td class=\"categorySet-head\"><div class=\"categorySet-body border-top\">");
				} else {
					out.println("<td width=\"50%\">");
				}
				out.println("<table width=\"80%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">");
				out.println("<tr><td class=\"bg-gray\"><div class=\"hr-blue\"><strong>");
				if ("en".equals(this.language)) {
					out.println(((WebProperty) this.properties.get(0)).getPageName_en());
				} else {
					out.println(Functions.toHTML(((WebProperty) this.properties.get(0)).getPageName_zh()));
				}
				out.println("&nbsp;&nbsp;&nbsp;&nbsp;</strong></div></td><td>&nbsp;</td><td>&nbsp;</td></tr>");
				for (WebProperty webProperty : this.properties) {
					Long repositoryId = webProperty.getRepository_id();
					out.println(
							"<tr><td class=\"bg-gray\" width=\"25%\" nowrap=\"nowrap\"><input type=\"hidden\" name=\""
									+ repositoryId + "_Type" + "\" value=\"" + webProperty.getPropertyType() + "\" />");
					String propertyName = "";
					if ("en".equals(this.language)) {
						propertyName = webProperty.getLabelName_en();
					} else {
						propertyName = Functions.toHTML(webProperty.getLabelName_zh());
					}
					out.println("<label>" + propertyName + ":</label></td>");
					out.println("<td class=\"new-column\" width=\"50%\">");
					if (webProperty.getPropertyType() == 1) {
						String value = webProperty.getF1() != null ? String.valueOf(webProperty.getF1()) : "";
						out.print("<input type=\"text\" class=\"input-100per\" name=\"" + repositoryId + "\" value=\""
								+ value + "\" inputName=\"" + propertyName + "\" " + this.readonly + " ");
						if ("no".equals(webProperty.getNot_null())) {
							out.println("validate=\"notNull,isInteger,maxLength\" maxSize=\"10\" />");
						} else {
							out.println("validate=\"isInteger,maxLength\" maxSize=\"10\" />");
						}
					} else if (webProperty.getPropertyType() == 2) {
						String value = webProperty.getF2() != null ? String.valueOf(webProperty.getF2().doubleValue())
								: "";
						out.print("<input type=\"text\" class=\"input-100per\" name=\"" + repositoryId + "\" value=\""
								+ value + "\" inputName=\"" + propertyName + "\" " + this.readonly + " ");
						if ("no".equals(webProperty.getNot_null())) {
							out.println(
									"validate=\"notNull,isNumber,maxLength\" decimalDigits=\"2\" maxSize=\"10\" />");
						} else {
							out.println("validate=\"isNumber,maxLength\" decimalDigits=\"2\" maxSize=\"10\" />");
						}
					} else if (webProperty.getPropertyType() == 3) {
						String value = webProperty.getF3() != null ? String.valueOf(webProperty.getF3()) : "";
						out.println("<input type=\"text\" class=\"input-100per\" name=\"" + repositoryId + "\" value=\""
								+ value + "\" inputName=\"" + propertyName + "\" " + this.readonly
								+ " readonly=\"true\" onclick=\"whenstart('/seeyon',this,675,640);\" />");
					} else if (webProperty.getPropertyType() == 4) {
						String value = webProperty.getF4() != null ? String.valueOf(webProperty.getF4()) : "";
						out.print("<input type=\"text\" class=\"input-100per\" name=\"" + repositoryId + "\" value=\""
								+ Functions.toHTML(value) + "\" inputName=\"" + propertyName + "\" " + this.readonly
								+ " ");
						if ("no".equals(webProperty.getNot_null())) {
							out.println("validate=\"notNull,maxLength\" maxSize=\"40\" />");
						} else {
							out.println("validate=\"maxLength\" maxSize=\"40\" />");
						}
					} else {
						String value = webProperty.getF5() != null ? String.valueOf(webProperty.getF5()) : "";
						out.print("<input type=\"text\" class=\"input-100per\" name=\"" + repositoryId + "\" value=\""
								+ Functions.toHTML(value) + "\" inputName=\"" + propertyName + "\" " + this.readonly
								+ " ");
						if ("no".equals(webProperty.getNot_null())) {
							out.println("validate=\"notNull,maxLength\" maxSize=\"40\" />");
						} else {
							out.println("validate=\"maxLength\" maxSize=\"40\" />");
						}
					}
					out.println("</td><td>&nbsp;</td></tr>");
				}
				// 此处判断是否显示附件控件
				if (this.attachments != null && this.attachments.toLowerCase().equals("true")) {
					//out.println("<tr><td colspan=\"3\"><div class=\"div-float\">(<span id=\"attachmentNumberDiv\"></span>附件)</div><v3x:fileUpload attachments=\"${attachments}\" canDeleteOriginalAtts=\"false\" /></td></tr>");
					out.println(super.getBodyContent().getString());
				}
				out.println("</table>");
				if ("staff".equals(this.model)) {
					out.println("</div></td>");
				} else {
					out.println("</td>");
				}
				out.println("</tr>");
			} catch (IOException e) {
				throw new JspTagException(e.toString(), e);
			}
		}
		init();
		return super.doEndTag();
	}
}
