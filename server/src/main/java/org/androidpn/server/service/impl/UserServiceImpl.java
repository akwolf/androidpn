/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.service.impl;

import java.util.List;

import org.androidpn.server.dao.UserMapper;
import org.androidpn.server.model.User;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 
 * This class is the implementation of UserService.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
@Service("userService")
public class UserServiceImpl implements UserService {

	protected final Log log = LogFactory.getLog(getClass());

	
	@Autowired
	private UserMapper userDao;
	
	/**
	 * 
	 */
	public UserServiceImpl() {
		System.out.println("--------------------");
	}

	public void setUserDao(UserMapper userDao) {
		this.userDao = userDao;
	}

	public User getUser(String userId) {
		return userDao.getUser(new Long(userId));
	}

	public List<User> getUsers() {
		return userDao.getUsers();
	}

	public User saveUser(User user) throws UserExistsException {
		userDao.saveUser(user);
		return user;

	}

	public User getUserByUsername(String username) throws UserNotFoundException {
		return userDao.getUserByUsername(username);
	}

	public boolean removeUser(Long userId) {
		log.debug("removing user: " + userId);
		return userDao.removeUser(userId) > 0 ? true : false;
	}
}
