package org.iana.test.spring;

import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.testng.annotations.*;

/**
 * @author Jakub Laszkiewicz
 */
public abstract class TransactionalSpringContextTests extends AbstractTransactionalSpringContextTests {
    private String configFileName;

    protected TransactionalSpringContextTests(String configFileName) {
        this.configFileName = configFileName;
    }

    protected abstract void init() throws Exception;

    protected abstract void cleanUp() throws Exception;

    @BeforeClass
    protected final void beforeTests() throws Exception {
        setPopulateProtectedVariables(true);
        setDefaultRollback(false);
        super.setUp();
        try {
            init();
        } finally {
            after();
        }
    }

    @AfterClass(alwaysRun = true)
    protected final void afterTests() throws Exception {
        before();
        try {
            cleanUp();
        } finally {
            super.tearDown();
        }
    }

    @BeforeMethod
    protected final void before() throws Exception {
        super.startNewTransaction();
    }

    @AfterMethod
    protected final void after() throws Exception {
        super.endTransaction();
    }

    @Override
    protected final String[] getConfigLocations() {
        return new String[]{configFileName};
    }
}
