package com.jingtingonline.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.jingtingonline.myapplication.comment.CommentAdapter;
import com.jingtingonline.myapplication.comment.CommentModel;
import com.jingtingonline.myapplication.comment.ReplyModel;
import com.jingtingonline.myapplication.comment.model.Expanding;
import com.jingtingonline.myapplication.comment.model.FirstComment;
import com.jingtingonline.myapplication.comment.model.Collapsing;
import com.jingtingonline.myapplication.comment.model.ICommentItem;
import com.jingtingonline.myapplication.comment.model.LoadingItem;
import com.jingtingonline.myapplication.comment.model.SecondComment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements CommentAdapter.CommentItemActions {

    private CommentAdapter mAdapter;
    private List<ICommentItem> mDataList;

    private HashMap<FirstComment, List<SecondComment>> mCachedCommentMap = new HashMap<>();
    private HashMap<String, FirstComment> mCommentIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CommentAdapter();
        mAdapter.setItemAction(this);
        recyclerView.setAdapter(mAdapter);
        mDataList = generateCommentData();
        mAdapter.submitList(mDataList);

    }

    public List<ICommentItem> generateCommentData() {
        List<CommentModel> models = CommentModel.generateRandomCommentModels(60);
        List<ICommentItem> dataList = new ArrayList<>();
        for (CommentModel model : models) {
            FirstComment firstComment = map(model);
            dataList.add(firstComment);
            ArrayList<SecondComment> comments = new ArrayList<>();
            if (model.replyNums > 0) {
                Expanding expanding = createNew(model.replyNums, model.id);
                dataList.add(expanding);
            }
            mCommentIdMap.put(firstComment.id, firstComment);
            mCachedCommentMap.put(firstComment, comments);
        }
        return dataList;
    }

    private FirstComment map(CommentModel model) {
        return new FirstComment(model.id, 0, model.content);
    }

    private Expanding createNew(int count, String parentId) {
        return new Expanding(count, parentId);
    }

    @Override
    public void onExpand(Expanding expanding, int pos) {
        String commentId = expanding.parentId;
        int index = mDataList.indexOf(expanding);

        FirstComment firstComment = mCommentIdMap.get(commentId);
        List<SecondComment> secondComments = mCachedCommentMap.get(firstComment);
        if (secondComments.size() == 0) {
            LoadingItem loadingItem = new LoadingItem();
            loadingItem.parentId = commentId;
            loadingItem.state = LoadingItem.State.Loading;
            mDataList.set(index, loadingItem);
            mAdapter.submitList(new ArrayList<>(mDataList));
        }
        mDataList.remove(index);

        main.postDelayed(() -> {
            assert secondComments != null;
            int listSize = 0;
            if (secondComments.size() == 0) {
                List<ReplyModel> models = ReplyModel.generateRandomReplyModels(3);
                // do load more
                List<SecondComment> commentList = models.stream().map(replyModel -> new SecondComment(replyModel.id, commentId, replyModel.content, replyModel.isAuthor)).collect(Collectors.toList());
                if (secondComments != null) {
                    secondComments.addAll(commentList);
                    mCachedCommentMap.put(firstComment, commentList);
                }
                listSize = commentList.size();
                mDataList.addAll(index, commentList);
            } else {
                int begin = 0;
                for (int i = 0; i < secondComments.size(); i++) {
                    if (!secondComments.get(i).isAuthor) {
                        begin = i;
                        break;
                    }
                }
                List<SecondComment> commentList = secondComments.subList(begin, secondComments.size());
                listSize = commentList.size();
                mDataList.addAll(index, commentList);
            }

            // 如果有数据需要继续添加展开
            Collapsing collapsing = new Collapsing(commentId);
            mDataList.add(index + listSize, collapsing);
            mAdapter.submitList(new ArrayList<>(mDataList));
        }, 100);

    }

    @Override
    public void onCollapsing(Collapsing collapsing, int pos) {
        int index = mDataList.indexOf(collapsing);
        String id = collapsing.parentId;
        List<SecondComment> comments = mCachedCommentMap.get(mCommentIdMap.get(id));
        if (index == -1) {
            Log.d("lrannn", " 找不到index");
            return;
        }
        // 移除收起
        mDataList.remove(index);
        index--;

        while (!(mDataList.get(index) instanceof FirstComment)) {
            ICommentItem item = mDataList.get(index);
            if (item instanceof SecondComment) {
                if (!((SecondComment) item).isAuthor) {
                    Log.d("lrannn", " isAuthor = " + ((SecondComment) item).isAuthor + " >> index =" + index);
                    assert comments != null;
                    mDataList.remove(index);
                    index--;
                } else {
                    Log.d("lrannn", " isAuthor = true" + " >>> index" + index);
                    break;
                }
            }
        }

        Log.d("lrannn", "mDataList.get(index)=" + (mDataList.get(index)));
        mDataList.add(index + 1, new Expanding(3, id));
        mAdapter.submitList(new ArrayList<>(mDataList));
    }

    @Override
    public void addReply(SecondComment comment, int pos) {
        String parentId = (comment).parentId;
        int index = mDataList.indexOf(comment);
        FirstComment firstComment = mCommentIdMap.get(parentId);
        try {
            FirstComment newModel = (FirstComment) firstComment.clone();
            int secondCommentCount = firstComment.secondCommentCount;
            newModel.secondCommentCount += 1;
            mCommentIdMap.put(parentId, newModel);
            List<SecondComment> comments = mCachedCommentMap.get(firstComment);
            SecondComment userReplyComments = new SecondComment("aa", parentId, "哈哈哈我是测试数据", true);
            comments.add(userReplyComments);
            mCachedCommentMap.remove(firstComment);
            mCachedCommentMap.put(newModel, comments);
            mDataList.add(index + 1, userReplyComments);
            mAdapter.submitList(new ArrayList<>(mDataList));
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }

    public Handler main = new Handler(Looper.getMainLooper());


}