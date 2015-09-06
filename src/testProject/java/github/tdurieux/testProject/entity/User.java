package github.tdurieux.testProject.entity;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 2603156267507828552L;

    public enum Type {
        USER, ADMIN
    }

    private String name;
    private int id;
    private Type type = Type.USER;

    public String print(Class object) {

        return object.getSimpleName();
    }

    public void thread() throws NullPointerException {
        new Thread(new Runnable() {

            @Override
            public void run() {
                print(this.getClass());
            }
        }).run();
    }

    public void throwException() throws Exception {
        throw new RuntimeException("");
    }

    public Boolean isInteger(Object o) {
        if (o instanceof Integer) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {

        return "[ " + id + "] " + name;
    }

}
