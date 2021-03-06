package org.symphonyoss.roomdesk.models.calls;

import org.symphonyoss.client.model.Chat;
import org.symphonyoss.roomdesk.listeners.service.CallServiceListener;

/**
 * A model that acts a skeleton for a MultiChatCall
 */
public abstract class MultiChatCall {
    protected CallServiceListener callServiceListener;

    public MultiChatCall() {
        callServiceListener = new CallServiceListener(this);
    }

    public abstract void initiateCall();

    public abstract void endCall();

    public abstract Chat getCallChat();

    public abstract CallTypes getCallType();

    public enum CallTypes {HELP_CALL}


}
