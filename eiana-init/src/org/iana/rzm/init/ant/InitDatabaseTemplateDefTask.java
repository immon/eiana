package org.iana.rzm.init.ant;

import org.hibernate.Session;
import org.iana.notifications.template.def.TemplateDef;
import org.iana.notifications.template.def.TemplateDefConfig;
import org.iana.notifications.template.def.xml.XMLTemplateDefLoader;
import org.iana.notifications.template.pgp.PgpKey;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class InitDatabaseTemplateDefTask extends HibernateTask {

    public void doExecute(Session session) throws Exception {

        PgpKey defaultKey = new PgpKey("defaultKey",
                "-----BEGIN PGP PRIVATE KEY BLOCK-----\n" +
                "Version: GnuPG v1.4.7 (MingW32)\n" +
                "\n" +
                "lQHhBEZEHakRBACVGdhNG6Mhyjv2oKvXA9Ds+NG+XrxO9tNYqA7qtJZusxzcI1Z+\n" +
                "oYTrK7qsig06ORiOxcYVhTIeTrRNFC42Mnw/NffepMIzyi7pM1UZpMjXc05e+UjU\n" +
                "eRoZHsFu/75iYWj9OBITqPxJfWDtnq9Ym/6jab0xpGXfWg89rJylm9l4fwCg7r2X\n" +
                "/9XMmCRDP37uTtPu4qHeAIMD/iAxrESBDihsmTyCxLwy94n1H6xGdN+SDku3qTCW\n" +
                "+GuP3hAkzYBpbLTptxwALwANMPK4dNWJStgM8dB+uwpwwlXHRk31zSXZo6sjPUQh\n" +
                "O23x3LVnfKJd73nSd4bGJ8C+x2m6xfbExcb8s/c25SBqJH3ccjOUuIBDhyTeuiFQ\n" +
                "K2lvA/4n4k6Q6fJd5Zjq+daL5PqL22Yn4+3x7BLIVCDp+OETY70pwUCR474yMxkN\n" +
                "8RgOQOSI0oB0me8y9DLrszFq4mbnD08AtJDiVkL6fx9OXSLBz3+7GDUSy5qhn5L9\n" +
                "RXq8WBHv+y21nzT7p2JwMxgxOhv1tQvsdpSUZufx+sGpx9eYwP4DAwKumZxN6Yj0\n" +
                "q2DA35gUYZrEFrVGH51Z9SjQ9Y5ScG7fX5nJWWqWe3fFuVj7bnG5V+MUXNKy5ZBB\n" +
                "0gCEsbQYVGVzdGVyIDx0ZXN0ZXJAaWFuYS5vcmc+iGAEExECACAFAkZEHakCGwMG\n" +
                "CwkIBwMCBBUCCAMEFgIDAQIeAQIXgAAKCRBHLW1HQP6WTH3IAKC0vv9ycrG3elzA\n" +
                "+QNU5Ou++JQMpQCguZwj5ockoa6W+UT3wruNSbUalNGdAmMERkQdqRAIAJJ1xfgU\n" +
                "n+d6C35CrJHdKgUuTsFq5E8zMuXmSG1FH5/OXDajGML/rVMA8IId2+4xnNfUFXjo\n" +
                "pAYw0E+j6N+XVpQ4lx4Fng1RohQpcM9cxDScenE35xge43Ucxw6NDO+Wl5Y3iyLm\n" +
                "lnBk9xYNxjRdnSj2aUBkQAZyzqhhyb51dsNj11duwtMS/8AVukJKl94p1ta5Bex2\n" +
                "ff+FxkHviOnGwT1qDxJyX0BOzrB85GjuNJlrjODHyHHoAJeygAgteoxoj2HiZUxS\n" +
                "6AJCBpHnBhoYLqwWJ3a8pEzyyat+BnyBieLiGLF2Rkl4xXMJJHNNGWVVNvX9mHzS\n" +
                "D6rM7RQBLR4j2LcAAwUH/27WbnGmlwYRwqM0s4SlnuPNYHiugeVrqQjfUj3Lc8qL\n" +
                "8lRK+XGv6Y3xj/MlnD4frBwRFuXyW+sDCQMdHieEbBrWQxhJPszaxpHqB+XgLvUb\n" +
                "f27YfjyqjGOR/SuUdhbcPfswSga2bUaHwzW6y+1wzszSDs1ZpV2LQy4hMSyKX5mL\n" +
                "l8MmNIBbOE6oLVMD0zgxLSDawpe942SxS1yZi79Ekrf9wIUOlZrJoEcOmk9BNUh7\n" +
                "ixhW+xVY1wwQQR64A+V/auetr2Tnq3FJqujivG4PgrgD5vtUvjVYsFPUjVWTFz0h\n" +
                "bV34f6C6Z074W/lPBfA8Zi/yyZ9ls0fF6AriKvZ5zo7+AwMCrpmcTemI9KtgWjnb\n" +
                "XAFJwalyKqNFJ/6FkwPC1lGnMSkYXUqubnf8fPO5D5NKgIYxeuMb99ZTb1nsewuD\n" +
                "c2RAF4cYNgWSrpZVSkx5YByA+P0Tt4hJBBgRAgAJBQJGRB2pAhsMAAoJEEctbUdA\n" +
                "/pZMNq4AnjbgTomHb/kE70c9kq1nGjrlOjsCAKDgHKLy1nseC6rU5wKTFkDIvwKn\n" +
                "Qw==\n" +
                "=sf9+\n" +
                "-----END PGP PRIVATE KEY BLOCK-----",
                "tester");

        session.save(defaultKey);

        TemplateDefConfig templatesDefConfig = new XMLTemplateDefLoader("iana-templates.properties", "templates.xml");
        List<TemplateDef> templateDefs = templatesDefConfig.getTemplateDefs();

        for (TemplateDef templateDef : templateDefs) {
            session.save(templateDef);
        }
    }

    public static void main(String[] args) {
        InitDatabaseTemplateDefTask task = new InitDatabaseTemplateDefTask();
        task.setAnnotationConfiguration("hibernate.cfg.xml");
        task.execute();
    }
}
