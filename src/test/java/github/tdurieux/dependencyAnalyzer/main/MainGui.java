package github.tdurieux.dependencyAnalyzer.main;


import org.junit.Test;

public class MainGui {
    @Test
    public void testMain() {
        GUILauncher.main(new String[]{"src/testProject/java"});
    }
}
