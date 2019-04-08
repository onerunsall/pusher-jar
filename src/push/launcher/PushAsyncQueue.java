package push.launcher;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.giveup.SplitUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PushAsyncQueue implements Runnable {

	private Initiator initiator;
	private Constant constant;

	private OkHttpClient okHttpClient = new OkHttpClient().newBuilder().readTimeout(1, TimeUnit.MILLISECONDS).build();

	private static Logger logger = Logger.getLogger(PushAsyncQueue.class);

	private static BlockingQueue<Payload> queue = new LinkedBlockingDeque<Payload>();

	public PushAsyncQueue(Initiator initiator) {
		this.initiator = initiator;
		this.constant = initiator.constant;
	}

	public static void notify(String title, String content, Map<String, String> extras, String... aliases) {
		Payload payload = new Payload();
		payload.aliases = aliases;
		payload.content = content;
		payload.title = title;
		payload.extras = extras;
		payload.type = "notify";
		try {
			queue.put(payload);
		} catch (InterruptedException e) {
			logger.info(ExceptionUtils.getStackTrace(e));
		}
	}

	public static void notifyAll(String title, String content, Map<String, String> extras) {
		Payload payload = new Payload();
		payload.content = content;
		payload.title = title;
		payload.extras = extras;
		payload.type = "notifyAll";
		try {
			queue.put(payload);
		} catch (InterruptedException e) {
			logger.info(ExceptionUtils.getStackTrace(e));
		}
	}

	public static void message(String title, String content, Map<String, String> extras, String[] aliases) {
		Payload payload = new Payload();
		payload.aliases = aliases;
		payload.title = title;
		payload.content = content;
		payload.extras = extras;
		payload.type = "message";
		try {
			queue.put(payload);
		} catch (InterruptedException e) {
			logger.info(ExceptionUtils.getStackTrace(e));
		}
	}

	public static class Payload {
		private String[] aliases;
		private String content;
		private String title;
		private String type;
		private Map<String, String> extras;
	}

	@Override
	public void run() {
		while (true) {
			Payload payload = null;
			try {
				payload = queue.take();
				logger.debug("发现新的推送任务");
				payload.extras = payload.extras == null ? new HashMap() : payload.extras;
				if (payload.type.equals("notify")) {
					String url = new StringBuilder(constant.notifyUrl).append("?client=").append(constant.client)
							.append("&aliases=").append(SplitUtils.toSplit(payload.aliases, ",", true))
							.append("&content=").append(payload.content).toString();
					Request okHttpRequest = new Request.Builder().url(url).build();
					logger.debug("call out api：" + url);
					Response okHttpResponse = okHttpClient.newCall(okHttpRequest).execute();
					okHttpResponse.close();

				} else if (payload.type.equals("notifyAll")) {
					String url = new StringBuilder(constant.notifyAllUrl).append("?client=").append(constant.client)
							.append("&content=").append(payload.content).toString();
					Request okHttpRequest = new Request.Builder().url(url).build();
					logger.debug("call out api：" + url);
					Response okHttpResponse = okHttpClient.newCall(okHttpRequest).execute();
					okHttpResponse.close();
				} else if (payload.type.equals("message")) {
					String url = new StringBuilder(constant.messageUrl).append("?client=").append(constant.client)
							.append("&aliases=").append(SplitUtils.toSplit(payload.aliases, ",", true))
							.append("&content=").append(payload.content).toString();
					Request okHttpRequest = new Request.Builder().url(url).build();
					logger.debug("call out api：" + url);
					Response okHttpResponse = okHttpClient.newCall(okHttpRequest).execute();
					okHttpResponse.close();
				}
			} catch (SocketTimeoutException e) {
			} catch (Exception e) {
				logger.info(ExceptionUtils.getStackTrace(e));
			}
		}

	}

}
