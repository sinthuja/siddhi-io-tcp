/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.extension.siddhi.io.tcp.transport.utils;

/**
 * Constants for tcp transport.
 */
public final class Constant {

    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final int DEFAULT_RECEIVER_THREADS = 10;
    public static final int DEFAULT_WORKER_THREADS = 10;
    public static final int DEFAULT_PORT = 9892;
    public static final String DEFAULT_HOST = "0.0.0.0";
    public static final boolean DEFAULT_TCP_NO_DELAY = true;
    public static final boolean DEFAULT_KEEP_ALIVE = true;

    public static final int DEFAULT_TIME_SYNC_PORT = 7452;
    public static final String TIME_SYNC_INIT = "T_SYN_IT";
    public static final String TIME_SYNC_DONE = "T_SYN_DE";
    public static final byte SUCCESS_RESPONSE = 0x20;
    public static final byte FAILURE_RESPONSE = 0X50;
    public static final byte PROTOCOL_VERSION = 0X01;

    private Constant() {

    }
}
