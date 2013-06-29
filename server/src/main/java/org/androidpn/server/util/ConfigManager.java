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
package org.androidpn.server.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * This class is to manage the application configruation.
 * 配置文件管理器，读取配置文件
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ConfigManager {

	private static final Log log = LogFactory.getLog(ConfigManager.class);

	private static Configuration config;

	private static ConfigManager instance;

	private ConfigManager() {
		// 默认读取config.xml配置文件
		loadConfig();
	}

	/**
	 * Returns the singleton instance of ConfigManger.
	 * 
	 * @return the instance
	 */
	public static ConfigManager getInstance() {
		if (instance == null) {
			synchronized (ConfigManager.class) {
				instance = new ConfigManager();
			}
		}
		return instance;
	}

	/**
	 * Loads the default configuration file.
	 * 从config.xml中读取配置信息
	 * 
	 */
	public void loadConfig() {
		loadConfig("config.xml");
	}

	/**
	 * Loads the specific configuration file.
	 * 读取指定文件名的配置信息
	 * 
	 * @param configFileName the file name
	 */
	public void loadConfig(String configFileName) {
		try {
			//			ConfigurationFactory factory = new ConfigurationFactory(configFileName);
			config = new XMLConfiguration(configFileName);
			//			config = factory.getConfiguration();
			log.info("Configuration loaded: " + configFileName);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RuntimeException("Configuration loading error: " + configFileName, ex);
		}
	}

	/**
	 * Returns the loaded configuration object.
	 * 返回有配置信息的配置实例
	 * 
	 * @return the configuration
	 */
	public Configuration getConfig() {
		return config;
	}

}
