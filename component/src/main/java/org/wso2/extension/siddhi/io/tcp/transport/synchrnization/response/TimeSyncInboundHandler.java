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
package org.wso2.extension.siddhi.io.tcp.transport.synchrnization.response;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.tcp.transport.utils.Constant;

import java.io.UnsupportedEncodingException;

/**
 * This is the Inbout Handler adapter to process the response that was sent from the server.
 */
public class TimeSyncInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = Logger.getLogger(TimeSyncInboundHandler.class);
    private TimeSyncResponse timeSyncResponse;
    private boolean waitingForResponse;

    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        if (in.readableBytes() < 5) {
            return;
        }
        byte protocol = in.readByte();
        if (protocol != Constant.PROTOCOL_VERSION) {
            ReferenceCountUtil.release(msg);
            return;
        }
        long responseReceiveTime = System.currentTimeMillis();
        byte responseCode = in.readByte();
        if (responseCode == Constant.SUCCESS_RESPONSE) {
            try {
                int sourceIdSize = in.readInt();
                String sourceId = getString(in, sourceIdSize);
                long requestSendTime = in.readLong();
                long requestReceiveTime = in.readLong();
                long responseSendTime = in.readLong();
                this.timeSyncResponse = new TimeSyncResponse(sourceId, requestSendTime, requestReceiveTime,
                        responseSendTime, responseReceiveTime);
                this.waitingForResponse = false;
            } catch (UnsupportedEncodingException e) {
                log.error("Unable to decode the request");
            }
        }
        ReferenceCountUtil.release(msg);
    }

    public void waitingForResponse() {
        this.waitingForResponse = true;
    }

    public boolean isWaitingForResponse() {
        return waitingForResponse;
    }

    public synchronized TimeSyncResponse getTimeSyncResponse() {
        TimeSyncResponse responseHolder = this.timeSyncResponse;
        this.timeSyncResponse = null;
        return responseHolder;
    }

    private static String getString(ByteBuf byteBuffer, int size) throws UnsupportedEncodingException {
        byte[] bytes = new byte[size];
        byteBuffer.readBytes(bytes);
        return new String(bytes, Constant.DEFAULT_CHARSET);
    }

}
