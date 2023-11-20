package com.jingtingonline.myapplication.comment.model;

import androidx.annotation.NonNull;

public class FirstComment extends ICommentItem implements Cloneable {

    public FirstComment(String id, int secondCommentCount, String selfData) {
        this.id = id;
        this.secondCommentCount = secondCommentCount;
        this.selfData = selfData;
    }

    public int secondCommentCount;

    public String selfData;


    @Override
    public boolean areContentsTheSame(ICommentItem other) {
        if (other instanceof FirstComment) {
            return ((FirstComment) other).equals(this);
        }
        return false;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
