/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*
*/
package org.wso2.extension.siddhi.io.tcp.transport.synchrnization.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.nio.charset.StandardCharsets;

/**
 * This is the Outbound handler which transmits the time sync requests to the server.
 */
public class TimeSyncOutboundHandler extends ChannelOutboundHandlerAdapter {

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        TimeSyncInitRequest request = (TimeSyncInitRequest) msg;
        ByteBuf encoded = ctx.alloc().buffer(request.getTotalBytes());

        encoded.writeByte(request.getProtocol());
        encoded.writeInt(request.getMessageType().getBytes(StandardCharsets.UTF_8).length);
        encoded.writeBytes(request.getMessageType().getBytes(StandardCharsets.UTF_8));
        encoded.writeInt(request.getSourceId().getBytes(StandardCharsets.UTF_8).length);
        encoded.writeBytes(request.getSourceId().getBytes(StandardCharsets.UTF_8));
        encoded.writeLong(request.getRequestSendTime());

        if (msg instanceof TimeSyncCompleteRequest) {
            TimeSyncCompleteRequest completeRequest = (TimeSyncCompleteRequest) msg;
            encoded.writeLong(completeRequest.getRequestReceiveTime());
            encoded.writeLong(completeRequest.getResponseSendTime());
            encoded.writeLong(completeRequest.getResponseReceiveTime());
        }
        ctx.writeAndFlush(encoded, promise);
    }

}
