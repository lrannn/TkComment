package com.jingtingonline.myapplication.comment.model;

import androidx.annotation.NonNull;

public class Expanding extends ICommentItem implements Cloneable {

    public String parentId;

    public Expanding(int count, String parentId) {
        id = "Expanding";
        this.parentId = parentId;
        content = "展开" + count + "条回复";
    }

    @Override
    public boolean areContentsTheSame(ICommentItem other) {
        if (other instanceof Expanding) {
            return ((Expanding) other).equals(this);
        }
        return false;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
