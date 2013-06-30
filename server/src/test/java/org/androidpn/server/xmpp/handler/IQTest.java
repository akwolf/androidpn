package org.androidpn.server.xmpp.handler;

import java.util.Random;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.jtester.testng.JTester;
import org.testng.annotations.Test;
import org.xmpp.packet.IQ;

/**
 * @author zhangh
 * @createTime 2013-6-29 下午8:18:04
 */
@Test
public class IQTest extends JTester {

	public void testAuth() {
		String NAMESPACE = "jabber:iq:auth";
		Element probeResponse = DocumentHelper.createElement(QName.get("query", NAMESPACE));
		probeResponse.addElement("username");
		probeResponse.addElement("password");
		probeResponse.addElement("digest");

		System.out.println(probeResponse.asXML());
	}

	public void testRegister() {
		String NAMESPACE = "jabber:iq:register";
		Element probeResponse = DocumentHelper.createElement(QName.get("query", NAMESPACE));
		probeResponse.addElement("username");
		probeResponse.addElement("password");
		probeResponse.addElement("email");
		probeResponse.addElement("name");
		System.out.println(probeResponse.asXML());
	}

	public void testIQ() {
		String NOTIFICATION_NAMESPACE = "androidpn:iq:notification";
		Random random = new Random();
		String id = Integer.toHexString(random.nextInt());

		Element notification = DocumentHelper.createElement(QName.get("notification", NOTIFICATION_NAMESPACE));
		notification.addElement("id").setText(id);
		notification.addElement("apiKey").setText("123456");
		notification.addElement("title").setText("title");
		notification.addElement("message").setText("message");
		notification.addElement("uri").setText("uri");

		//<notification xmlns="androidpn:iq:notification"><id>79388f01</id><apiKey>123456</apiKey><title>title</title><message>message</message><uri>uri</uri></notification>
		System.out.println("notification : " + notification.asXML());
		IQ iq = new IQ();
		iq.setType(IQ.Type.set);
		iq.setChildElement(notification);
		//		<iq type="set" id="33-0">
		//		  <notification xmlns="androidpn:iq:notification">
		//		    <id>79388f01</id>
		//		    <apiKey>123456</apiKey>
		//		    <title>title</title>
		//		    <message>message</message>
		//		    <uri>uri</uri>
		//		  </notification>
		//		</iq>		
		System.out.println("iq : " + iq);
		
		//<notification xmlns="androidpn:iq:notification"><id>79388f01</id><apiKey>123456</apiKey><title>title</title><message>message</message><uri>uri</uri></notification>
		System.out.println("iq childElement : "+iq.getChildElement().asXML());
		
		//androidpn:iq:notification
		System.out.println("iq namespace uri : "+iq.getChildElement().getNamespaceURI());
		
	}

}
