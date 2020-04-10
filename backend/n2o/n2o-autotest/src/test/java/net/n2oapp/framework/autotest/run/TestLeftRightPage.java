package net.n2oapp.framework.autotest.run;

import net.n2oapp.framework.autotest.api.collection.Regions;
import net.n2oapp.framework.autotest.api.component.header.SimpleHeader;
import net.n2oapp.framework.autotest.api.component.page.LeftRightPage;
import net.n2oapp.framework.autotest.impl.component.N2oComponent;

import static net.n2oapp.framework.autotest.N2oSelenide.collection;

public class TestLeftRightPage extends N2oComponent implements LeftRightPage {

    @Override
    public Regions left() {
        return collection(element().$(".left").$$(".region"), Regions.class);
    }

    @Override
    public Regions right() {
        return collection(element().$(".right").$$(".region"), Regions.class);
    }

    @Override
    public SimpleHeader header() {
        return null;
    }

    @Override
    public PageToolbar toolbar() {
        return null;
    }

    @Override
    public Breadcrumb breadcrumb() {
        return null;
    }

    @Override
    public Dialog dialog(String title) {
        return null;
    }

    @Override
    public Tooltip tooltip() {
        return null;
    }
}
