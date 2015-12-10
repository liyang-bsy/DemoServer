package net.lylab.vicp.web.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import net.lylab.vicp.web.utils.Page;
import net.vicp.lylab.core.NonCloneableBaseObject;
import net.vicp.lylab.core.interfaces.Executor;
import net.vicp.lylab.core.model.Message;
import net.vicp.lylab.mybatis.MultiSourcesSession;

public abstract class BaseAction extends NonCloneableBaseObject implements
		Runnable, Executor {

	private HttpServletRequest request;
	private HttpServletResponse response;

	// private SqlSession session;
	// private Map<String, SqlSession> sessions;
	private MultiSourcesSession session;
	private Page page = null;

	protected Message input;
	protected Message output;
	protected Integer code;
	protected String retMsg;
	protected boolean rollback = false;

	protected static Map<String, Object> toMap(Object obj) {
		return toMap(obj, new String[] {});
	}

	public static void main(String[] arg) {
		// String json =
		// "{\"body\":{\"total\":1439798967000,\"list\":[{\"body_id\":1,\"body_name\":\"头部\",\"body_path\":\"0,1,\",\"body_spell\":\"toubu\"}]},\"header\":{\"ret_result\":{\"ret_code\":0,\"ret_msg\":\"成功\"},\"token_id\":null},\"key\":\"LoadBodyList\"}";
		// MallMessageSend mms = new
		// JSONDeserializer<MallMessageSend>().use(null, MallMessageSend.class)
		// .deserialize(json);
		// Map<String, Object> m = convert(mms.getBody());
		// for(String key : m.keySet())
		// System.out.println(m.get(key).getClass().getSimpleName());
		// ConsultLog cl = new ConsultLog();
		// cl.setComment_time(new Date());
		// MallMessageSend mms = new MallMessageSend();
		// mms.getBody().put("fdsa", cl);
		// System.out.println(new JSONSerializer().transform(new
		// DateTransformer("yyyy-MM-dd HH:mm:ss"),
		// "body.fdsa.comment_time").exclude("*.class").deepSerialize(mms));
		// System.out.println(new JSONSerializer().transform(new
		// DateTransformer("yyyy-MM-dd HH:mm:ss"),
		// "*.comment_time").exclude("*.class").deepSerialize(mms));
		// System.out.println(new JSONSerializer().transform(new
		// DateTransformer("yyyy-MM-dd HH:mm:ss"),
		// "comment_time").exclude("*.class").deepSerialize(mms));
		// List<HashMap<Object, Object>> list=(List<HashMap<Object, Object>>)
		// new
		// JSONDeserializer().deserialize("[{\"get_key\":\"isCity\",\"get_value\":\"true\"},{\"get_key\":\"xxxCity\",\"get_value\":\"xxxx\"}]");
		// for (HashMap<Object, Object> map:list){
		// System.out.println(map.get("get_key"));
		// }
		// System.out.println(list);
	}

	class Param {
		String get_key;
		String get_value;

		public String getGet_key() {
			return get_key;
		}

		public void setGet_key(String get_key) {
			this.get_key = get_key;
		}

		public String getGet_value() {
			return get_value;
		}

		public void setGet_value(String get_value) {
			this.get_value = get_value;
		}
	}

	protected static Map<String, Object> toMap(Object obj, String... exclude) {
		String json = new JSONSerializer().exclude("*.class").exclude(exclude)
				.deepSerialize(obj);
		return new JSONDeserializer<Map<String, Object>>().use(null,
				HashMap.class).deserialize(json);
	}

	protected static Map<String, Object> convertAllValuesToString(
			Map<String, Object> map) {
		return convert(map);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> convert(Map<String, Object> map) {
		for (String key : map.keySet()) {
			Object obj = map.get(key);
			System.out.println("process:" + key + "\tvalue:" + obj.toString());
			if (obj instanceof List) {
				map.put(key, convert((List<Object>) obj));
				continue;
			} else if (obj instanceof Map) {
				map.put(key, convert((Map<String, Object>) obj));
				continue;
			} else
				map.put(key, String.valueOf(obj == null ? "" : obj));
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private static List<Object> convert(List<Object> list) {
		List<Object> destList = new ArrayList<Object>();
		for (Object obj : list) {
			System.out.println("process:" + "list" + "\tvalue:"
					+ obj.toString());
			if (obj instanceof List) {
				destList.add(convert((List<Object>) obj));
				continue;
			} else if (obj instanceof Map) {
				destList.add(convert((Map<String, Object>) obj));
				continue;
			} else
				destList.add(String.valueOf(obj == null ? "" : obj));
		}
		return destList;
	}

	protected static Object toObject(String json, Class<?> targetClass) {
		return new JSONDeserializer<Object>().use(null, targetClass)
				.deserialize(json);
	}

	/**
	 * 在此实现你需要的mapper
	 */
	public abstract void createMapper();

	/**
	 * 在此判断参数的完整性，返回null代表参数完整，否则返回的字符串将交给前台告知该字段缺失
	 */
	public abstract String checkIntegrity();

	/**
	 * 在此实现你需要的业务逻辑
	 */
	@Override
	public abstract void exec();

	public void run() {
		createMapper();
		String missingParam = checkIntegrity();
		if (missingParam != null) {
			code = 0x00003;
			retMsg = "JSON部分缺省:" + missingParam;
			return;
		}
		exec();
	}

	/**
	 * 当解析分页失败时，会创建默认的分页[1/20]给前台,Body中为Long类型
	 * 
	 * @return
	 */
	public synchronized Page getPage() {
		if (page == null) {
			Long page_no = Long.valueOf(input.getBody().get("page_no").toString());
			Long page_size = Long.valueOf(input.getBody().get("page_size").toString());
			int pageNo = 0, pageSize = 0;
			try {
				pageNo = page_no.intValue();
				pageSize = page_size.intValue();
				page = new Page(pageNo, pageSize);
			} catch (Exception e) {
				page = new Page();
			}
		}
		return page;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public Message getInput() {
		return input;
	}

	public void setInput(Message input) {
		this.input = input;
	}

	public Message getOutput() {
		return output;
	}

	public void setOutput(Message output) {
		this.output = output;
	}

	protected SqlSession getSession(String env) {
		return session.get(env);
	}

	/**
	 * 请勿多次提供sql连接，以免疏忽处理没关掉连接
	 * 
	 * @param session
	 */
	public synchronized void setSession(MultiSourcesSession session) {
		if (this.session == null)
			this.session = session;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	public boolean isRollback() {
		return rollback;
	}

}
