package com.activiti.extension.oaf.constants;
/**
 * This Enum specifies the status of OrderApproval process in Database.
 * @author Pradip Patel
 */
public enum OAFStatus {
    START("START"),
    WAIT("WAIT"),
    STOP("STOP"),
    COMPLETE("COMPLETE"),
    REJECT("REJECT"),
    FAIL("FAIL");

    private final String status;

    /**
     * @param status
     * @description set status
     */
    OAFStatus(final String status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.status;
    }

}
