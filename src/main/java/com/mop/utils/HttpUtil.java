package com.mop.utils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;

public class HttpUtil {

	private final static Logger log = Logger.getLogger(HttpUtil.class);

	public static String easyHttp_get(String url) throws ParseException, IOException
	{
		String string = "";
		//创建httpClient请求对象
		CloseableHttpClient  httpClient = HttpClients.createDefault();
		//声明get请求
		HttpGet httpGet = new HttpGet(url);
		//设置配置请求参数
		RequestConfig requestConfig = RequestConfig.custom()
				//连接服务器时间
				.setConnectTimeout(35000)
				//请求超时时间15分钟
				.setConnectionRequestTimeout(900000)
				//获取数据时间
				.setSocketTimeout(900000)
				.build();
		//为httpGet配置实例
		httpGet.setConfig(requestConfig);
		//发送请求
		CloseableHttpResponse response = httpClient.execute(httpGet);
		//判断状态码
		if(response.getStatusLine().getStatusCode()==200)
		{
			HttpEntity entity = response.getEntity();
			//使用工具类EntityUtils，从响应中取出实体表示的内容并转换成字符串
			string = EntityUtils.toString(entity, "utf-8");
		}
		//5.关闭资源
		response.close();
		httpClient.close();
		return string;
	}
	
	/**
	 * 测试日志接口
	 * @return 得到序列化后的json字符串
	 */
	public static String getData(String dataBase,String tableName,int pageSize,String starttime,String endtime,String os,String apptypeId,String appver)
	{
		try{
			String url = "http://172.31.64.223:8097/gainLogData?dataBase="
					+ dataBase + "&tableName=" + tableName + "&pagesize="
					+ pageSize + "&startDateline="+ starttime +"&endDateline=" + endtime
					+ "&os=" + os + "&apptypeId=" + apptypeId + "&appver=" + appver;
			log.info("请求接口：" + url);
			JSONArray jsonArray = JSONArray.parseArray(easyHttp_get(url));
			if (jsonArray == null)
			{
				log.info("");
				return "400";
			}
			if (jsonArray.size() == 1)
			{
				log.info("数据接口无数据");
				return "100";
			}
			return JSONArray.toJSONString(jsonArray, SerializerFeature.WriteMapNullValue);
		}catch(NullPointerException e){
			e.printStackTrace();
			log.info("数据接口无数据");
			return "100";
		}catch (ConnectException e) {
			e.printStackTrace();
			log.info("数据接口无响应");
			return "200";
		}catch (SocketTimeoutException e){
			e.printStackTrace();
			log.info("数据接口请求超时");
			return "300";
		}catch (IOException e){
			e.printStackTrace();
			log.info("ERROR:请及时排查！");
			return "400";
		}
	}

	/**
	 * 线上日志接口
	 * @return 得到序列化后的json字符串
	 */
	public static String getData_Prod(String dataBase,String tableName,int pageSize,String startdt,String enddt,String os,String apptypeid,String appver)
	{
		try{
			String url = "http://172.31.64.223:8097/gainLogData?dataBase=" + dataBase +
					"&tableName=" + tableName +
					"&pagesize=" + pageSize +
					"&startDt=" + startdt +
					"&endDt=" + enddt +
					"&os=" + os +
					"&apptypeId=" + apptypeid +
					"&appver=" + appver;
			log.info("请求接口：" + url);
			JSONArray jsonArray = JSONArray.parseArray(easyHttp_get(url));
			if (jsonArray.size() == 1)
			{
				log.info("数据接口无数据");
				return "100";
			}
			return JSONArray.toJSONString(jsonArray, SerializerFeature.WriteMapNullValue);
		}catch(NullPointerException e){
			e.printStackTrace();
			log.info("数据接口无数据");
			return "100";
		}catch (ConnectException e) {
			e.printStackTrace();
			log.info("数据接口无响应");
			return "200";
		}catch (SocketTimeoutException e){
			e.printStackTrace();
			log.info("数据接口请求超时");
			return "300";
		}catch (IOException e){
			e.printStackTrace();
			log.info("ERROR:请及时排查！");
			return "400";
		}
	}

}