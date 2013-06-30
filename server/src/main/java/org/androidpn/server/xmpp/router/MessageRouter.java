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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xmpp.packet.Message;

/** 
 * This class is to route Message packets to their corresponding handler.
 * 
 * 将message包路由到对应的处理器(handler)上
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
@Component
@Scope("prototype")
public class MessageRouter {

    /**
     * Constucts a packet router.
     */
    public MessageRouter() {
    }

    /**
     * Routes the Message packet.
     * 
     * @param packet the packet to route
     */
    public void route(Message packet) {
        throw new RuntimeException("Please implement this!");
    }

}
