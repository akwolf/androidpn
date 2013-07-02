package org.dom4j.io;

import java.io.File;
import java.net.URL;

import org.dom4j.Document;
import org.jtester.testng.JTester;
import org.testng.annotations.Test;

/**
 * @author zhangh
 * @createTime 2013-7-1 上午10:34:02
 */
@Test
public class Dom4jTest extends JTester {

	public void testSystemID() throws Exception {
		File file = new File("D:\\NInfo.plist");
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		System.out.println(doc.asXML());

		URL url = new URL("http://www.apple.com/DTDs/PropertyList-1.0.dtd");
	}
}
