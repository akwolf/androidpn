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
package org.androidpn.server.xmpp.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.androidpn.server.xmpp.handler.IQAuthHandler;
import org.androidpn.server.xmpp.handler.IQHandler;
import org.androidpn.server.xmpp.handler.IQRegisterHandler;
import org.androidpn.server.xmpp.handler.IQRosterHandler;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

/**
 * This class is to route IQ packets to their corresponding handler.
 * 
 * 对IQ进行路由，交付对应的处理器(handler)
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
@Component
@Scope("prototype")
public class IQRouter {

	private final Log log = LogFactory.getLog(getClass());

	private SessionManager sessionManager;

	/** iqHandler列表 */
	private List<IQHandler> iqHandlers = new ArrayList<IQHandler>();

	/** namespace与IQHandler缓存器 */
	private Map<String, IQHandler> namespace2Handlers = new ConcurrentHashMap<String, IQHandler>();

	@Autowired
	private IQAuthHandler iqAuthHandler;

	@Autowired
	private IQRegisterHandler iqRegisterHandler;

	@Autowired
	private IQRosterHandler iqRosterHandler;

	/**
	 * Constucts a packet router registering new IQ handlers.
	 */
	public IQRouter() {
		sessionManager = SessionManager.getInstance();
		// iqHandlers.add(new IQAuthHandler());
		// iqHandlers.add(new IQRegisterHandler());
		// iqHandlers.add(new IQRosterHandler());
		
		iqHandlers.add(iqAuthHandler);
		iqHandlers.add(iqRegisterHandler);
		iqHandlers.add(iqRosterHandler);
	}

	/**
	 * Routes the IQ packet based on its namespace.
	 * 
	 * @param packet
	 *            the packet to route
	 */
	public void route(IQ packet) {
		if (packet == null) {
			throw new NullPointerException();
		}
		// 取得发送者的jid
		JID sender = packet.getFrom();
		// 根据jid查询对应的clientSession
		ClientSession session = sessionManager.getSession(sender);

		if (session == null
				|| session.getStatus() == Session.STATUS_AUTHENTICATED
				|| ("jabber:iq:auth".equals(packet.getChildElement()
						.getNamespaceURI())
						|| "jabber:iq:register".equals(packet.getChildElement()
								.getNamespaceURI()) || "urn:ietf:params:xml:ns:xmpp-bind"
							.equals(packet.getChildElement().getNamespaceURI()))) {
			// 处理IQ包
			handle(packet);
		} else {
			// 创建错误的IQ返回信息包
			IQ reply = IQ.createResultIQ(packet);
			reply.setChildElement(packet.getChildElement().createCopy());
			reply.setError(PacketError.Condition.not_authorized);
			session.process(reply);
		}
	}

	private void handle(IQ packet) {
		try {
			// 取得<iq>元素中的内容
			// namespace不在["",jabber:client,jabber:server]的元素
			Element childElement = packet.getChildElement();
			String namespace = null;
			if (childElement != null) {
				// 取得子元素的namespace
				namespace = childElement.getNamespaceURI();
			}
			// 返回类型的IQ
			if (namespace == null) {
				// 返回类型的IQ类型不为result,error为未知包
				if (packet.getType() != IQ.Type.result
						&& packet.getType() != IQ.Type.error) {
					log.warn("Unknown packet " + packet);
				}
			} else {
				// 取得消息处理器
				IQHandler handler = getHandler(namespace);
				// 该消息对应的处理器(Handler)不存在
				if (handler == null) {
					sendErrorPacket(packet,
							PacketError.Condition.service_unavailable);
				} else {
					// 消息处理器进行消息处理
					handler.process(packet);
				}
			}

		} catch (Exception e) {
			log.error("Could not route packet", e);
			Session session = sessionManager.getSession(packet.getFrom());
			if (session != null) {
				IQ reply = IQ.createResultIQ(packet);
				reply.setError(PacketError.Condition.internal_server_error);
				session.process(reply);
			}
		}
	}

	/**
	 * Send the error packet to the original sender
	 * 
	 * 将错误消息返回给发送者
	 * 
	 */
	private void sendErrorPacket(IQ originalPacket,
			PacketError.Condition condition) {
		if (IQ.Type.error == originalPacket.getType()) {
			log.error("Cannot reply an IQ error to another IQ error: "
					+ originalPacket);
			return;
		}
		IQ reply = IQ.createResultIQ(originalPacket);
		reply.setChildElement(originalPacket.getChildElement().createCopy());
		reply.setError(condition);
		try {
			PacketDeliverer.deliver(reply);
		} catch (Exception e) {
			// Ignore
		}
	}

	/**
	 * Adds a new IQHandler to the list of registered handler.
	 * 
	 * 添加一个IQHandler到List中若已经添加过则跑出异常
	 * 
	 * @param handler
	 *            the IQHandler
	 */
	public void addHandler(IQHandler handler) {
		if (iqHandlers.contains(handler)) {
			throw new IllegalArgumentException(
					"IQHandler already provided by the server");
		}
		namespace2Handlers.put(handler.getNamespace(), handler);
	}

	/**
	 * Removes an IQHandler from the list of registered handler.
	 * 
	 * 清除IQHander，包括[List,Map]
	 * 
	 * @param handler
	 *            the IQHandler
	 */
	public void removeHandler(IQHandler handler) {
		if (iqHandlers.contains(handler)) {
			throw new IllegalArgumentException(
					"Cannot remove an IQHandler provided by the server");
		}
		namespace2Handlers.remove(handler.getNamespace());
	}

	/**
	 * Returns an IQHandler with the given namespace.
	 * 
	 * 根据namespace取得对应的Handler
	 */
	private IQHandler getHandler(String namespace) {
		// 根据namespace获取IQHandler
		IQHandler handler = namespace2Handlers.get(namespace);
		// 如果 IQHandler缓存中未找到，则从iqHandlers(List)中进行对比匹配，并添加到IQHandler缓存器中
		// IQHandler在此处进行初始化
		if (handler == null) {
			for (IQHandler handlerCandidate : iqHandlers) {
				if (namespace.equalsIgnoreCase(handlerCandidate.getNamespace())) {
					handler = handlerCandidate;
					namespace2Handlers.put(namespace, handler);
					break;
				}
			}
		}
		return handler;
	}

}
