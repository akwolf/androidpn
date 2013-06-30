package org.androidpn.server.dao;

import org.androidpn.server.model.User;
import org.jtester.annotations.SpringApplicationContext;
import org.jtester.annotations.SpringBeanByType;
import org.jtester.testng.JTester;
import org.testng.annotations.Test;

/**
 * @author zhangh
 * @createTime 2013-6-28 下午5:58:26
 */
@SpringApplicationContext({ "spring-config.xml" })
@Test
public class UserMapperTest extends JTester {

	@SpringBeanByType
	UserMapper userMapper;

	@Test(description = "插入用户")
	public void testSaveUser() {
		User user = new User();
		user.setEmail("zhangh@justsy.com");
		user.setName("zhangh");
		user.setPassword("123456");
		user.setUsername("zhangh");
		long c = userMapper.saveUser(user);
		System.out.println(user.getId());
		want.number(c).isGt(0L);
	}
}
