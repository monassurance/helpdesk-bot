package org.symphonyoss.webroomdesk.listeners.chat;

import org.symphonyoss.ai.constants.MLTypes;
import org.symphonyoss.ai.listeners.AiCommandListener;
import org.symphonyoss.ai.utils.Messenger;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.agent.model.MessageSubmission;
import org.symphonyoss.webroomdesk.config.HelpBotConfig;
import org.symphonyoss.webroomdesk.models.users.DeskUser;
import org.symphonyoss.webroomdesk.models.users.Member;
import org.symphonyoss.webroomdesk.utils.DeskUserCache;

/**
 * Created by nicktarsillo on 7/7/16.
 */
public class MemberAliasListener implements ChatListener {
    private SymphonyClient symClient;

    public MemberAliasListener(SymphonyClient symClient) {
        this.symClient = symClient;
    }

    public void onChatMessage(Message message) {
        if (message == null
                || message.getStream() == null
                || AiCommandListener.isCommand(message, symClient))
            return;


        try {

            if (DeskUserCache.getDeskUser(message.getFromUserId().toString()).getUserType() != DeskUser.DeskUserType.MEMBER)
                return;

            Member member = (Member) DeskUserCache.getDeskUser(message.getFromUserId().toString());

            if (member.isUseAlias()) {


                if (member.isOnCall()) {

                    Messenger.sendMessage(MLTypes.START_ML.toString() + MLTypes.START_BOLD
                                    + member.getAlias() + ": " + MLTypes.END_BOLD
                                    + message.getMessage().substring(MLTypes.START_ML.toString().length()),
                            MessageSubmission.FormatEnum.MESSAGEML,
                            member.getCall().getCallChat(),
                            symClient);

                } else {


                    Chat chat = symClient.getChatService().getChatByStream(System.getProperty(HelpBotConfig.MEMBER_CHAT_STREAM));

                    Messenger.sendMessage(MLTypes.START_ML.toString() + MLTypes.START_BOLD
                                    + member.getAlias() + ": " + MLTypes.END_BOLD
                                    + message.getMessage().substring(MLTypes.START_ML.toString().length()),
                            MessageSubmission.FormatEnum.MESSAGEML, chat, symClient);

                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenOn(Chat chat) {
        if (chat != null && chat.getRemoteUsers().size() == 1) {
            chat.registerListener(this);
        }
    }

    public void stopListenung(Chat chat) {
        if (chat != null) {
            chat.removeListener(this);
        }
    }
}
