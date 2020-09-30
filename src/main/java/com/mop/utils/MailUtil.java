package com.mop.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.mop.entity.FieldCount;
import com.mop.entity.MailNotice;
import com.mop.entity.TestReport;
import org.apache.log4j.Logger;

public class MailUtil {

	private final static Logger log = Logger.getLogger(MailUtil.class);
	
	public static StringBuffer html_Subject(FieldCount fc, String mailName, TestReport testreport, Map<String,Integer> isReturnCount, Map<String,Integer> networkCount)
	{
		StringBuffer body = new StringBuffer();
		body.append("【日志数据测试报告-").append(mailName).append("-通过率").append(testreport.getSuccessPercent()).append("%】");
		body.append("<div style=\"font-family: 微软雅黑; font-weight: bold; font-size:14px; \">测试用例总数为：" + testreport.getTestcaseNum() + "条</div><br>");
		body.append("<div style=\"font-family: 微软雅黑; font-weight: bold; font-size:14px; color:green; \">成功用例为：" + testreport.getSuccessCount() + "条，通过率：" + String.format("%.2f", testreport.getSuccessPercent()) + "%</div><br>");
		body.append("<div style=\"font-family: 微软雅黑; font-weight: bold; font-size:14px; color:red;\">失败用例为：" + testreport.getFailCount() + "条，失败率：" + String.format("%.2f", testreport.getFailPercent()) + "%</div><br>");

		if (isReturnCount != null)
		{
			//isReturn分布情况
			double isReturnPercent = 0;
			body.append("<table border='1' style=\"text-align:center\">");
			body.append("<tr><td>isReturn分布情况</td><td>出现次数</td><td>出现占比</td></tr>");
			for (Map.Entry<String,Integer> entry : isReturnCount.entrySet())
			{
				body.append("<tr>");
				body.append("<td>").append(entry.getKey()).append("</td>");
				body.append("<td>").append(entry.getValue()).append("</td>");
				isReturnPercent = (double)entry.getValue() / (double)testreport.getTestcaseNum() * 100;
				body.append("<td>").append(String.format("%.2f", isReturnPercent)).append("%</td>");
				body.append("</tr>");
			}
			body.append("</table>");
		}
		if (networkCount != null)
		{
			//network各值分布情况
			double networkPercent = 0;
			body.append("<table border='1' style=\"text-align:center\">");
			body.append("<tr><td>network分布情况</td><td>出现次数</td><td>出现占比</td></tr>");
			for (Map.Entry<String,Integer> entry : networkCount.entrySet())
			{
				body.append("<tr>");
				body.append("<td>" + entry.getKey() + "</td>");
				body.append("<td>" + entry.getValue() + "</td>");
				networkPercent = (double)entry.getValue() / (double)testreport.getTestcaseNum() * 100;
				body.append("<td>" + String.format("%.2f",networkPercent) + "%</td>");
				body.append("</tr>");
			}
			body.append("</table>");
		}
		//(详细的测试结果)表头
		body.append("<table border='1' style=\"text-align:center\">");
		body.append("<tr>");
		String title[] = {"字段名","通过总数","通过占比","失败总数","失败占比","出现null总数","出现null占比"};
		for(int index = 0;index < title.length;index++)
		{
			body.append("<td>" + title[index] + "</td>");
		}
		body.append("</tr>");
		
		//字段列
		for(int i = 0;i < fc.getTablehead().length;i++)
		{
			body.append("<tr>");
			body.append("<td>" + fc.getTablehead()[i] + "</td>");
			body.append("<td style=\"color:green;\">" + fc.getSuccessCount()[i] + "</td>");
			body.append("<td style=\"color:green;\">" + String.format("%.2f", fc.getSuccessPercent()[i]) + "%</td>");
			body.append("<td style=\"color:red;\">" + fc.getFailCount()[i] + "</td>");
			body.append("<td style=\"color:red;\">" + String.format("%.2f", fc.getFailPercent()[i]) + "%</td>");
			body.append("<td style=\"color:#EE7621;\">" + fc.getNullCount()[i] + "</td>");
			body.append("<td style=\"color:#EE7621;\">" + String.format("%.2f", fc.getNullPercent()[i]) + "%</td>");
			body.append("</tr>");
		}
		body.append("</table>");
		return body;
	}

