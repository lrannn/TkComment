package com.jingtingonline.myapplication.comment.model;

public class Collapsing extends ICommentItem {

    public String parentId;

    public Collapsing(String parentId) {
        id = "Folding";
        content = "收起";
        this.parentId = parentId;
    }

    @Override
    public boolean areContentsTheSame(ICommentItem other) {
        if (other instanceof Collapsing) {
            return ((Collapsing) other).equals(this);
        }
        return false;
    }
}
