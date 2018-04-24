package com.seeyon.v3x.hr.taglibs;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.hr.webmodel.WebProperty;
import java.io.IOException;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.collections.CollectionUtils;

public class SalaryAddTaglib extends BodyTagSupport {
	private static final long serialVersionUID = 2029996302794886937L;
	public static final String TAG_NAME = "salaryAddTag";
	private List<WebProperty> properties;
	private String language;
	private String model;
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


	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public void release() {
		super.release();
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
					Long propertyId = webProperty.getProperty_id();
					out.println(
							"<tr><td class=\"bg-gray\" width=\"25%\" nowrap=\"nowrap\"><input type=\"hidden\" name=\""
									+ propertyId + "_pageId" + "\" value=\"" + webProperty.getPage_id() + "\" />");
					String propertyName = "";
					if ("en".equals(this.language)) {
						propertyName = webProperty.getLabelName_en();
					} else {
						propertyName = Functions.toHTML(webProperty.getLabelName_zh());
					}
					out.println("<label>" + propertyName + ":</label></td>");
					out.println("<td class=\"new-column\" width=\"50%\">");
					out.print("<input type=\"text\" name=\"" + propertyId + "\" value=\"\" inputName=\"" + propertyName
							+ "\" validate=\"");
					if ("no".equals(webProperty.getNot_null())) {
						out.print("notNull");
					}
					if (webProperty.getPropertyType() == 1) {
						if ("no".equals(webProperty.getNot_null())) {
							out.print(",");
						}
						out.println("isInteger,maxLength\" class=\"input-100per\" maxSize=\"10\" />");
					} else if (webProperty.getPropertyType() == 2) {
						if ("no".equals(webProperty.getNot_null())) {
							out.print(",");
						}
						out.println(
								"isNumber,maxLength\" decimalDigits=\"2\" class=\"input-100per\" maxSize=\"10\" />");
					} else if (webProperty.getPropertyType() == 3) {
						out.println(
								"\" class=\"cursor-hand input-100per\" readonly=\"true\" onclick=\"whenstart('/seeyon',this,675,640);\" />");
					} else {
						if ("no".equals(webProperty.getNot_null())) {
							out.print(",");
						}
						out.println("maxLength\" class=\"input-100per\" maxSize=\"40\" />");
					}
					out.println("</td><td>&nbsp;</td></tr>");
				}
				// 此处判断是否显示附件控件 这里不支持jsp标签需要重新考虑
				if (this.attachments != null && this.attachments.toLowerCase().equals("true")) {
					//out.println("<tr><td colspan=\"3\"><input type=\"button\" onclick=\"insertAttachment();\" value=\"附件\" class=\"button-default-2\"><div class=\"div-float\">(<span id=\"attachmentNumberDiv\"></span>个)</div><v3x:fileUpload attachments=\"${attachments}\" canDeleteOriginalAtts=\"true\" /></td></tr>");
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
		return super.doEndTag();
	}
}
