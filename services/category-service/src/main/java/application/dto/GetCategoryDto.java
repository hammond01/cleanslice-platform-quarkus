package application.dto;

import java.time.LocalDateTime;

public class GetCategoryDto {
    public Long id;
    public String name;
    public String description;
    public Long parentId;
    public String slug;
    public LocalDateTime createdAt;
    public String createdBy;
    public LocalDateTime lastModifiedAt;
    public String lastModifiedBy;
    public boolean active;
}


