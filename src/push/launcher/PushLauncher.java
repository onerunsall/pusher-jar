package push.launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	boolean prod = false;
	boolean test = false;
	boolean dev = true;

	public boolean isProd() {
		return prod;
	}

	public void setProd(boolean prod) {
		this.prod = prod;
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public boolean isDev() {
		return dev;
	}

	public void setDev(boolean dev) {
		this.dev = dev;
	}

	public void registerJiGuang(String masterSecret, String appKey) {
		jPushClient = new JPushClient(masterSecret, appKey);
	}

	public void sendNotificationByAlias(String[] alias, String notification)
			throws APIConnectionException, APIRequestException {
		sendNotificationByAlias(Arrays.asList(alias), notification);
	}

	public void sendNotificationByTag(String[] tags, String notification)
			throws APIConnectionException, APIRequestException {
		sendNotificationByTag(Arrays.asList(tags), notification);
	}

	public void sendNotificationByTag(List<String> tags, String notification)
			throws APIConnectionException, APIRequestException {
		jPushClient.sendPush(PushPayload.newBuilder().setNotification(Notification.alert(notification))
				.setPlatform(Platform.all()).setAudience(Audience.tag(tags))
				.setOptions(Options.newBuilder().setApnsProduction(prod).build()).build());
	}

	public void sendNotificationByAlias(List<String> alias, String notification)
			throws APIConnectionException, APIRequestException {
		jPushClient.sendPush(PushPayload.newBuilder().setNotification(Notification.alert(notification))
				.setPlatform(Platform.all()).setAudience(Audience.alias(alias))
				.setOptions(Options.newBuilder().setApnsProduction(prod).build()).build());
	}

	public void sendNotificationAll(String notification) throws APIConnectionException, APIRequestException {
		jPushClient.sendNotificationAll(notification);
	}

	public List<String> getTags() throws APIConnectionException, APIRequestException {
		List tags = jPushClient.getTagList().tags;
		return tags == null ? new ArrayList<String>() : tags;
	}

	public boolean tagExist(String tag) throws APIConnectionException, APIRequestException {
		List<String> tags = getTags();
		return tags.contains(tag);
	}
}
