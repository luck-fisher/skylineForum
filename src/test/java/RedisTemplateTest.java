import com.class1.boot.MainApplication;
import com.class1.boot.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MainApplication.class)
@SpringBootTest
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void testStrings(){
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey,2);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
    }

    @Test
    public void testHash(){
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey,"id",1);

    }
    @Test
    public void bb(){
        System.out.println(CommunityUtil.getJsonString(1,"失败"));
    }
}
