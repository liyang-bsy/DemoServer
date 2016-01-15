package net.lylab.vicp.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import net.lylab.vicp.web.core.BaseAction;
import net.lylab.vicp.web.utils.Logger;
import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.core.model.Message;
import net.vicp.lylab.mybatis.MultiSourcesSession;
import net.vicp.lylab.mybatis.SQLSessionFactory;
import net.vicp.lylab.utils.Config;
import net.vicp.lylab.utils.Utils;
import net.vicp.lylab.utils.tq.Task;

@WebServlet("/interface")
public class KeyDispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	static private Config config = CoreDef.config.getConfig("action");
	protected transient static Log log = LogFactory.getLog(Task.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 设置字符集
		request.setCharacterEncoding("UTF-8");
		// 初始化数据
		Message input = null;
		Message output = new Message();
		// 这两个字符串的目标其实是保留原始数据的引用
		String before = readData(request), after = "";
		String key = null;
		try { do {
			// JSON解析
			try {
				input = new JSONDeserializer<Message>().use(null, Message.class).deserialize(before);
			} catch (Exception e) {
				output.setCode(0x00002);
				output.setMessage("JSON解析错误");
				break;
			}
			if (input == null) {
				output.setCode(0x00002);
				output.setMessage("JSON解析错误");
				break;
			}
			// 拿key
			key = input.getKey();
			if(StringUtils.isBlank(key)) {
				output.setCode(0x00003);
				output.setMessage("JSON部分缺省");
				break;
			}
			// 用key
			output.setKey(key);
			// 用key 找action
			BaseAction action = null;
			try {
				action = (BaseAction) config.getNewInstance(key + "Action");
			} catch (Exception e) { }
			if(action == null) {
				output.setCode(0x00004);
				output.setMessage("接口未定义");
				break;
			}
			//校验Token
			output.setToken(input.getToken());
			
			// 初始化action
			action.setRequest(request);
			action.setResponse(response);
			action.setCode(0xFFFFF);
			action.setRetMsg("未知错误");
			action.setInput(input);
			action.setOutput(output);

			// 分配给这个action与数据库交互的多源session
			try (MultiSourcesSession session = new MultiSourcesSession((SQLSessionFactory)
					CoreDef.config.getConfig("Singleton").getObject("SQLSessionFactory"))) {
					try {
					action.setSession(session);
					action.run();
					// 提交事务 或 回滚事务
					if(action.isRollback())
						session.rollback();
					else
						session.commit();
				} catch (Throwable t) {
					output.setCode(0x0000c);
					output.setMessage("后台程序处理接口出错");
					// 如果exec执行失败，则报告错误并回滚事务
					log.error(Utils.getStringFromThrowable(t));
					session.rollback();
					break;
				}
			} catch (Exception e) {
				output.setCode(0x00005);
				output.setMessage("数据库忙，链接数据库失败");
				break;
			}
			// 收回返回值
			output = action.getOutput();
			if (action.getCode() != null)
				output.setCode(action.getCode());
			if (action.getCode() == 0)
				output.setMessage("ok");
			else if(action.getRetMsg() != null)
				output.setMessage(action.getRetMsg());
		} while (false); } catch (Throwable t) {
			// 任何情况下发生异常，那就报告错误
			log.error(Utils.getStringFromThrowable(t));
		}
		
		// 解析返回值
		after = new JSONSerializer().exclude("*.class", "*.id").deepSerialize(output);
		// 发给前台
		response.setContentType("text/html;charset=UTF-8");
		response.getOutputStream().write(after.getBytes(CoreDef.CHARSET()));
		response.getOutputStream().close();
		
		// May be you want to log here
		((Logger) CoreDef.config.getConfig("Singleton").getObject("Logger")).appendLine("Access key:" + key
				+ CoreDef.LINE_SEPARATOR + "Before:" + request + CoreDef.LINE_SEPARATOR + "After:" + response);
	}

	public String readData(HttpServletRequest request) throws IOException {
		StringBuilder str = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line = "";
		while ((line = reader.readLine()) != null) {
			str.append(line);
		}
		return str.toString();
	}

}
