package com.class1.boot.dao.elastic;

import com.class1.boot.pojo.DiscussPost;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {
    @Query("{\"match\": {\"title\": {\"query\": \"?0\"}}}")
    @Highlight(fields = {
            @HighlightField(name = "title"),
            @HighlightField(name = "content")
    })
    List<DiscussPost> findByName(String keyword);

    List<DiscussPost> findByTitle(String title, Pageable pageable, HighlightBuilder highlightBuilder);
}
