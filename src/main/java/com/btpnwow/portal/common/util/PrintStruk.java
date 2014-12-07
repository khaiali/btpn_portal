package com.btpnwow.portal.common.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;

public class PrintStruk extends HttpServlet {
	
private static final long serialVersionUID = 1L;
	
	
	protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String idReport = new Utils().getParameterMap().get("pan").toString() ;
		System.out.println("----- idreport " + idReport);
		String source = "";
		
		switch (Integer.parseInt(idReport) ) {
		case 1 :
			source =  "D:\\PLN\\Templet Report JASPER\\struklandscape_prepaid.jrxml";
			break;
		case 2 :
			source =  "D:\\PLN\\Templet Report JASPER\\struklandscape_postpaid.jrxml";
			break;
		case 3 :
			source =  "D:\\PLN\\Templet Report JASPER\\struklandscape_nontaglis.jrxml";
			break;
		default:
			break;
		}

		try {

            JREmptyDataSource  withoutdb = new JREmptyDataSource();

            JasperReport jasperReport = JasperCompileManager.compileReport(source);
            byte bytes[] = JasperRunManager.runReportToPdf(jasperReport, new Utils().getParameterMap(), withoutdb);
            response.setContentType("application/pdf");
            response.setContentLength(bytes.length);
            ServletOutputStream outStream = response.getOutputStream();
            outStream.write(bytes, 0, bytes.length);
            outStream.flush();
            new Utils().getParameterMap().clear();
            outStream.close();
            
		}  catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}