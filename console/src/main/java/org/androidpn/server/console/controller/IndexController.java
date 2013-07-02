package org.androidpn.server.console.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zhangh
 * @createTime 2013-6-29 下午4:41:13
 */
@Controller
@RequestMapping("/index.do")
public class IndexController {
	@RequestMapping
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("index");
		return mav;
	}
}
