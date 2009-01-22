package org.iana.rzm.init.ant;

import org.iana.rzm.user.*;
import org.jdom.*;
import org.jdom.input.*;
import org.springframework.context.*;
import org.springframework.context.support.*;

import java.io.*;
import java.util.*;

public class IanaTesters {

    private List<IanaTester>testers;

    public static void main(String[] args)throws Exception{
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("services-config.xml");
        IanaTesters testers = new IanaTesters();
        testers.doExecute(context);
    }

    public void doExecute(ApplicationContext context) throws Exception {
        Reader testers = loadFile();
        List<IanaTester> list = getTesters(testers);
        UserManager userManager = (UserManager) context.getBean("userManager");
        for (IanaTester tester : list) {
            RZMUser user =
                new RZMUser(
                    tester.getFirst(),
                            tester.getLast(),
                            null,
                            tester.getUserName(),
                            tester.getEmail(),
                            tester.getUserName(),
                            false);
            List<TesterRole> roles = tester.getRoles();
            for (TesterRole role : roles) {
                SystemRole.SystemType type =
                    role.getType().equals("ac") ? SystemRole.SystemType.AC : SystemRole.SystemType.TC;
                user.addRole(new SystemRole(type, role.getDomain(), true, false));
            }
            userManager.create(user);
        }
    }


    @SuppressWarnings("unchecked")
    public List<IanaTester> getTesters(Reader reader) {
        if(testers != null){
            return testers;
        }

        SAXBuilder builder = new SAXBuilder();

        try {
            Document document = builder.build(reader);
            Element root = document.getRootElement();
            List<Element> testers = root.getChild("testers").getChildren("user");
            List<IanaTester> restult = new ArrayList<IanaTester>();
            for (Element tester : testers) {
                IanaTester user = new IanaTester(tester);
                restult.add(user);
            }
            this.testers = restult;
            return restult;
        } catch (JDOMException e) {
           throw new RuntimeException(e);
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    public Reader loadFile() throws UnsupportedEncodingException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("iana-testers.xml");
        if (in != null) {
            return new InputStreamReader(in, "UTF-8");
        }
        return null;
    }

    public List<IanaTester> getTestersForDomain(String name) throws UnsupportedEncodingException {
        List<IanaTester> list = getTesters(loadFile());
        List<IanaTester> result = new ArrayList<IanaTester>();
        for (IanaTester ianaTester : list) {
            for (TesterRole role : ianaTester.getRoles()) {
                if(role.getDomain().equals(name)){
                    result.add(ianaTester);
                }
            }
        }
        return result;
    }

}
