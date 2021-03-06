package net.n2oapp.framework.config.metadata.pack;

import net.n2oapp.framework.api.pack.MetadataPack;
import net.n2oapp.framework.api.pack.ReadersBuilder;
import net.n2oapp.framework.config.io.region.*;

public class N2oRegionsV1IOPack implements MetadataPack<ReadersBuilder> {
    @Override
    public void build(ReadersBuilder b) {
        b.ios(new NoneRegionIOv1(),
                new LineRegionIOv1(),
                new PanelRegionIOv1(),
                new TabsRegionIOv1(),
                new CustomRegionIOv1());
    }
}
