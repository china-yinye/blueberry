package com.mop.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.mop.entity.Testcase2;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileUtil {

	private final static Logger log = Logger.getLogger(FileUtil.class);

	//生成excel失败附件
	public static String failedCase_attachment(List<Testcase2> testcase,String tableName){
		try
		{
			String fileName = "";
			XSSFWorkbook workbook = null;
			short color = HSSFColor.HSSFColorPredefined.DARK_RED.getIndex();
			Sheet sheet = null;
			Row row = null;
			Cell cell = null;
			Font font = null;
			CellStyle cellStyle = null;
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String nowTime = dateFormat.format( now );
			fileName = tableName + nowTime + ".xls";
			FileOutputStream fos = null;
			if (!testcase.isEmpty())
			{
				workbook = new XSSFWorkbook();
				sheet= workbook.createSheet();
				String tableHead = testcase.get(0).getFullCase();
				JSONObject table_object = JSONObject.parseObject(tableHead);
				//表头
				int i = 0;
				//行数与单元格下标从0开始
				row = sheet.createRow(0);
				font = workbook.createFont();
				cellStyle = workbook.createCellStyle();
				for (Map.Entry<String,Object> table_entry : table_object.entrySet())
				{
					cell = row.createCell(i);
					cell.setCellValue(table_entry.getKey());
					font.setBold(true);
					font.setColor(color);
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
					i++;
				}
				cell = row.createCell(i);
				//表格的样式
				font.setBold(true);
				font.setColor(color);
				cellStyle.setFont(font);
				cell.setCellStyle(cellStyle);
				cell.setCellValue("出错字段");
				//------------------------隔开--------------------------
				int fullCase_index = 0;
				int testcase_length = testcase.size();
				JSONObject cellObject = null;
				StringBuilder field_str = new StringBuilder("");
				for (int testcase_index = 0;testcase_index < testcase_length;testcase_index++)
				{
					//所有字段的value(第一行是表头，往下跳一行)
					row = sheet.createRow(testcase_index + 1);
					cellObject = JSONObject.parseObject(testcase.get(testcase_index).getFullCase());
					fullCase_index = 0;
					//foreach循环的方式会直到被遍历对象的size = -1的时候停止，迭代遍历无需暴露接口内部就可直接访问具体的值
					for (Map.Entry<String,Object> cell_entry : cellObject.entrySet())
					{
						cell = row.createCell(fullCase_index);
						//为了防止空指针异常没用toString，我也不想try catch XD
						cell.setCellValue(cell_entry.getValue() + "");
						fullCase_index++;
					}
					//出错的字段
					for (String failed_field : testcase.get(testcase_index).getFailColumn())
					{
						field_str.append(failed_field).append(" #### ");
					}
					cell = row.createCell(fullCase_index);
					//清除尾部多余分隔符
					field_str.delete(field_str.length() - 5,field_str.length());
					font.setBold(true);
					font.setColor(color);
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(field_str.toString());
					//清空重复使用
					field_str.delete(0,field_str.length());
				}
				//保存并生成excel文件
				//File file = new File("e:\\" + fileName);
				File file = new File("/data/testfile/" + fileName);
				fos = new FileOutputStream(file);
				workbook.write(fos);
				fos.close();
				return fileName;
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}

	//下载文件
	public static boolean download(HttpServletRequest request, HttpServletResponse response, String filename)
	{
		if (filename != null)
		{
			File file = new File("/data/testfile/" + filename);
			if (file.exists())
			{
				response.setHeader("content-type", "application/octet-stream");
				response.addHeader("Content-Disposition", "attachment;filename=" + filename);// 设置文件名
				byte[] buffer = new byte[1024];
				FileInputStream fis = null;
				BufferedInputStream bis = null;
				try {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					OutputStream os = response.getOutputStream();
					int i = bis.read(buffer);
					while (i != -1) {
						os.write(buffer, 0, i);
						i = bis.read(buffer);
					}
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (bis != null)
					{
						try {
							bis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (fis != null)
					{
						try {
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return true;
	}

}