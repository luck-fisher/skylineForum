import com.class1.boot.MainApplication;
import com.class1.boot.dao.mappers.DiscussPostMapper;
import com.class1.boot.dao.mappers.UserMapper;
import com.class1.boot.pojo.DiscussPost;
import com.class1.boot.pojo.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MainApplication.class)
@SpringBootTest
public class SkylineForumTest {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testGetUser(){
        User user = userMapper.getUserByEmail("2");
        System.out.println(user);
    }

    @Test
    public void testInsert(){
        User user = new User();
        user.setUsername("12");
        user.setPassword("12");
        user.setSalt("12");
        user.setEmail("12");
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode("12");
        user.setHeaderUrl("12");
        user.setCreateTime(new Date());

        userMapper.addUser(user);
    }

    @Test
    public void testGetDiscussPost(){
        PageHelper.startPage(1,2);

        List<Map<String,Object>> list = discussPostMapper.getAllDiscussPost(0);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list, 5);

        for (Map<String, Object> discussPost : pageInfo.getList()) {
            User user = userMapper.getUserById((Integer) discussPost.get("user_id"));
            discussPost.put("user",user);
        }

        System.out.println(pageInfo.getList());
    }

}
