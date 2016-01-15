package net.lylab.vicp.web.action.runtime;

import java.util.Date;
import java.util.Map;

import net.lylab.vicp.web.core.BaseAction;
import net.vicp.lylab.utils.Utils;

public class CheckRuntime extends BaseAction {

	@Override
	public void createMapper() {
	}

	@Override
	public String checkIntegrity() {
		return null;
	}
	
	@Override
	public void exec() {
		Map<String, Object> sbody = input.getBody();
		try{ do {
			sbody.put("当前时间", Utils.format(new Date(), "yyyy-MM-dd HH-mm:ss"));
			
			code = 0;
		} while(false); } catch (Exception e) { e.printStackTrace(); }

	}

}
