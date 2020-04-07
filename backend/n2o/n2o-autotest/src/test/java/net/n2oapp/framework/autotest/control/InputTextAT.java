package net.n2oapp.framework.autotest.control;

import net.n2oapp.framework.autotest.api.component.control.InputText;
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
 * Автотест компонента ввода текста
 */
public class InputTextAT extends AutoTestBase {

    private SimplePage page;

    @BeforeAll
    public static void beforeClass() {
        configureSelenide();
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/control/input_text/index.page.xml"),
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
    public void testInputText() {
        InputText input = page.single().widget(FormWidget.class).fields().field("InputText")
                .control(InputText.class);
        input.shouldExists();

        input.shouldHavePlaceholder("Введите текст");
        input.shouldBeEmpty();
        input.val("test-value");
        input.shouldHaveValue("test-value");
        // превышение указанной длины
        input.val("test1test2test3");
        input.shouldHaveValue("test1test2");
    }

    @Test
    public void testInputNumber() {
        InputText input = page.single().widget(FormWidget.class).fields().field("InputNumber")
                .control(InputText.class);
        input.shouldExists();

        input.shouldHaveValue("5");
        input.val("10");
        input.shouldHaveValue("10");
        // проверка работы кнопок (+step, -step)
        input.clickPlusStepButton();
        input.shouldHaveValue("12");
        input.clickMinusStepButton();
        input.shouldHaveValue("10");
        // проверка, что значение, измененное кнопками не выходит за границу
        input.val("99");
        input.clickPlusStepButton();
        input.shouldHaveValue("99");
        input.val("-99");
        input.clickMinusStepButton();
        input.shouldHaveValue("-99");
    }

    @Test
    public void testInputFloat() {
        InputText input = page.single().widget(FormWidget.class).fields().field("InputFloat")
                .control(InputText.class);
        input.shouldExists();

        input.val("7.7");
        input.shouldHaveValue("7.7");
        // проверка работы кнопок (+step, -step)
        input.clickPlusStepButton();
        input.shouldHaveValue("8.2");
        input.clickMinusStepButton();
        input.shouldHaveValue("7.7");
        // проверка, что значение, измененное кнопками не выходит за границу
        input.val("9.9");
        input.clickPlusStepButton();
        input.shouldHaveValue("9.9");
        input.val("-9.9");
        input.clickMinusStepButton();
        input.shouldHaveValue("-9.9");
    }
}