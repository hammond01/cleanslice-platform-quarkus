package domain.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import share.ModificationStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntityWithNumber extends BaseEntity {

    @Column(name = "number", unique = true, nullable = false)
    private String number;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "locked_by")
    private String lockedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "modification_status", nullable = false)
    private ModificationStatus modificationStatus = ModificationStatus.ACTIVE;

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
        this.setModificationStatus(ModificationStatus.ACTIVE);
        this.deletedAt = null;
        this.deletedBy = null;
    }

    // Getters and Setters
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public ModificationStatus getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(ModificationStatus modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}
