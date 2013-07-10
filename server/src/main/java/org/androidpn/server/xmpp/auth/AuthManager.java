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
package org.androidpn.server.xmpp.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.UnauthenticatedException;
import org.androidpn.server.xmpp.XmppServer;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * This class is to provide the methods associated with user authentication. 
 * 
 * 提供用户认证的方法
 * 
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class AuthManager {

	private static final Log log = LogFactory.getLog(AuthManager.class);

	// 同步锁对象
	private static final Object DIGEST_LOCK = new Object();

	private static MessageDigest digest;

	static {
		try {
			digest = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			log.error("Internal server error", e);
		}
	}

	/**
	 * Returns the user's password. 
	 * 通过用户名查询用户密码
	 * 
	 * @param username the username
	 * @return the user's password
	 * @throws UserNotFoundException if the your was not found
	 */
	public static String getPassword(String username) throws UserNotFoundException {

		return ((UserService) XmppServer.getInstance().getBean("userService")).getUserByUsername(username)
				.getPassword();
	}

	/**
	 * Authenticates a user with a username and plain text password, and
	 * returns an AuthToken.
	 * 
	 * 通过普通文本密码和用户名返回认证Token
	 * 
	 * @param username the username
	 * @param password the password
	 * @return an AuthToken
	 * @throws UnauthenticatedException if the username and password do not match
	 */
	public static AuthToken authenticate(String username, String password) throws UnauthenticatedException {
		if (username == null || password == null) {
			throw new UnauthenticatedException();
		}
		username = username.trim().toLowerCase();
		// zhangh@gmail.com包含@符号
		if (username.contains("@")) {
			int index = username.indexOf("@");
			// 截取gmail.com域名
			String domain = username.substring(index + 1);
			// 传入的和xmpp的域不一致
			if (domain.equals(Config.getString("xmpp.domain", "127.0.0.1").toLowerCase())) {
				username = username.substring(0, index);
			} else {
				throw new UnauthenticatedException();
			}
		}
		try {
			// 传入的密码和当前用户名的密码不一致
			if (!password.equals(getPassword(username))) {
				throw new UnauthenticatedException();
			}
		} catch (UserNotFoundException unfe) {
			throw new UnauthenticatedException();
		}
		return new AuthToken(username);
	}

	/**
	 * Authenticates a user with a username, token, and digest, and returns
	 * an AuthToken.
	 * 
	 * 通过指定加密算法加密的密码和用户名返回认证Token
	 * 
	 * @param username the username
	 * @param token the token
	 * @param digest the digest
	 * @return an AuthToken
	 * @throws UnauthenticatedException if the username and password do not match 
	 */
	public static AuthToken authenticate(String username, String token, String digest) throws UnauthenticatedException {
		if (username == null || token == null || digest == null) {
			throw new UnauthenticatedException();
		}
		username = username.trim().toLowerCase();
		if (username.contains("@")) {
			int index = username.indexOf("@");
			String domain = username.substring(index + 1);
			if (domain.equals(Config.getString("xmpp.domain", "127.0.0.1").toLowerCase())) {
				username = username.substring(0, index);
			} else {
				throw new UnauthenticatedException();
			}
		}
		try {
			String password = getPassword(username);
			String anticipatedDigest = createDigest(token, password);

			if (!digest.equalsIgnoreCase(anticipatedDigest)) {
				throw new UnauthenticatedException();
			}
		} catch (UserNotFoundException unfe) {
			throw new UnauthenticatedException();
		}
		return new AuthToken(username);
	}

	/**
	 * Returns true if plain text password authentication is supported according to JEP-0078.
	 * 
	 * @return true if plain text password authentication is supported
	 */
	public static boolean isPlainSupported() {
		return true;
	}

	/**
	 * Returns true if digest authentication is supported according to JEP-0078.
	 * 
	 * @return true if digest authentication is supported
	 */
	public static boolean isDigestSupported() {
		return true;
	}

	private static String createDigest(String token, String password) {
		synchronized (DIGEST_LOCK) {
			digest.update(token.getBytes());
			return Hex.encodeHexString(digest.digest(password.getBytes()));
		}
	}

}
