package net.n2oapp.framework.autotest.impl.component.page;

import com.codeborne.selenide.*;
import net.n2oapp.framework.autotest.N2oSelenide;
import net.n2oapp.framework.autotest.api.collection.Toolbar;
import net.n2oapp.framework.autotest.api.collection.Alerts;
import net.n2oapp.framework.autotest.api.component.header.SimpleHeader;
import net.n2oapp.framework.autotest.api.component.page.Page;
import net.n2oapp.framework.autotest.impl.component.N2oComponent;
import net.n2oapp.framework.autotest.impl.component.header.N2oSimpleHeader;
import org.openqa.selenium.WebElement;

/**
 * Страница для автотестирования
 */
public class N2oPage extends N2oComponent implements Page {

    public SimpleHeader header() {
        return new N2oSimpleHeader(element().$(".n2o-header"));
    }

    @Override
    public PageToolbar toolbar() {
        return new N2oPageToolbar();
    }

    @Override
    public Breadcrumb breadcrumb() {
        return new N2oBreadcrumb(element().$(".breadcrumb"));
    }

    @Override
    public Dialog dialog(String title) {
        return new N2oDialog(element().$$(".modal-dialog").findBy(Condition.text(title)).parent());
    }

    @Override
    public Tooltip tooltip() {
        return new N2oTooltip(element().$(".list-text-cell__tooltip-container, .show.tooltip"));
    }

    @Override
    public Alerts alerts() {
        return N2oSelenide.collection(element().$$(".n2o-alerts .n2o-alert"), Alerts.class);
    }

    @Override
    public void urlShouldMatches(String regexp) {
        element().should(new UrlMatch(regexp));
    }

    @Override
    public void scrollUp() {
        Selenide.executeJavaScript("window.scrollTo(0, 0)");
    }

    @Override
    public void scrollDown() {
        Selenide.executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
    }


    public class N2oPageToolbar implements PageToolbar {

        @Override
        public Toolbar topLeft() {
            return N2oSelenide.collection(element().$$(".n2o-page-body .n2o-page-actions").first().$$(".btn-toolbar:first-child .btn"), Toolbar.class);
        }

        @Override
        public Toolbar topRight() {
            return N2oSelenide.collection(element().$$(".n2o-page-body .n2o-page-actions").first().$$(".btn-toolbar:last-child .btn"), Toolbar.class);
        }

        @Override
        public Toolbar bottomLeft() {
            return N2oSelenide.collection(element().$$(".n2o-page-body .n2o-page-actions").last().$$(".btn-toolbar:first-child .btn"), Toolbar.class);
        }

        @Override
        public Toolbar bottomRight() {
            return N2oSelenide.collection(element().$$(".n2o-page-body .n2o-page-actions").last().$$(".btn-toolbar:last-child .btn"), Toolbar.class);
        }
    }

    public class N2oBreadcrumb extends N2oComponent implements Breadcrumb {

        public N2oBreadcrumb(SelenideElement element) {
            setElement(element);
        }

        @Override
        public void clickLink(String text) {
            element().$$(".n2o-breadcrumb-link").findBy(Condition.text(text)).shouldBe(Condition.exist).click();
        }

        @Override
        public void parentTitleShouldHaveText(String text) {
            element().$(".breadcrumb-item").shouldHave(Condition.text(text));
        }

        @Override
        public void titleShouldHaveText(String text) {
            element().$(".active.breadcrumb-item")
                    .shouldHave(Condition.text(text));
        }
    }

    public static class N2oDialog implements Dialog {
        private final SelenideElement element;

        public N2oDialog(SelenideElement element) {
            this.element = element;
        }

        @Override
        public void shouldBeVisible() {
            element.isDisplayed();
        }

        @Override
        public void shouldHaveText(String text) {
            element.$(".modal-body").shouldHave(Condition.text(text));
        }

        @Override
        public void click(String label) {
            element.$$(".btn").findBy(Condition.text(label)).click();
        }

        @Override
        public void shouldBeClosed(long timeOut) {
            element.$(".modal-header .modal-title").waitWhile(Condition.exist, timeOut);
        }
    }

    public static class N2oTooltip implements Tooltip {
        private final SelenideElement element;

        public N2oTooltip(SelenideElement element) {
            this.element = element;
        }

        @Override
        public void shouldBeExist() {
            element.shouldBe(Condition.exist);
        }

        @Override
        public void shouldNotBeExist() {
            element.shouldNotBe(Condition.exist);
        }

        @Override
        public void shouldBeEmpty() {
            element.shouldBe(Condition.empty);
        }

        @Override
        public void shouldHaveText(String... text) {
            ElementsCollection items = element.$$(".list-text-cell__tooltip-container__body, .tooltip-inner");
            items.shouldHaveSize(text.length);
            if (text.length != 0)
                items.shouldHave(CollectionCondition.texts(text));
        }
    }

    static class UrlMatch extends Condition {
        private final String regex;

        public UrlMatch(String regex) {
            super("urlMatch", true);
            this.regex =regex;
        }

        @Override
        public boolean apply(Driver driver, WebElement element) {
            return driver.url().matches(regex);
        }

        @Override
        public String actualValue(Driver driver, WebElement element) {
            return driver.url();
        }

        @Override
        public String toString() {
            return String.format("%s '%s'", getName(), regex);
        }
    }
}
