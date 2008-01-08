package org.iana.rzm.web.tapestry.state;

import org.apache.tapestry.engine.state.StateObjectFactory;
import org.iana.rzm.web.services.SequenceGenerator;
import org.iana.rzm.web.services.TokenSynchronizerImpl;


public class TokenSynchronizerFactory implements StateObjectFactory {
    private SequenceGenerator sequenceGenerator;


    public Object createStateObject() {
        TokenSynchronizerImpl tokenSynchronizer = new TokenSynchronizerImpl();
        tokenSynchronizer.setSequenceGenerator(sequenceGenerator);
        return tokenSynchronizer;
    }

    public void setSequenceGenerator(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }


}
