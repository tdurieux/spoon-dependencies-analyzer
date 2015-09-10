package github.tdurieux.testProject.entity;

import github.tdurieux.testProject.entity.User.Type;

public class Administrator extends User {

    private static final long serialVersionUID = 1909782862186322526L;

    private Type type = Type.USER;

    public Administrator() {
        super();
    }

}
