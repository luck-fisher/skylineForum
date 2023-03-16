import com.class1.boot.MainApplication;
import com.class1.boot.service.impl.KafkaConsumers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MainApplication.class)
@SpringBootTest
public class KafkaTest {
    @Autowired
    private KafkaConsumers consumers;

    @Autowired
    private KafkaProducers producers;

    @Test
    public void kafkaRun(){
        producers.sendMessage("test","hello,world!");
        try {
            Thread.sleep(1000*10);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


