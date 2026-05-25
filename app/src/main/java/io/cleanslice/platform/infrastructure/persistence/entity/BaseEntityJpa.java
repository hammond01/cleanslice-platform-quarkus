package io.cleanslice.platform.infrastructure.persistence.entity;

import io.cleanslice.platform.infrastructure.persistence.AuditingEntityListener;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import io.cleanslice.platform.domain.enums.ModificationStatus;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityJpa extends PanacheEntityBase {

    @Column(name = "row_id", insertable = false, updatable = false)
    public Integer RowId;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    public String createdBy;

    @Column(name = "last_modified_at")
    public LocalDateTime lastModifiedAt;

    @Column(name = "last_modified_by")
    public String lastModifiedBy;

    @Column(name = "locked_at")
    public LocalDateTime lockedAt;

    @Column(name = "locked_by")
    public String lockedBy;

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    public String deletedBy;

    @Version
    @Column(name = "row_version")
    public Long rowVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "modification_status", nullable = false)
    public ModificationStatus modificationStatus = ModificationStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (modificationStatus == null) {
            modificationStatus = ModificationStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }
}

