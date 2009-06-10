package org.iana.rzm.init.ant;

import org.iana.codevalues.*;
import org.iana.rzm.facade.system.domain.types.*;
import org.springframework.context.*;

import java.io.*;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class InitCodeValues {

    public static void main(String[] args) {
        ApplicationContext context = SpringInitContext.getContext();
        initCountryCodes(context);
        initDomainTypes(context);
    }

    static void initCountryCodes(ApplicationContext context) {
        try {
            List<Value> values = new ArrayList<Value>();
            InputStream is = InitCodeValues.class.getClassLoader().getResourceAsStream("iso3166countries.properties");
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                int space = line.indexOf(' ');
                if (space > -1) {
                    String countryCode = line.substring(0, space).trim();
                    String countryName = line.substring(space + 1).trim();
                    values.add(new Value(countryCode, countryName));
                }
            }
            CodeValuesManager dao = (CodeValuesManager) context.getBean("codeValues");
            Code code = dao.getCode("cc");
            if (code != null) dao.delete(code);
            dao.create(new Code("cc", values));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void initDomainTypes(ApplicationContext context) {
        List<Value> values = new ArrayList<Value>();
        values.add(new Value("cc", "country-code"));
        values.add(new Value("sp", "sponsored"));
        values.add(new Value("inf", "infrastructure"));
        values.add(new Value("gen", "generic"));
        values.add(new Value("gen-rest", "generic-restricted"));
        CodeValuesManager dao = (CodeValuesManager) context.getBean("codeValues");
        Code code = dao.getCode(DomainTypesImpl.CODE_DT);
        if (code != null) dao.delete(code);
        dao.create(new Code(DomainTypesImpl.CODE_DT, values));
    }
}
