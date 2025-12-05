package domain.entity.base;

import infrastructure.persistence.AuditingEntityListener;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import share.ModificationStatus;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

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

    public boolean isLocked() {
        return lockedAt != null;
    }

    public boolean isDeleted() {
        return deletedAt != null || modificationStatus == ModificationStatus.DELETED;
    }

    public void lock(String userId) {
        this.lockedAt = LocalDateTime.now();
        this.lockedBy = userId;
        this.modificationStatus = ModificationStatus.LOCKED;
    }

    public void unlock() {
        this.lockedAt = null;
        this.lockedBy = null;
        this.modificationStatus = ModificationStatus.ACTIVE;
    }

    public void softDelete(String userId) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = userId;
        this.modificationStatus = ModificationStatus.DELETED;
    }

    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
        this.modificationStatus = ModificationStatus.ACTIVE;
    }
}
