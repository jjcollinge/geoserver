package org.geoserver.gwc.web.blob;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.geowebcache.s3.S3BlobStoreConfig;

public class AzureBlobStoreType implements BlobStoreType<AzureBlobStoreConfig> {
    private static final long serialVersionUID = 7349157660150568235L;

    @Override
    public String toString(){
        return "Azure BlobStore";
    }

    @Override
    public AzureBlobStoreConfig newConfigObject() {
        AzureBlobStoreConfig config = new AzureBlobStoreConfig();
        config.setEnabled(true);
        return config;
    }

    @Override
    public Class<AzureBlobStoreConfig> getConfigClass() {
        return AzureBlobStoreConfig.class;
    }

    @Override
    public Panel createPanel(String id, IModel<AzureBlobStoreConfig> model) {
        return new AzureBlobStorePanel(id, model);
    }

}