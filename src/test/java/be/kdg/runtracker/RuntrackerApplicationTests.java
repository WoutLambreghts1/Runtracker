package be.kdg.runtracker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = be.kdg.runtracker.RuntrackerApplicationTests.class)
@ComponentScan("be.kdg.runtracker")
public class RuntrackerApplicationTests {

	@Test
	public void contextLoads() {
	}

}
