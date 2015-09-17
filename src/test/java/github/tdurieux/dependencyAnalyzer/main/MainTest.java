package github.tdurieux.dependencyAnalyzer.main;


import org.junit.Test;

public class MainTest {
    @Test
    public void testMain() throws Exception {
        Main.main(new String[]{"src/testProject/java"});
    }
}