	public static StringBuffer notice(MailNotice mailNotice){
		StringBuffer sb = new StringBuffer("");
		String font_head = "<div style=\"font-family:微软雅黑;font-weight:bold;font-size:14px;\">";
		String font_end = "</div></br>";
		sb.append(font_head).append("【本次").append(mailNotice.getAppTypeId()).append("日志测试详情结果，共").append(mailNotice.getLogSum()).append("条】").append(font_end);
		sb.append(font_head).append("<font color=\"green\">").append("【通过日志：").append(mailNotice.getPassSum()).append("条】").append("</font>").append(font_end);
		sb.append(font_head).append("<font color=\"red\">").append("【失败日志：").append(mailNotice.getFailSum()).append("条】").append("</font>").append(font_end);
		sb.append(font_head).append("<font color=\"#EE7621\">").append("【无数据日志：").append(mailNotice.getErrorLogName().size()).append("条】").append("</font>").append(font_end);
		//通过日志
		sb.append("<table border='1' style='text-align:center;'>");
		sb.append("<tr><td>").append("日志名称").append("</td><td>").append("测试情况").append("</td></tr>");
		for (String logName : mailNotice.getPassLog())
		{
			sb.append("<tr>");
			sb.append("<td>").append(logName).append("</td>");
			sb.append("<td style=\"color:green;\">").append("PASS").append("</td>");
			sb.append("</tr>");
		}
		//失败日志
		for (String logName : mailNotice.getFailLog())
		{
			sb.append("<tr>");
			sb.append("<td>").append(logName).append("</td>");
			sb.append("<td style=\"color:red;\">").append("FAIL").append("</td>");
			sb.append("</tr>");
		}
		//无数据日志
		for (int index = 0,length = mailNotice.getErrorLogName().size();index < length;index++)
		{
			sb.append("<tr>");
			sb.append("<td>").append(mailNotice.getErrorLogName().get(index)).append("</td>");
			sb.append("<td style=\"color:#EE7621;\">").append(mailNotice.getReason().get(index)).append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb;
	}

	
	/**
     * 发送邮件
     * @author yinye
     * @param to
     * 		收件人，可以是邮件组
     * @param subject
     * 		邮件标题
     * @param body
     * 		邮件内容
     * @param cc
     * 		抄送人
	 * @param attachment
	 * 		附件
     */
    public static void sendMail(String to,String subject,String body,String cc,File attachment)
    {
    	Properties props = new Properties();
        //props.setProperty("mail.debug", "true");
        props.setProperty("mail.smtp.auth", "true");
        //使用smtp的话，默认为SSL协议
        //props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");//使用ssl协议
        props.setProperty("mail.transport.protocol", "smtp");
        Session session = Session.getInstance(props);
        Message msg = new MimeMessage(session);
        try {
        	//msg.setText(body);
        	//设置邮件
        	msg.setSubject(subject);
			msg.setFrom(new InternetAddress("shareinstall@021.com"));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
			
			BodyPart bp=new MimeBodyPart();
			bp.setContent(body, "text/html;charset=gb2312");
			Multipart mp=new MimeMultipart();
			
			//附件
			BodyPart attachmentBodyPart = null;
            // 添加附件的内容
            if (null != attachment) 
            {
            	attachmentBodyPart = new MimeBodyPart();
            	DataSource source = new FileDataSource(attachment);
            	attachmentBodyPart.setDataHandler(new DataHandler(source));
            	//MimeUtility.encodeWord可以避免文件名乱码
                try {
					attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
                mp.addBodyPart(attachmentBodyPart);
            }
			
			mp.addBodyPart(bp);
			msg.setContent(mp);
			msg.saveChanges();
			
			Transport transport = session.getTransport();
			transport.connect("smtp.exmail.qq.com", 25,"shareinstall@021.com", "HMgHYZduU2TmYejC");
			
			//此方式发送邮件只显示内容
			//transport.sendMessage(msg, new Address[] {new InternetAddress("xxxx@youdomain.com")});
			
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
   

}