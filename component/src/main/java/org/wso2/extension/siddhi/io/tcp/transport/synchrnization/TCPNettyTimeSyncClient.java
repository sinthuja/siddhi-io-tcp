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
package org.wso2.extension.siddhi.io.tcp.transport.synchrnization;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.tcp.transport.synchrnization.request.TimeSyncCompleteRequest;
import org.wso2.extension.siddhi.io.tcp.transport.synchrnization.request.TimeSyncInitRequest;
import org.wso2.extension.siddhi.io.tcp.transport.synchrnization.request.TimeSyncOutboundHandler;
import org.wso2.extension.siddhi.io.tcp.transport.synchrnization.response.TimeSyncInboundHandler;
import org.wso2.extension.siddhi.io.tcp.transport.synchrnization.response.TimeSyncResponse;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;

/**
 * This is the TCP client which provides the time syncing functionality.
 */
public class TCPNettyTimeSyncClient {
    private static final Logger log = Logger.getLogger(TCPNettyTimeSyncClient.class);

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private String hostAndPort;
    private Channel channel;
    private TimeSyncInboundHandler timeSyncInboundHandler;
    private TimeSyncOutboundHandler timeSyncOutboundHandler;

    public TCPNettyTimeSyncClient(boolean keepAlive, boolean noDelay) {
        this(0, keepAlive, noDelay);
    }

    public TCPNettyTimeSyncClient() {
        this(0, true, true);
    }

    public TCPNettyTimeSyncClient(int numberOfThreads, boolean keepAlive, boolean noDelay) {
        group = new NioEventLoopGroup(numberOfThreads);
        bootstrap = new Bootstrap();
        timeSyncInboundHandler = new TimeSyncInboundHandler();
        timeSyncOutboundHandler = new TimeSyncOutboundHandler();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, keepAlive)
                .option(ChannelOption.TCP_NODELAY, noDelay)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addFirst(timeSyncOutboundHandler);
                        pipeline.addLast(timeSyncInboundHandler);
                    }
                });
    }

    public void connect(String host, int port) throws ConnectionUnavailableException {
        // Start the connection attempt.
        try {
            hostAndPort = host + ":" + port;
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (Throwable e) {
            throw new ConnectionUnavailableException("Error connecting to '" + hostAndPort + "', "
                    + e.getMessage(), e);
        }
    }

    public synchronized boolean sendTimeSyncRequest(final String sourceId) {
        TimeSyncInitRequest timeSyncInitRequest = new TimeSyncInitRequest(sourceId, System.currentTimeMillis());
        this.timeSyncInboundHandler.waitingForResponse();
        ChannelFuture cf = channel.writeAndFlush(timeSyncInitRequest);
        try {
            cf.sync();
            if (cf.isSuccess()) {
                long currentTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - currentTime < 60000 &&
                        this.timeSyncInboundHandler.isWaitingForResponse()) {
                    Thread.sleep(1);
                }
                if (!this.timeSyncInboundHandler.isWaitingForResponse()) {
                    TimeSyncResponse timeSyncResponse = this.timeSyncInboundHandler.getTimeSyncResponse();
                    if (timeSyncResponse != null) {
                        TimeSyncCompleteRequest timeSyncCompleteRequest = new TimeSyncCompleteRequest(sourceId,
                                timeSyncResponse.getRequestSendTime(), timeSyncResponse.getRequestReceiveTime(),
                                timeSyncResponse.getResponseSendTime(), timeSyncResponse.getResponseReceiveTime());
                        ChannelFuture cf2 = channel.writeAndFlush(timeSyncCompleteRequest);
                        cf2.sync();
                        return cf2.isSuccess();
                    }
                }
            }
            return false;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void disconnect() {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("Error closing connection to '" + hostAndPort + "' from client '" + e);
            }
            channel.disconnect();
            log.info("Disconnecting client to '" + hostAndPort);
        }
    }

    public void shutdown() {
        disconnect();
        if (group != null) {
            group.shutdownGracefully();
        }
        log.info("Stopping client to '" + hostAndPort);
        hostAndPort = null;
    }
}
