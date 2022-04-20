package com.pj.squashrestapp.util;

import com.pj.squashrestapp.dbinit.fake.FakeUtil;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@UtilityClass
public class EmojiUtil {

    private static final List<String> FILTER_ALLOWED = List.of(
            "face",
            "computer",
            "aubergine",
            "tomato",
            "potato",
            "apple",
            "cat",
            "dog",
            "fish",
            "fruit",
            "money",
            "doughnut",
            "ice",
            "hand",
            "horse",
            "monkey",
            "panda",
            "vehicle",
            "car",
            "package",
            "robot",
            "pet",
            "rainbow",
            "pizza",
            "house",
            "sports",
            "beetle",
            "snail",
            "book",
            "volleyball",
            "tennis",
            "male",
            "female",
            "ball",
            "ship",
            "female");

    private static final List<String> FILTER_FORBIDDEN =
            List.of("middle", "indicator", "clock", "family", "regional", "wind");

    public static final List<String> EMOJIS = EmojiManager.getAll().stream()
            .filter(EmojiUtil::isAllowed)
            .map(Emoji::getUnicode)
            .collect(Collectors.toList());

    public static String getRandom() {
        final int random = FakeUtil.randomBetweenTwoIntegers(0, EMOJIS.size() - 1);
        return EMOJIS.get(random);
    }

    private static boolean isAllowed(final Emoji emoji) {
        final Set<String> set = new TreeSet<>();

        for (final String tag : emoji.getTags()) {
            final List<String> sublist =
                    List.of(tag.replaceAll("[^A-Za-z]", " ").split("\\s+"));
            set.addAll(sublist);
        }

        for (final String alias : emoji.getAliases()) {
            final List<String> sublist =
                    List.of(alias.replaceAll("[^A-Za-z]", " ").split("\\s+"));
            set.addAll(sublist);
        }

        final List<String> sublist =
                List.of(emoji.getDescription().replaceAll("[^A-Za-z]", " ").split("\\s+"));
        set.addAll(sublist);

        return CollectionUtils.containsAny(set, FILTER_ALLOWED) && !CollectionUtils.containsAny(set, FILTER_FORBIDDEN);
    }
}
