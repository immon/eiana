package org.iana.rzm.web.services;

public class TokenSynchronizerImpl implements TokenSynchronizer {

    private String token;
    private boolean resubmission = false;
    private SequenceGenerator sequenceGenerator;


    public String getToken() {
        token = String.valueOf(sequenceGenerator.next());
        return token;
    }


    public void setToken(String token) {
        if (this.token == null || !this.token.equals(token)) {
            resubmission = true;
        } else {
            this.token = null;
            resubmission = false;
        }
    }

    public boolean isResubmission() {
        return resubmission;
    }


    public void setSequenceGenerator(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }
}
