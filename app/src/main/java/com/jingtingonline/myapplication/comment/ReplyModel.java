package com.jingtingonline.myapplication.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReplyModel {

    public String id;

    public String content;

    public String nickName;

    public int likeState;

    public String time;

    public boolean isAuthor = false;

    public static List<ReplyModel> generateRandomReplyModels(int count) {
        List<ReplyModel> replyModels = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            ReplyModel replyModel = new ReplyModel();
            replyModel.content = generateRandomString();
            replyModel.nickName = generateRandomString();
            replyModel.likeState = generateRandomLikeState();
            replyModel.time = generateRandomTimeString();
            replyModel.id = generateRandomString();
            replyModel.isAuthor = i == 0 || i == 1;

            replyModels.add(replyModel);
        }

        return replyModels;
    }

    private static String generateRandomString() {
        // 生成随机字符串的逻辑，你可以根据需要进行更改
        // 这里简单地返回一个固定长度的随机字符串
        int length = 10;
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

    private static int generateRandomLikeState() {
        // 生成随机点赞状态的逻辑，你可以根据需要进行更改
        // 这里简单地返回 0 或 1，表示不喜欢或喜欢
        return new Random().nextInt(2);
    }

    private static String generateRandomTimeString() {
        // 生成随机时间字符串的逻辑，你可以根据需要进行更改
        // 这里简单地返回一个固定格式的随机时间字符串
        return "2023-11-18 12:00:00";
    }
}
