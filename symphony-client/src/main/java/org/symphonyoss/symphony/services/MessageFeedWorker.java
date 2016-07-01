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

package org.symphonyoss.symphony.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.SymphonyClient;
import org.symphonyoss.symphony.agent.model.Datafeed;
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.agent.model.MessageList;

import java.util.concurrent.TimeUnit;

/**
 * Created by Frank Tarsillo on 5/21/2016.
 */
public class MessageFeedWorker implements Runnable {

    private MessageListener messageListener;
    private SymphonyClient symClient;
    private Logger logger = LoggerFactory.getLogger(MessageFeedWorker.class);
    private Datafeed datafeed;
    private MessageList messageList;

    public MessageFeedWorker(SymphonyClient symClient, MessageListener messageListener) {
        this.symClient = symClient;
        this.messageListener = messageListener;


    }

    public void run() {


        while (true) {


            try {

                if (datafeed == null) {
                    try {
                        logger.info("Creating datafeed with pod...");

                        datafeed = symClient.getAgentClient().createDatafeed();

                    } catch (Exception e) {

                        logger.error("Failed to create datafeed with pod, please check connection..", e);
                        datafeed = null;
                        try {
                            TimeUnit.SECONDS.sleep(2000);
                        } catch (InterruptedException e1) {
                            logger.error("Interrupt.. ", e1);
                        }
                        continue;
                    }

                }


                messageList = symClient.getAgentClient().getMessagesFromDatafeed(datafeed);

                if(messageList != null) {
                    logger.debug("Received {} messages..", messageList.size());

                    for (Message message : messageList) {
                        messageListener.onMessage(message);
                    }
                }

            } catch (Exception e) {
                logger.error("Failed to create read datafeed from pod, please check connection..resetting.", e);
                datafeed = null;

            }


        }

    }

}



