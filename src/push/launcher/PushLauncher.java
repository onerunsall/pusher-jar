package push.launcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

/**
 * @ClassName: ApiDemo
 * @Description: TODO
 *
 */
public class PushLauncher {
	private static Logger logger = Logger.getLogger(PushLauncher.class);

	JPushClient jPushClient = null;

	public void registerJiGuang(String masterSecret, String appKey) {
		jPushClient = new JPushClient(masterSecret, appKey);
	}

	public void sendNotification(String[] alias, String mobile, String notification)
			throws APIConnectionException, APIRequestException {
		sendNotification(Arrays.asList(alias), mobile, notification);
	}

	public void sendNotification(List<String> alias, String mobile, String notification)
			throws APIConnectionException, APIRequestException {
		jPushClient.sendPush(PushPayload.newBuilder().setNotification(Notification.alert(notification))
				.setPlatform(Platform.all()).setAudience(Audience.alias(alias))
				.setOptions(Options.newBuilder().setApnsProduction(true).build()).build());
	}

}
