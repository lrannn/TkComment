package com.jingtingonline.myapplication.comment.model;

public class SecondComment extends ICommentItem {

    public boolean isAuthor;

    public SecondComment(String id, String parentId, String selfData,boolean isAuthor) {
        this.id = id;
        this.parentId = parentId;
        this.selfData = selfData;
        this.isAuthor = isAuthor;
    }

    public String parentId;
    public String selfData = "";

    @Override
    public boolean areContentsTheSame(ICommentItem other) {
        if (other instanceof SecondComment) {
            return ((SecondComment) other).equals(this);
        }
        return false;
    }
}
