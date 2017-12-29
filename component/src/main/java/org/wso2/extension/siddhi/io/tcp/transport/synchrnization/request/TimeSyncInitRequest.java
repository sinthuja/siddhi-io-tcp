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

import org.wso2.extension.siddhi.io.tcp.transport.utils.Constant;

import java.nio.charset.StandardCharsets;

/**
 * This is the POJO which holds the basic time sync request properties.
 */
public class TimeSyncInitRequest {
    private byte protocol;
    protected String messageType;
    private String sourceId;
    private long requestSendTime;

    public TimeSyncInitRequest(byte protocol, String sourceId, long requestSendTime) {
        this.protocol = protocol;
        this.messageType = Constant.TIME_SYNC_INIT;
        this.sourceId = sourceId;
        this.requestSendTime = requestSendTime;
    }

    public TimeSyncInitRequest(String sourceId, long requestSendTime) {
        this(Constant.PROTOCOL_VERSION, sourceId, requestSendTime);
    }

    public String getMessageType() {
        return messageType;
    }

    public byte getProtocol() {
        return protocol;
    }


    public String getSourceId() {
        return sourceId;
    }

    public long getRequestSendTime() {
        return requestSendTime;
    }

    public int getTotalBytes() {
        int size = 1; //protocol
        size = size + 4 + messageType.getBytes(StandardCharsets.UTF_8).length;
        // messageType size as int(4) and actual messageType value.
        size = size + 4 + sourceId.getBytes(StandardCharsets.UTF_8).length;
        size = size + 8;
        return size;
    }
}
