import com.class1.boot.MainApplication;
import com.class1.boot.dao.elastic.DiscussPostRepository;
import com.class1.boot.dao.mappers.DiscussPostMapper;
import com.class1.boot.pojo.DiscussPost;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MainApplication.class)
@SpringBootTest
public class DiscussPropertiesTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private DiscussPostRepository discussPostRepository;

    @Resource
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.getDiscussPostById(1));
        discussPostRepository.save(discussPostMapper.getDiscussPostById(2));
        discussPostRepository.save(discussPostMapper.getDiscussPostById(3));
        List<DiscussPost> discussPosts = discussPostMapper.getDiscussPosts();
        discussPostRepository.saveAll(discussPosts);

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
                .withQuery(QueryBuilders.multiMatchQuery("xxx","title","content"))
                .withSorts(SortBuilders.fieldSort("score")).build();
    }

}
