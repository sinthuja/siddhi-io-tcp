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


/**
 * This is the POJO class which holds the finalized time sync request properties that was sent from the server.
 */
public class TimeSyncCompleteRequest extends TimeSyncInitRequest {
    private long requestReceiveTime;
    private long responseSendTime;
    private long responseReceiveTime;

    public TimeSyncCompleteRequest(byte protocol, String sourceId, long requestSendTime, long requestReceiveTime,
                                   long responseSendTime, long responseReceiveTime) {
        super(protocol, sourceId, requestSendTime);
        this.messageType = Constant.TIME_SYNC_DONE;
        this.requestReceiveTime = requestReceiveTime;
        this.responseSendTime = responseSendTime;
        this.responseReceiveTime = responseReceiveTime;
    }


    public TimeSyncCompleteRequest(String sourceId, long requestSendTime, long requestReceiveTime,
                                   long responseSendTime, long responseReceiveTime) {
        this(Constant.PROTOCOL_VERSION, sourceId, requestSendTime, requestReceiveTime, responseSendTime,
                responseReceiveTime);
    }

    public long getRequestReceiveTime() {
        return requestReceiveTime;
    }

    public long getResponseSendTime() {
        return responseSendTime;
    }

    public long getResponseReceiveTime() {
        return responseReceiveTime;
    }

    public int getTotalBytes() {
        int size = super.getTotalBytes();
        size = size + (3 * 8); //four long values
        return size;
    }
}
