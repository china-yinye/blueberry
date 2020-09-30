package com.mop.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Field;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;

/**
 * jira工具类
 * 
 * @author yinye
 *
 */
public class JiraUtil {

	/**
	 * 登录jira并返回指定的JiraRestClient对象，调用jira接口必须先登录
	 * 
	 * @param username
	 * @param password
	 * @return 
	 * @throws URISyntaxException
	 */
	public static JiraRestClient login_jira(String username,String password) throws URISyntaxException
	{
		try {
			final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
			final URI jiraServerUri = new URI("http://jira.tt.cn");
			final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, username, password);
			return restClient;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取并返回指定的Issue对象
	 * 
	 * @param issueNum
	 * @param username
	 * @param password
	 * @return
	 * @throws URISyntaxException
	 */
	public static Issue get_issue(String issueNum,String username,String password) throws URISyntaxException
	{
		final JiraRestClient restClient = login_jira(username,password);
		final NullProgressMonitor pm = new NullProgressMonitor();
		final Issue issue = restClient.getIssueClient().getIssue(issueNum, pm);
		return issue;
	}
	
	/**
	 * 创建问题
	 * 
	 * @param username：用户名
	 * @param password：密码
	 * @param projectName：所属项目
	 * @param issueType：问题类型		1L：缺陷		2L：送测
	 * @param description：问题主题
	 * @param summary：问题描述
	 * @param bugType：缺陷类型
	 * @param priority：问题优先级   1L：紧急	2L：重要		3L：一般		4L：次要		5L：无关紧要
	 * @param issueLevel：问题严重程度
	 * @param issueShownStage：缺陷引入阶段
	 * @param issueFindStage：缺陷发现阶段
	 * @param assigneeName：经办人名称（这里为jira用户，也就是erp）
	 * @param attachmentPath_and_attachmentName：附件名称与附件路径
	 * @throws URISyntaxException
	 */
	public static BasicIssue createIssue(
			String username,String password,String projectName,long issueType,String description,String summary
			,String bugType,long priority,String issueLevel,String issueShownStage,String issueFindStage
			,String assigneeName,String attachmentPath_and_attachmentName[]) throws URISyntaxException
	{
		
		//首先登录jira获取用户信息，获取JiraRestClient对象
		final JiraRestClient restClient = login_jira(username, password);
		//设置问题类型
		IssueInputBuilder issueBuilder = new IssueInputBuilder("ProjectKey",issueType);
		//设置所属项目
		final URI projectUri = new URI("http://jira.jd.com/rest/api/2/project/"+projectName);
		BasicProject bproject = new BasicProject(projectUri, projectName, "", (long)10000);
		issueBuilder.setProject(bproject);			//设置项目
		issueBuilder.setDescription(description);	//问题主题
		issueBuilder.setSummary(summary);			//问题描述
		issueBuilder.setPriorityId(priority);		//问题优先级
		issueBuilder.setAssigneeName(assigneeName);	//经办人
		
		//设置自定义字段
		issueBuilder.setFieldValue("customfield_11400", ComplexIssueInputFieldValue.with("value", bugType));//缺陷类型
		issueBuilder.setFieldValue("customfield_11401", ComplexIssueInputFieldValue.with("value", issueLevel));//问题严重程度
		issueBuilder.setFieldValue("customfield_11403", ComplexIssueInputFieldValue.with("value", issueShownStage));//缺陷引入阶段
		issueBuilder.setFieldValue("customfield_11404", ComplexIssueInputFieldValue.with("value", issueFindStage));//缺陷发现阶段
		issueBuilder.setFieldValue("customfield_11405", ComplexIssueInputFieldValue.with("value", "否"));	//开发自测应发现
		
		//创建issue
		IssueInput issueInput = issueBuilder.build();
		NullProgressMonitor pm = new NullProgressMonitor();
		BasicIssue bIssue = restClient.getIssueClient().createIssue(issueInput, pm);
		System.out.println(bIssue.getKey()+"\n"+bIssue.getSelf());
			
		//Jira中上传附件需要先生成BugKey再将这个附件关联到bug---上传附件
		if(attachmentPath_and_attachmentName != null && attachmentPath_and_attachmentName.length > 0)
		{
			URI jiraAttachmentUri = new URI("http://jira.jd.com/rest/api/2/issue/"+bIssue.getKey()+"/attachments");
			for(int i = 0;i < attachmentPath_and_attachmentName.length;i++)
			{
				if(!attachmentPath_and_attachmentName[i].equals(""))
				{	
					String attachmentPath = attachmentPath_and_attachmentName[i];
					String attachmentName = attachmentPath_and_attachmentName[i].split("/")[2];
					File f = new File(attachmentPath);
					if(f.exists() && f.isFile())
					{
						try {
							FileInputStream fis = new FileInputStream(f);
							restClient.getIssueClient().addAttachment(pm, jiraAttachmentUri, fis, attachmentName);
							fis.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return bIssue;
	}
	
	//保存上传的文件
	public static String saveUploadFile(MultipartFile attachment,String path) throws IOException
	{
		if(!attachment.isEmpty())
		{
			try {
				File filepath = new File(path);
				if(!filepath.exists())
				{
					filepath.mkdirs();
				}
				String saveFilePath = path + attachment.getOriginalFilename();
				attachment.transferTo(new File(saveFilePath));
				return saveFilePath;
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 测试函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws URISyntaxException {

		String date = "2020-11-1 10:20:31";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		try {
			d = simpleDateFormat.parse(date);
		}catch (ParseException e){
			e.printStackTrace();
		}
		String date1 = simpleDateFormat.format(d);
		System.out.println(date1);

//		String username = "yinye";
//		String password = "yy13816044103";
//		Issue issue = get_issue("LOGTEST-55", username, password);
//		List<Field> lf = (List<Field>) issue.getFields();
//		for (Field field : lf) {
//			System.out.println(field);
//		}
		//String attachmentPath_and_attachmentName[] = {"",""};
		//createIssue(username,password,"YHDJD",1L,"yinyetest","test","接口问题",1L,"Major（一般缺陷）","测试","功能测试","yinye",attachmentPath_and_attachmentName);
	}
	
}
