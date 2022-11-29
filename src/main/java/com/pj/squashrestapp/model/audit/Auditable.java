package com.pj.squashrestapp.model.audit;

import com.pj.squashrestapp.util.GeneralUtil;

import java.time.LocalDateTime;

public interface Auditable {
    Audit getAudit();
    void setAudit(Audit audit);

    default void updateAudit() {
        final Audit audit = (getAudit() == null)
                ? new Audit()
                : getAudit();
        audit.setUpdatedBy(GeneralUtil.extractSessionUsername());
        audit.setUpdatedOn(LocalDateTime.now());
        setAudit(audit);
    }

    default void createAudit() {
        final Audit audit = (getAudit() == null)
                ? new Audit()
                : getAudit();
        audit.setCreatedBy(GeneralUtil.extractSessionUsername());
        audit.setCreatedOn(LocalDateTime.now());
        setAudit(audit);
    }
}
