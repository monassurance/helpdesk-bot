/*
 *
 *
 * Copyright 2016 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.symphonyoss.webservice.util;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.symphonyoss.webservice.models.callback.EventCallback;

import java.util.ArrayList;

public class EventSupport<T> {

    public static final Logger log = LoggerFactory.getLogger(EventSupport.class);

    private ArrayList<T> listeners = new ArrayList<T>();

    public EventSupport() {
        super();
    }

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void removeListener(T listener) {
        listeners.add(listener);
    }

    public void fireEvent(EventCallback<T> c) {
        ArrayList<T> copy = new ArrayList<T>(listeners);
        for (T t : copy) {
            try {
                c.fire(t);
            } catch (Exception e) {
                log.error("Exception in snooper callback: ", e);
            }
        }
    }

}
