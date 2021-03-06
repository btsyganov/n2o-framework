package net.n2oapp.framework.config.io.control.v2;

import net.n2oapp.framework.config.io.control.plain.HtmlIOv2;
import net.n2oapp.framework.config.io.widget.form.FormElementIOV4;
import net.n2oapp.framework.config.reader.control.N2oStandardControlReaderTestBase;
import net.n2oapp.framework.config.selective.ION2oMetadataTester;
import org.junit.Test;

public class N2oHtmlXmlIOv2Test extends N2oStandardControlReaderTestBase {

    @Test
    public void test() {
        ION2oMetadataTester tester = new ION2oMetadataTester();
        tester.ios(new HtmlIOv2(), new FormElementIOV4());
        assert tester.check("net/n2oapp/framework/config/io/control/v2/testHtml.widget.xml");
    }
}