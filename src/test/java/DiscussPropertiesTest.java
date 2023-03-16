import com.class1.boot.MainApplication;
import com.class1.boot.dao.elastic.DiscussPostRepository;
import com.class1.boot.dao.mappers.DiscussPostMapper;
import com.class1.boot.pojo.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MainApplication.class)
@SpringBootTest
public class DiscussPropertiesTest {
    @Resource
    private ElasticsearchOperations elasticsearchOperations;
    @Resource
    private DiscussPostRepository discussPostRepository;
    @Resource
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.getDiscussPostById(21));
        discussPostRepository.save(discussPostMapper.getDiscussPostById(25));
//        discussPostRepository.save(discussPostMapper.getDiscussPostById(3));
//        List<DiscussPost> discussPosts = discussPostMapper.getDiscussPosts();
//        discussPostRepository.saveAll(discussPosts);

    }

    @Test
    public void testUpdate(){
        DiscussPost post = discussPostMapper.getDiscussPostById(2);
        post.setContent("哈哈哈，我来啦");
        discussPostRepository.save(post);
    }

    @Test
    public void testDelete(){
        DiscussPost post = discussPostMapper.getDiscussPostById(3);
        discussPostRepository.delete(post);
    }

    @Test
    public void testQuery(){

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("美好 相机 图片","title","content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
          SearchHits<DiscussPost> hits = elasticsearchOperations.search(searchQuery, DiscussPost.class);

//        Criteria criteria = new Criteria();
//        List<DiscussPost> xxx = discussPostRepository.findByName("xxx");
////        System.out.println(xxx);
//        com.class1.boot.pojo.PageRequest pageRequest = new com.class1.boot.pojo.PageRequest()
//                .setPageNumber(0).setPageSize(2).setSortDirection(Sort.Direction.DESC).setSortBy("createTime");
//        Pageable pageable = PageRequest.of(pageRequest.getPageNumber(),pageRequest.getPageSize(),
//                pageRequest.getSortDirection(),pageRequest.getSortBy());
//        HighlightBuilder highlightBuilder = new HighlightBuilder().field("title").preTags("<em>").postTags("</em>");
        for (SearchHit<DiscussPost> hit : hits) {
            List<String> contentHighlights = hit.getHighlightFields().get("content");
            List<String> titleHighlights = hit.getHighlightFields().get("title");
            System.out.println(contentHighlights);
            System.out.println(titleHighlights);
            if (!CollectionUtils.isEmpty(contentHighlights)) {
                hit.getContent().setContent(contentHighlights.get(0));
            }
            if (!CollectionUtils.isEmpty(titleHighlights)) {
                hit.getContent().setTitle(titleHighlights.get(0));
            }
        }
//        System.out.println(discussPostRepository.findByTitle("美好", pageable,highlightBuilder));
        for (SearchHit<DiscussPost> hit : hits) {
            System.out.println(hit);
        }
    }

}

