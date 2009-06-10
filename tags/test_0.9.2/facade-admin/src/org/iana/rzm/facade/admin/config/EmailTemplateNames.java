package org.iana.rzm.facade.admin.config;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface EmailTemplateNames {

    String CONTACT_CONFIRMATION = "contact-confirmation";
    String TECHNICAL_DEFICIENIES = "technical-deficiencies";
    String NORMAL_REDELEGATION_PROCESSING = "normal_redelegation-processing";
    String THIRD_PARTY_CONSULTATION = "third_party-consultation";
    String COMPLETED_NSCHANGE = "completed-nschange";
    String COMPLETED = "completed";
    String WITHDRAWN = "withdrawn";
    String ADMIN_CLOSED = "admin-closed";
    String EXCEPTION = "exception";
    String REJECTED = "rejected";
    String CONTACT_CONFIRMATION_REMAINDER = "contact-confirmation-remainder";
    String IMPACTED_PARTIES_CONFIRMATION = "impacted_parties-confirmation";
    String IMPACTED_PARTIES_CONFIRMATION_REMAINDER = "impacted_parties-confirmation-remainder";
    String ZONE_INSERTION_ALERT = "zone-insertion-alert";
    String ZONE_PUBLICATION_ALERT = "zone-publication-alert";
    String TECHNICAL_CHECK_PERIOD = "technical-check-period";
    String TECHNICAL_CHECK = "technical-check";
    String USDOC_CONFIRMATION = "usdoc-confirmation";
    String USDOC_CONFIRMATION_NSCHANGE = "usdoc-confirmation-nschange";
    String USDOC_CONFIRMATION_REMAINDER = "usdoc-confirmation-remainder";
    String PASSWORD_CHANGE = "password-change";
    String SPECIAL_REVIEW = "special-review";
    String MANUAL_REVIEW = "manual-review";

    public static final String[] EMAIL_TEMPLATE_NAMES = {
        CONTACT_CONFIRMATION,
        TECHNICAL_DEFICIENIES,
        NORMAL_REDELEGATION_PROCESSING,
        THIRD_PARTY_CONSULTATION,
        COMPLETED_NSCHANGE,
        COMPLETED,
        WITHDRAWN,
        ADMIN_CLOSED,
        EXCEPTION,
        REJECTED,
        CONTACT_CONFIRMATION_REMAINDER,
        IMPACTED_PARTIES_CONFIRMATION,
        IMPACTED_PARTIES_CONFIRMATION_REMAINDER,
        ZONE_INSERTION_ALERT,
        ZONE_PUBLICATION_ALERT,
        TECHNICAL_CHECK_PERIOD,
        TECHNICAL_CHECK,
        USDOC_CONFIRMATION,
        USDOC_CONFIRMATION_NSCHANGE,
        USDOC_CONFIRMATION_REMAINDER,
        PASSWORD_CHANGE,
        SPECIAL_REVIEW,
        MANUAL_REVIEW
    };


}
