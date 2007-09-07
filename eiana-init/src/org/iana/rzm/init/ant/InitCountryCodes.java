package org.iana.rzm.init.ant;

import org.iana.codevalues.Value;
import org.iana.codevalues.Code;
import org.iana.codevalues.HibernateCodeValuesRetriever;
import org.iana.codevalues.CodeValuesRetriever;
import org.springframework.context.ApplicationContext;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
public class InitCountryCodes {

    public static void main(String[] args) {
        try {
            List<Value> values = new ArrayList<Value>();
            InputStream is = InitCountryCodes.class.getClassLoader().getResourceAsStream("iso3166countries.properties");
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
            ApplicationContext context = SpringInitContext.getContext();
            CodeValuesRetriever dao = (CodeValuesRetriever) context.getBean("codeValues");
            Code code = dao.getCode("cc");
            if (code != null) dao.delete(code);
            dao.create(new Code("cc", values));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
