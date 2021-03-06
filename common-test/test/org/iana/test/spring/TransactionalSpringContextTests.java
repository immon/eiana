package org.iana.test.spring;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * @author Jakub Laszkiewicz
 */
public abstract class TransactionalSpringContextTests extends AbstractTransactionalSpringContextTests {
    private String configFileName;
    private MockHttpServletRequest request;

    protected TransactionalSpringContextTests(String configFileName) {
        this.configFileName = configFileName;
        request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());
    }

    protected abstract void init() throws Exception;

    protected abstract void cleanUp() throws Exception;

    @Override
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        getApplicationContext().getBeanFactory().registerScope("request", new RequestScope());
        getApplicationContext().getBeanFactory().registerScope("session", new RequestScope());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

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
