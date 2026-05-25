package io.cleanslice.platform.domain;

import io.cleanslice.platform.domain.enums.ModificationStatus;
import java.time.LocalDateTime;

public abstract class BaseEntity {

    public Integer RowId;
    public LocalDateTime createdAt;
    public String createdBy;
    public LocalDateTime lastModifiedAt;
    public String lastModifiedBy;
    public LocalDateTime lockedAt;
    public String lockedBy;
    public LocalDateTime deletedAt;
    public String deletedBy;
    public Long rowVersion;
    public ModificationStatus modificationStatus = ModificationStatus.ACTIVE;

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

