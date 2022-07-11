package net.thumbtack.busserver.model;

public enum Role {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String name;

    Role(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }

    public static Role getByName(String name){
        Role.values();
        for (Role r : Role.values()){
            if (r.getName().equals(name)){
                return r;
            }
        }
        return null;
    }

}
