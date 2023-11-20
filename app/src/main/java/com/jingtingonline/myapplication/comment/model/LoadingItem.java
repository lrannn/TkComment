package com.jingtingonline.myapplication.comment.model;

public class LoadingItem extends ICommentItem {

    public String parentId;

    public LoadingItem() {
        state = State.Idle;
        id = "loading";
    }

    public State state;

    @Override
    public boolean areContentsTheSame(ICommentItem other) {
        if (other instanceof LoadingItem) {
            return ((LoadingItem) other).equals(this);
        }
        return false;
    }

    public enum State {
        Idle, Loading, Completed;
    }

}
