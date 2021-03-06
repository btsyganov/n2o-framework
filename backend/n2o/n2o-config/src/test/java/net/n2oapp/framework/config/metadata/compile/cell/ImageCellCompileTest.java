package net.n2oapp.framework.config.metadata.compile.cell;

import net.n2oapp.framework.api.metadata.global.view.widget.table.ImageShape;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oImageCell;
import net.n2oapp.framework.api.metadata.meta.action.link.LinkActionImpl;
import net.n2oapp.framework.api.metadata.meta.widget.table.Table;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.io.widget.table.cell.ImageCellElementIOv2;
import net.n2oapp.framework.config.metadata.compile.context.WidgetContext;
import net.n2oapp.framework.config.metadata.pack.N2oActionsPack;
import net.n2oapp.framework.config.metadata.pack.N2oPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oRegionsPack;
import net.n2oapp.framework.config.metadata.pack.N2oWidgetsPack;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Тестирование компиляции ячейки с изображением
 */
public class ImageCellCompileTest extends SourceCompileTestBase {
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oPagesPack(), new N2oRegionsPack(), new N2oWidgetsPack(), new N2oActionsPack());
        builder.ios(new ImageCellElementIOv2());
        builder.compilers(new ImageCellCompiler());
    }

    @Test
    public void testImageCell() {
        Table table = (Table) compile("net/n2oapp/framework/config/metadata/compile/cell/testImageCell.widget.xml")
                .get(new WidgetContext("testImageCell"));

        N2oImageCell cell = (N2oImageCell) table.getComponent().getCells().get(0);
        assertThat(cell.getSrc(), is("ImageCell"));
        assertThat(cell.getUrl(), is("/test"));
        assertThat(cell.getShape(), is(ImageShape.rounded));
        assertThat(cell.getWidth(), is(20));
        assertThat(cell.getProperties().size(), is(1));
        assertThat(cell.getProperties().get("width"), is(30));

        assertThat(cell.getCompiledAction(), instanceOf(LinkActionImpl.class));

        cell = (N2oImageCell) table.getComponent().getCells().get(1);
        assertThat(cell.getWidth(), is(100));
    }
}
