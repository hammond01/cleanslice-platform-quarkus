package domain.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import share.ModificationStatus;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntityWithNumber extends BaseEntity {

    @Column(name = "number", unique = true, nullable = false)
    public String number;

    @Column(name = "locked_at")
    public LocalDateTime lockedAt;

    @Column(name = "locked_by")
    public String lockedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "modification_status", nullable = false)
    public ModificationStatus modificationStatus = ModificationStatus.ACTIVE;

    public boolean isLocked() {
        return lockedAt != null;
    }

    public void lock(String user) {
        lockedAt = LocalDateTime.now();
        lockedBy = user;
        modificationStatus = ModificationStatus.LOCKED;
    }

    public void unlock() {
        lockedAt = null;
        lockedBy = null;
        modificationStatus = ModificationStatus.ACTIVE;
    }

    public void restore() {
        this.modificationStatus = ModificationStatus.ACTIVE;
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
