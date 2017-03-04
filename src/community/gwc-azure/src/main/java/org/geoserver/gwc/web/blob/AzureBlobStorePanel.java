/* (c) 2015 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.blob;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.geowebcache.azure.AzureBlobStoreConfig;

public class AzureBlobStorePanel extends Panel {

    private static final long serialVersionUID = -8237328668463257329L;

    public AzureBlobStorePanel(String id, final IModel<AzureBlobStoreConfig> configModel) {
        super(id, configModel);

        add(new TextField<String>("blobContainer").setRequired(true).add(
                new AttributeModifier("title", new ResourceModel("blobContainer.title"))));
        add(new TextField<String>("azureAccountKey").setRequired(true).add(
                new AttributeModifier("title", new ResourceModel("azureAccountKey.title"))));
        add(new TextField<String>("azureAccountName").setRequired(true).add(
                new AttributeModifier("title", new ResourceModel("azureAccountName.title"))));
        add(new TextField<String>("prefix").add(new AttributeModifier("title", new ResourceModel("prefix.title"))));
    }

}
