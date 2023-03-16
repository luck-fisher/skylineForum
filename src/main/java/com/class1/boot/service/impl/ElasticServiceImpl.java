package com.class1.boot.service.impl;

import com.class1.boot.dao.elastic.DiscussPostRepository;
import com.class1.boot.dao.mappers.DiscussPostMapper;
import com.class1.boot.pojo.DiscussPost;
import com.class1.boot.service.ElasticService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ElasticServiceImpl implements ElasticService {

    @Resource
    DiscussPostRepository discussPostRepository;

    @Resource
    ElasticsearchOperations elasticsearchOperations;

    @Resource
    DiscussPostMapper discussPostMapper;
    /**
     * 把帖子保存在elastic中
     */
    @Override
    public void savePost(DiscussPost post) {
        discussPostRepository.save(post);
    }

    /**
     * 删除帖子
     */
    @Override
    public void deletePost(int postId) {
        discussPostRepository.delete(discussPostMapper.getDiscussPostById(postId));
    }

    /**
     * 获得查询的分页集合
     * @param keyword 关键词
     * @param pageNum 页数
     */
    @Override
    public SearchHits<DiscussPost> getSearchRes(String keyword, int pageNum) {
        NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(pageNum,10))
                .withHighlightFields(
                      new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                      new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        SearchHits<DiscussPost> hits = elasticsearchOperations.search(queryBuilder, DiscussPost.class);
        for (SearchHit<DiscussPost> hit:
             hits) {
            List<String> titleHighlights = hit.getHighlightFields().get("title");
            List<String> contentHighlights = hit.getHighlightFields().get("content");
            if (!CollectionUtils.isEmpty(contentHighlights)) {
                hit.getContent().setContent(contentHighlights.get(0));
            }
            if (!CollectionUtils.isEmpty(titleHighlights)) {
                hit.getContent().setTitle(titleHighlights.get(0));
            }
        }
        return hits;
    }
}
