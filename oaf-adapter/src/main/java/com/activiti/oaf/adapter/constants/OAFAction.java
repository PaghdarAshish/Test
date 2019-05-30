package com.activiti.oaf.adapter.constants;

/**
 * This Enum specifies the Action need to taken on OrderApproval request while running scheduler.
 * @author Pradip Patel
 */
public enum OAFAction {
    NEW("NEW"),
    RESTART("RESTART"),
    UPDATE("UPDATE"),
    RE_INITIATE("RE_INITIATE"),
    AUTO_RESTART("AUTO_RESTART");

    private final String status;

    /**
     * @param status
     * @description set status
     */
    OAFAction(final String status) {
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
