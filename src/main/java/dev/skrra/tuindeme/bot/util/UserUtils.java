package dev.skrra.tuindeme.bot.util;

import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.User;

public class UserUtils {


    public static String constructUserName(String firstName, String lastName, String userName) {
        if (!StringUtils.hasText(firstName) && !StringUtils.hasText(lastName) && !StringUtils.hasText(userName)) {
            return ".";
        }

        if (StringUtils.hasText(firstName)) {
            return StringUtils.hasText(lastName) ? (firstName + " " + lastName).trim() : firstName;
        }

        return StringUtils.hasText(userName) ? userName : ".";
    }

    public static String constructUserName(User user) {
        return constructUserName(user.getFirstName(), user.getLastName(), user.getUserName());
    }

}
