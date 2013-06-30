package org.androidpn.server.service;

import org.androidpn.server.model.User;
import org.jtester.annotations.SpringApplicationContext;
import org.jtester.annotations.SpringBeanByType;
import org.jtester.testng.JTester;
import org.testng.annotations.Test;

/**
 * @author zhangh
 * @createTime 2013-6-28 下午9:28:35
 */
@SpringApplicationContext({ "spring-config.xml" })
@Test
public class UserServiceTest extends JTester {

	@SpringBeanByType
	UserService userService;

//	public void testSaveUser() throws UserExistsException {
//		User user = new User();
//		user.setEmail("zhangh@justsy.com");
//		user.setName("zhangh");
//		user.setPassword("123456");
//		user.setUsername("zhangh");
//		User c = userService.saveUser(user);
//		System.out.println(c + ",id=" + user.getId());
//		want.number(c.getId()).isGt(0L);
//	}
}
