package org.symphonyoss.webroomdesk.models.calls;

import org.symphonyoss.ai.constants.MLTypes;
import org.symphonyoss.ai.utils.Messenger;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.symphony.agent.model.MessageSubmission;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.pod.model.User;
import org.symphonyoss.symphony.pod.model.UserIdList;
import org.symphonyoss.webroomdesk.constants.HelpBotConstants;
import org.symphonyoss.webroomdesk.listeners.chat.HelpClientListener;
import org.symphonyoss.webroomdesk.listeners.chat.TranscriptListener;
import org.symphonyoss.webroomdesk.listeners.chat.WebCallChatListener;
import org.symphonyoss.webroomdesk.listeners.command.HelpCallCommandListener;
import org.symphonyoss.webroomdesk.listeners.command.MemberCommandListener;
import org.symphonyoss.webroomdesk.models.HelpBotSession;
import org.symphonyoss.webroomdesk.models.users.HelpClient;
import org.symphonyoss.webroomdesk.models.users.Member;

import java.util.HashSet;
import java.util.Set;

/**
 * A model that represents a call between a member and a client.
 */
public class MultiChatHelpCall extends MultiChatCall {
    protected SymphonyClient symClient;
    protected Chat helpChat;
    private HelpCallCommandListener helpCallCommandListener;
    protected MemberCommandListener memberCommandListener;
    private HelpClientListener helpClientListener;
    private TranscriptListener transcriptListener;
    protected Member member;
    private HelpClient client;

    public MultiChatHelpCall(Member member, HelpClient client, HelpBotSession session) {
        super();
        this.member = member;
        this.client = client;
        this.symClient = session.getSymphonyClient();

        this.memberCommandListener = session.getMemberListener();
        this.helpClientListener = session.getHelpClientListener();
        this.transcriptListener = session.getTranscriptListener();
    }

    /**
     * Starts the call.
     * Cross listeners.
     * Add new command listener.
     * Set on call.
     * Send initial web messages from the bot.
     */
    public void initiateCall() {
        helpChat = new Chat();
        helpChat.setLocalUser(symClient.getLocalUser());

        client.setCall(this);
        client.setOnCall(true);

        member.setCall(this);
        member.setOnCall(true);

        Set<User> users = new HashSet<User>();

        try {

            users.add(symClient.getUsersClient().getUserFromId(client.getUserID()));
            users.add(symClient.getUsersClient().getUserFromId(member.getUserID()));

            helpChat.setRemoteUsers(users);
            helpChat.setStream(symClient.getStreamsClient().getStream(users));

            helpClientListener.stopListening(getUserChat(client.getUserID()));
            memberCommandListener.stopListening(getUserChat(member.getUserID()));

            helpCallCommandListener = new HelpCallCommandListener(symClient, this);
            helpCallCommandListener.listenOn(getUserChat(client.getUserID()));
            helpCallCommandListener.listenOn(getUserChat(member.getUserID()));

            helpChat.registerListener(transcriptListener);

            Messenger.sendMessage(HelpBotConstants.CONNECTED_TO_CALL, MessageSubmission.FormatEnum.TEXT,
                    helpChat, symClient);

            Messenger.sendMessage(getRoomInfo(), MessageSubmission.FormatEnum.MESSAGEML,
                    helpChat, symClient);

            Messenger.sendMessage(getHelpSummary(), MessageSubmission.FormatEnum.MESSAGEML,
                    helpChat, symClient);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * End the call.
     * Cross listeners back.
     * Notify that the call has ended.
     */
    public void endCall() {
        helpCallCommandListener.stopListening(getUserChat(client.getUserID()));
        helpCallCommandListener.stopListening(getUserChat(member.getUserID()));

        helpClientListener.listenOn(getUserChat(client.getUserID()));

        memberCommandListener.setPushCommands(false);
        memberCommandListener.listenOn(getUserChat(member.getUserID()));
        memberCommandListener.setPushCommands(true);

        helpChat.removeListener(transcriptListener);

        Messenger.sendMessage(HelpBotConstants.EXIT_CALL, MessageSubmission.FormatEnum.TEXT,
                helpChat, symClient);

        client.setCall(null);
        client.setOnCall(false);

        member.setCall(null);
        member.setOnCall(false);
    }

    public Chat getCallChat() {
        return helpChat;
    }

    public CallTypes getCallType() {
        return CallTypes.HELP_CALL;
    }

    /**
     * Parses a string to contain the a description of the rooms current
     * state,
     *
     * @return the room info
     */
    public String getRoomInfo() {
        String roomInfo = MLTypes.START_ML + "Room Info:" + MLTypes.BREAK + "   Client: ";

        if (client.getEmail() == "" || client.getEmail() == null) {
            roomInfo += client.getUserID().toString() + MLTypes.BREAK;
        } else {
            roomInfo += client.getEmail() + MLTypes.BREAK;
        }

        roomInfo += "   " + HelpBotConstants.MEMBER_LABEL + ": " + member.getEmail() + MLTypes.BREAK;

        return roomInfo + MLTypes.END_ML;
    }

    /**
     * Gets the help summary from the client.
     *
     * @return the help summary
     */
    public String getHelpSummary() {
        return MLTypes.START_ML + "Help Summary:" + MLTypes.BREAK + client.getHelpSummary() + MLTypes.END_ML;
    }

    /**
     * Get the one on one user web.
     *
     * @param userID the user's ID
     * @return the web the user belongs to
     */
    protected Chat getUserChat(Long userID) {
        UserIdList list = new UserIdList();
        list.add(userID);
        Stream stream = null;
        try {
            stream = symClient.getStreamsClient().getStream(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return symClient.getChatService().getChatByStream(stream.getId());
    }
}
