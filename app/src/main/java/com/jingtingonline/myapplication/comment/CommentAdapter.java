package com.jingtingonline.myapplication.comment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jingtingonline.myapplication.R;
import com.jingtingonline.myapplication.comment.model.Expanding;
import com.jingtingonline.myapplication.comment.model.FirstComment;
import com.jingtingonline.myapplication.comment.model.Collapsing;
import com.jingtingonline.myapplication.comment.model.ICommentItem;
import com.jingtingonline.myapplication.comment.model.LoadingItem;
import com.jingtingonline.myapplication.comment.model.SecondComment;

import java.util.Objects;

public class CommentAdapter extends ListAdapter<ICommentItem, CommentAdapter.ViewHolder> {

    public static final int ITEM_TYPE_FIRST_COMMENT = 0;
    public static final int ITEM_TYPE_SECOND_COMMENT = 1;
    public static final int ITEM_TYPE_LOADING = 2;
    public static final int ITEM_TYPE_EXPAND = 3;
    public static final int ITEM_TYPE_FOLD = 4;
    private CommentItemActions itemActions;


    public CommentAdapter() {
        this(new DiffUtil.ItemCallback<ICommentItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull ICommentItem oldItem, @NonNull ICommentItem newItem) {
                return Objects.equals(oldItem.id, newItem.id);
            }

            @Override
            public boolean areContentsTheSame(@NonNull ICommentItem oldItem, @NonNull ICommentItem newItem) {
                return oldItem.areContentsTheSame(newItem);
            }
        });
    }

    private CommentAdapter(@NonNull DiffUtil.ItemCallback<ICommentItem> diffCallback) {
        super(diffCallback);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.item_expanding;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewHolder holder = null;
        switch (viewType) {
            case ITEM_TYPE_EXPAND:
                holder = new ExpandViewHolder(inflater.inflate(layoutId, parent, false));
                break;
            case ITEM_TYPE_FIRST_COMMENT:
                layoutId = R.layout.item_first_comment;
                holder = new FirstLevelCommentViewHolder(inflater.inflate(layoutId, parent, false));
                break;
            case ITEM_TYPE_LOADING:
                layoutId = R.layout.item_loading;
                holder = new LoadingViewHolder(inflater.inflate(layoutId, parent, false));
                break;
            case ITEM_TYPE_FOLD:
                layoutId = R.layout.item_folding;
                holder = new CollapsingViewHolder(inflater.inflate(layoutId, parent, false));
                break;
            default:
                layoutId = R.layout.item_second_comment;
                holder = new SecondLevelCommentViewHolder(inflater.inflate(layoutId, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("CommentAdapter", "holder  = " + holder + " >> position = " + position);
        int viewType = getItemViewType(position);
        ICommentItem commentItem = getItem(position);
        holder.setOnCommentItemAction(itemActions);
        holder.bind(commentItem, position);
    }

    @Override
    public int getItemViewType(int position) {
        ICommentItem commentItem = getItem(position);
        if (commentItem instanceof FirstComment) {
            return ITEM_TYPE_FIRST_COMMENT;
        }
        if (commentItem instanceof SecondComment) {
            return ITEM_TYPE_SECOND_COMMENT;
        }
        if (commentItem instanceof LoadingItem) {
            return ITEM_TYPE_LOADING;
        }
        if (commentItem instanceof Collapsing) {
            return ITEM_TYPE_FOLD;
        }
        if (commentItem instanceof Expanding) {
            return ITEM_TYPE_EXPAND;
        }
        return -1;
    }


    public void setItemAction(CommentItemActions action) {
        itemActions = action;
    }

    class SecondLevelCommentViewHolder extends ViewHolder {

        private final TextView tvContent;

        public SecondLevelCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
        }

        @Override
        public void bind(ICommentItem item, int pos) {
            SecondComment comment = (SecondComment) item;
            String data = comment.selfData;
            tvContent.setText(data);
            tvContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemActions.addReply(comment, pos);
                }
            });
        }
    }

    class FirstLevelCommentViewHolder extends ViewHolder {

        private final TextView tvContent;

        public FirstLevelCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);

        }

        @Override
        public void bind(ICommentItem item, int pos) {
            FirstComment comment = (FirstComment) item;
            String data = comment.selfData;
            tvContent.setText(data);
        }
    }

    class CollapsingViewHolder extends ViewHolder {

        private final TextView tvFolder;

        public CollapsingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFolder = itemView.findViewById(R.id.tvFoldState);
        }

        @Override
        void bind(ICommentItem item, int pos) {
            Collapsing collapsing = (Collapsing) item;
            tvFolder.setOnClickListener(view -> itemActions.onCollapsing(collapsing, pos));
        }
    }


    class LoadingViewHolder extends ViewHolder {

        private final TextView tvLoading;


        private Handler mainHandler = new Handler(Looper.getMainLooper());

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLoading = itemView.findViewById(R.id.tvLoading);
        }


        public int animVal = 0;

        @Override
        void bind(ICommentItem item, int pos) {
            LoadingItem loadingItem = (LoadingItem) item;
            if (loadingItem.state == LoadingItem.State.Loading) {
                mainHandler.post(() -> {
                    animVal++;
                    StringBuilder content = new StringBuilder(".");
                    for (int i = 0; i < animVal; i++) {
                        content.append(".");
                    }
                    tvLoading.setText("正在加载" + content.toString());

                });
            }
        }

    }


    class ExpandViewHolder extends ViewHolder {

        private final TextView tvExpanding;

        public ExpandViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExpanding = itemView.findViewById(R.id.tvExpand);
        }

        @Override
        public void bind(ICommentItem item, int pos) {
            Expanding expanding = (Expanding) item;
            tvExpanding.setText(expanding.content);
            tvExpanding.setOnClickListener(view -> {
                itemActions.onExpand(expanding, pos);
            });
        }

    }


    abstract class ViewHolder extends RecyclerView.ViewHolder {

        public CommentItemActions itemActions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }


        abstract void bind(ICommentItem item, int pos);

        public void setOnCommentItemAction(CommentItemActions action) {
            itemActions = action;
        }

    }

    public interface CommentItemActions {
        void onExpand(Expanding expanding, int pos);

        void onCollapsing(Collapsing collapsing, int pos);

        void addReply(SecondComment comment, int pos);
    }
}
