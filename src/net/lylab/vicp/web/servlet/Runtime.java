package net.lylab.vicp.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import net.vicp.lylab.core.Singleton;
import net.vicp.lylab.utils.cache.LYCache;

/**
 * 后台服务运行状态
 */
@WebServlet("/runtime")
public class Runtime extends HttpServlet {
	private static final long serialVersionUID = 3200296505570525661L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unused")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		StringBuilder content = new StringBuilder(1024);
		String cmdResult = "";
		// 命令模式
		if(!StringUtils.isBlank(request.getParameter("cmdMode")))
		{
			String cmd = request.getParameter("cmdMode");
			if(false);
			else if(cmd.equals("clearCache"))
			{
				((LYCache) Singleton.get("LYCache")).clear();
				cmdResult = "ok";
			}
		}
		content.append("<!DOCTYPE html>");
		content.append("<html>");
		content.append("<meta charset=\"UTF-8\">");
		content.append("<body>");
		content.append("当前时间：\t\t" + System.currentTimeMillis() + "<br/>");
//		content.append("访问者IP_模式1：\t\t" + Utils.getIpAddr(request) + "<br/>");
//		content.append("访问者IP_模式2：\t\t" + request.getRemoteAddr() + "<br/>");

		if(!StringUtils.isBlank(cmdResult))
			content.append("命令结果:<br/>" + cmdResult + "<br/>");
		
		content.append("\n");
		content.append("</body>");
		content.append("</html>");
		response.getOutputStream().write(content.toString().getBytes("UTF-8"));
	}

}
