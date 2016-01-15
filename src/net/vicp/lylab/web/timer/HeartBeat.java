package net.vicp.lylab.web.timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import flexjson.JSONSerializer;
import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.core.model.Message;
import net.vicp.lylab.utils.timer.TimerJob;
import net.vicp.lylab.web.utils.HttpUtils;

public class HeartBeat extends TimerJob {
	
	private String serverName;
	
	@Override
	public Date getStartTime() {
		Calendar cl = Calendar.getInstance();
		cl.add(Calendar.SECOND, 30);
		return cl.getTime();
	}

	@Override
	public Integer getInterval() {
		return 1*MINUTE;
	}

	@Override
	public void exec() {
		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Message toServer = new Message();
		toServer.setKey("Heartbeat");
		toServer.getBody().put("lastTime", now);
		HttpUtils.sendPost(
				CoreDef.config.getString("urlMonitor")
				, new JSONSerializer().exclude("*.class").deepSerialize(toServer));
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

}
