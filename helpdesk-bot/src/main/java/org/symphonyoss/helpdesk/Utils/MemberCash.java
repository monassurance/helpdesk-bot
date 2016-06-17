package org.symphonyoss.helpdesk.utils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.helpdesk.models.users.Member;
import org.symphonyoss.helpdesk.models.users.MemberWrapper;
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.pod.model.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nicktarsillo on 6/14/16.
 */
public class MemberCash {
    public static final Map<String, Member> MEMBERS = new HashMap<String, Member>();
    private static final Logger logger = LoggerFactory.getLogger(MemberCash.class);

    public static void loadMembers() {
        File[] files = new File(System.getProperty("files.json")).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.equals(".DS_Store");
            }
        });

        Gson gson = new Gson();

        if (files != null) {
            for (File file : files) {
                try {
                    logger.debug(file.getName());
                    Member member = gson.fromJson(new FileReader(file), MemberWrapper.class).toMember();
                    addMember(member);
                    logger.debug("Loaded member {}", member.getUserID());
                } catch (IOException e) {
                    logger.error("Could not load json {} ", file.getName(), e);
                }
            }
        }
    }

    public static void writeMember(Member member) {
        try {
            Gson gson = new Gson();
            FileWriter jsonFile = new FileWriter(System.getProperty("files.json") + member.getUserID() + ".json");
            String toJson = gson.toJson(member.toWrapper(), MemberWrapper.class);
            jsonFile.write(toJson);
            jsonFile.flush();
            jsonFile.close();

        } catch (IOException e) {
            logger.error("Could not write file for hashtag {}", member.getEmail(), e);
        }
    }

    public static void removeMember(Member member) {
        new File(System.getProperty("files.json") + member.getUserID() + ".json").delete();
        DeskUserCash.removeUser(member);
    }

    public static void addMember(Member member) {
        MemberCash.writeMember(member);
        MemberCash.MEMBERS.put(member.getUserID().toString(), member);
        DeskUserCash.addUser(member);
    }

    public static String listMembers(){
        String list = "";
        for (Member member: MEMBERS.values())
            list += ", " + member.getEmail();
        if(MEMBERS.size() > 0)
            return list.substring(1);
        else
            return list;
    }

    public static Member getMember(User user) {
        return MEMBERS.get(user.getId().toString());
    }

    public static Member getMember(Message message) {
        return MEMBERS.get(message.getFromUserId().toString());
    }

    public static Member getMember(String userID) {
        return MEMBERS.get(userID);
    }

    public static boolean hasMember(String userID) {
        return MEMBERS.containsKey(userID);
    }
}