package net.n2oapp.framework.autotest.control;

import net.n2oapp.framework.autotest.api.component.control.InputSelect;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.widget.FormWidget;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.*;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 * Автотест компонента ввода текста с выбором из выпадающего списка
 */
public class InputSelectAT extends AutoTestBase {

    private SimplePage page;

    @BeforeAll
    public static void beforeClass() {
        configureSelenide();
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/control/input_select/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/blank.header.xml"));

        page = open(SimplePage.class);
        page.shouldExists();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oPagesPack(), new N2oHeaderPack(), new N2oWidgetsPack(), new N2oFieldSetsPack(), new N2oControlsPack());
    }

    @Test
    public void testSingle() {
        InputSelect input = page.single().widget(FormWidget.class).fields().field("InputSelect1")
                .control(InputSelect.class);
        input.shouldExists();

        input.shouldBeEmpty();
        input.select(1);
        input.shouldSelected("Two");
        input.clear();
        input.shouldBeEmpty();

        input.val("Three");
        input.shouldHaveValue("Three");
    }

    @Test
    public void testMulti() {
        InputSelect input = page.single().widget(FormWidget.class).fields().field("InputSelect2")
                .control(InputSelect.class);
        input.shouldExists();

        String[] empty = new String[0];
        input.shouldSelectedMulti(empty);
        input.selectMulti(1, 2);
        input.shouldSelectedMulti("Two", "Three");
        input.clear();
        input.shouldSelectedMulti(empty);

        input.selectMulti(2, 1, 0);
        input.shouldSelectedMulti("Three", "Two", "One");

        // удаление выбранных значений
        input.clearItems("Three");
        input.shouldSelectedMulti("Two", "One");
        input.clearItems("Two", "One");
        input.shouldSelectedMulti(empty);
    }

    @Test
    public void testCheckboxes() {
        InputSelect input = page.single().widget(FormWidget.class).fields().field("InputSelect3")
                .control(InputSelect.class);
        input.shouldExists();

        String[] empty = new String[0];
        input.shouldSelectedMulti(empty);
        input.selectMulti(1, 2);
        input.shouldSelectedMulti("Two", "Three");
        input.clear();
        input.shouldSelectedMulti(empty);

        input.selectMulti(2, 1, 0);
        input.shouldSelectedMulti("Three", "Two", "One");

        // удаление выбранных значений
        input.clearItems("Three");
        input.shouldSelectedMulti("Two", "One");
        input.clearItems("Two", "One");
        input.shouldSelectedMulti(empty);
    }
}