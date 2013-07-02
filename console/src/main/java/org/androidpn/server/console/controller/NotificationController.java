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
package org.androidpn.server.console.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.push.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/** 
 * A controller class to process the notification related requests.  
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
@Controller
@RequestMapping("/notification.do")
public class NotificationController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private NotificationManager notificationManager;

	public NotificationController() {
		//		notificationManager = new NotificationManager();
	}

	@RequestMapping("/list")
	public ModelAndView list() throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("notification/form");
		return mav;
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public ModelAndView send(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String broadcast = ServletRequestUtils.getStringParameter(request, "broadcast", "Y");
		String username = ServletRequestUtils.getStringParameter(request, "username");
		String title = ServletRequestUtils.getStringParameter(request, "title");
		String message = ServletRequestUtils.getStringParameter(request, "message");
		String uri = ServletRequestUtils.getStringParameter(request, "uri");

		String apiKey = Config.getString("apiKey", "");
		logger.debug("apiKey=" + apiKey);

		if (broadcast.equalsIgnoreCase("Y")) {
			notificationManager.sendBroadcast(apiKey, title, message, uri);
		} else {
			notificationManager.sendNotifcationToUser(apiKey, username, title, message, uri);
		}
		System.out.println("send!!");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:notification.do");
		return mav;
	}

}
