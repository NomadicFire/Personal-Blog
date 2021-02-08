package com.jingwen.blog.service;

import com.jingwen.blog.dao.CommentRepository;
import com.jingwen.blog.po.Comment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        List<Comment> comments = commentRepository.findByBlogIdAndParentCommentNull(blogId, sort);
        return eachComment(comments);
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        Long parentCommentId = comment.getParentComment().getId();
        if (parentCommentId != -1) {
            comment.setParentComment(commentRepository.getOne(parentCommentId));
        } else {
            comment.setParentComment(null);
        }
        comment.setCreateTime(new Date());
        return commentRepository.save(comment);
    }

    /**
     * Iterate through all top-level comments
     * @param comments
     * @return
     */
    private List<Comment> eachComment(List<Comment> comments) {
        List<Comment> commentsView = new ArrayList<>();
        for (Comment comment : comments) {
            Comment c = new Comment();
            BeanUtils.copyProperties(comment, c);
            commentsView.add(c);
        }
        // combine all sub-comments to the first-level sub-comment
        combineChildren(commentsView);
        return commentsView;
    }

    /**
     *
     * @param comments root node
     */
    private void combineChildren(List<Comment> comments) {
        for (Comment comment : comments) {
            List<Comment> replys1 = comment.getReplyComments();
            for (Comment reply1 : replys1) {
                // recursively find all sub-comments and put them in tempReplys array
                recursively(reply1);
            }
            // make the top-level comment node's reply collection to the updated tempReplys collection
            comment.setReplyComments(tempReplys);
            // clear tempReply
            tempReplys = new ArrayList<>();
        }
    }

    // Stores all the sub-comments after recursion
    private List<Comment> tempReplys = new ArrayList<>();

    /**
     * recursion, peeling the onion
     * @param comment
     */
    private void recursively(Comment comment) {
        tempReplys.add(comment); // adding top-level nodes to tempReplys
        if (comment.getReplyComments().size() > 0) {
            List<Comment> replys = comment.getReplyComments();
            for (Comment reply : replys) {
                tempReplys.add(reply);
                if (reply.getReplyComments().size() > 0) {
                    recursively(reply);
                }
            }
        }
    }
}
