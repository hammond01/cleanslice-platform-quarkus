package io.cleanslice.platform.domain;


public class Category extends BaseEntityWithNumber {
    public String name;
    public String description;
    public Long parentId;
    
    public String slug;
    
    public boolean active = true;
}


