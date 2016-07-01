/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.symphony;

import org.symphonyoss.symphony.clients.AgentClient;
import org.symphonyoss.symphony.clients.AuthorizationClient;
import org.symphonyoss.symphony.clients.ServiceClient;
import org.symphonyoss.symphony.model.SymAuth;
import org.symphonyoss.symphony.service.model.User;
import org.symphonyoss.symphony.services.*;

/**
 * Created by Frank Tarsillo on 5/14/2016.
 */

public interface SymphonyClient {

    SymAuth getSymAuth();

    void setSymAuth(SymAuth symAuth);

    MessageService getMessageService();

    PresenceService getPresenceService();

    ConversationService getConversationService();

    AgentClient getAgentClient();

    ServiceClient getServiceClient();

    boolean init(SymAuth symAuth, String email, String agentUrl, String serviceUrl) throws Exception;

    User getLocalUser();



}
