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

import org.androidpn.server.xmpp.handler.PresenceUpdateHandler;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;

/** 
 * This class is to route Presence packets to their corresponding handler.
 * 
 * 路由Presence包到相应的处理器(handler)
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
//@Component
//@Scope("prototype")
public class PresenceRouter {

    private final Log log = LogFactory.getLog(getClass());

    private SessionManager sessionManager;

    /** 在线状态更新处理器 */
//    @Autowired
    private PresenceUpdateHandler presenceUpdateHandler;

    /**
     * Constucts a packet router.
     */
    public PresenceRouter() {
        sessionManager = SessionManager.getInstance();
        presenceUpdateHandler = new PresenceUpdateHandler();
    }

    /**
     * Routes the Presence packet.
     * 
     * @param packet the packet to route
     */
    public void route(Presence packet) {
        if (packet == null) {
            throw new NullPointerException();
        }
        // 根据发送包的jid查询建立连接的会话(session)
        ClientSession session = sessionManager.getSession(packet.getFrom());

        // 当没有建立连接且状态为非连接状态，进行Presence包处理
        if (session == null || session.getStatus() != Session.STATUS_CONNECTED) {
            handle(packet);
        } else {
        	// 返回未授权错误
            packet.setTo(session.getAddress());
            packet.setFrom((JID) null);
            packet.setError(PacketError.Condition.not_authorized);
            session.process(packet);
        }
    }

    private void handle(Presence packet) {
        try {
        	// 取得信息包类型
            Presence.Type type = packet.getType();
            // Presence updates (null == 'available')
            if (type == null || Presence.Type.unavailable == type) {
                presenceUpdateHandler.process(packet);
            } else {
                log.warn("Unknown presence type");
            }

        } catch (Exception e) {
            log.error("Could not route packet", e);
            // 出现异常关闭会话
            Session session = sessionManager.getSession(packet.getFrom());
            if (session != null) {
                session.close();
            }
        }
    }

}
