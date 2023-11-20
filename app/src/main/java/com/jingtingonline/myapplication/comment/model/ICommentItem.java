package com.jingtingonline.myapplication.comment.model;

public abstract class ICommentItem {
    public String id = null;
    public String content = null;

    public abstract boolean areContentsTheSame(ICommentItem other);
}
