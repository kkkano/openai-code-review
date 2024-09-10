package plus.gaga.middlewatr.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Test
    public void test() {
        System.out.println(Integer.parseInt("abc1234"));
        System.out.println(Integer.parseInt("abc12345"));
        System.out.println(Integer.parseInt("abc123456"));
        System.out.println(Integer.parseInt("abc12345678911111"));
    }

}
