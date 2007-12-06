package org.iana.rzm.trans.confirmation.usdoc;

import javax.persistence.Embeddable;

/**
 * @author Patrycja Wegrzynowicz
 */
@Embeddable
public class USDoCConfirmation {

    public static enum Type { REQUIRED, ACCEPTED, REJECTED }

    /**
     * This field denotes whether the confirmation was
     */
    Type databaseChangeConfirmation;

    Type nameserversChangeConfirmation;


    private USDoCConfirmation() {
    }

    public USDoCConfirmation(boolean databaseChangeConfirmation, boolean nameserversChangeConfirmation) {
        if (databaseChangeConfirmation) this.databaseChangeConfirmation = Type.REQUIRED;
        if (nameserversChangeConfirmation) this.nameserversChangeConfirmation = Type.REQUIRED;
    }

    public boolean isAccepted() {
        return isDatabaseChangeAccepted() && isNameserversChangeAccepted();
    }

    public boolean isReceived() {
        return isDatabaseChangeReceived() && isNameserversChangeReceived();
    }

    public boolean isDatabaseChangeConfRequired() {
        return databaseChangeConfirmation != null;
    }

    public boolean isNameserversChangeConfRequired() {
        return nameserversChangeConfirmation != null;
    }

    public boolean isDatabaseChangeAccepted() {
        return databaseChangeConfirmation == null || databaseChangeConfirmation == Type.ACCEPTED;
    }

    public boolean isNameserversChangeAccepted() {
        return nameserversChangeConfirmation == null || nameserversChangeConfirmation == Type.ACCEPTED;
    }

    public boolean isDatabaseChangeReceived() {
        return databaseChangeConfirmation == null || databaseChangeConfirmation != Type.REQUIRED;
    }

    public boolean isNameserversChangeReceived() {
        return nameserversChangeConfirmation == null || nameserversChangeConfirmation != Type.REQUIRED;
    }

    public void setDatabaseChangeConfirmation(boolean accepted) throws USDoCConfirmationException {
        if (!isDatabaseChangeConfRequired()) {
            throw new USDoCConfirmationNotExpected(true);
        }
        Type type = accepted ? Type.ACCEPTED : Type.REJECTED;
        if (databaseChangeConfirmation == type) {
            throw new USDoCConfirmationAlreadyReceived(true);
        }
        if (isNameserversChangeConfRequired() &&
                isNameserversChangeReceived() &&
                nameserversChangeConfirmation != type) {
            throw new USDoCConfirmationMismatch(true);
        }
        databaseChangeConfirmation = type;
    }

    public void setNameserversChangeConfirmation(boolean accepted) throws USDoCConfirmationException {
        if (!isNameserversChangeConfRequired()) {
            throw new USDoCConfirmationNotExpected(false);
        }
        Type type = accepted ? Type.ACCEPTED : Type.REJECTED;
        if (nameserversChangeConfirmation == type) {
            throw new USDoCConfirmationAlreadyReceived(false);
        }
        if (isDatabaseChangeConfRequired() &&
                isDatabaseChangeReceived() &&
                databaseChangeConfirmation != type) {
            throw new USDoCConfirmationMismatch(true);
        }
        nameserversChangeConfirmation = type;
    }

}
