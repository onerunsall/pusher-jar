package push.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class Initiator {

	Constant constant;

	public Initiator(String environment, String client) throws UnsupportedEncodingException, IOException {
		constant = new Constant();
		constant.client = client;
		constant.environment = environment;
		if (!"prod".equals(environment) && !"test".equals(environment) && !"dev".equals(environment))
			throw new RuntimeException("environment有误");

		InputStream in = Initiator.class.getClassLoader()
				.getResourceAsStream("push_launcher/mainConfig_" + constant.environment + ".properties");
		Properties properties = new Properties();
		properties.load(new InputStreamReader(in, constant.CHARSET));
		in.close();

		constant.messageUrl = properties.getProperty("messageUrl");
		constant.notifyAllUrl = properties.getProperty("notifyAllUrl");
		constant.notifyUrl = properties.getProperty("notifyUrl");
	}
}
