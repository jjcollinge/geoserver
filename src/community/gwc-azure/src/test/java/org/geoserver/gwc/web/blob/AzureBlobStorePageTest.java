/* (c) 2015 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.blob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.gwc.GWC;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geowebcache.config.BlobStoreConfig;
import org.geowebcache.config.ConfigurationException;
import org.geowebcache.config.FileBlobStoreConfig;
import org.geowebcache.layer.TileLayer;
import org.geowebcache.azure.AzureBlobStoreConfig;
import org.junit.Test;

public class AzureBlobStorePageTest extends GeoServerWicketTestSupport {
        
    
    @Test
    public void testPage() {
        BlobStorePage page = new BlobStorePage();

        tester.startPage(page);
        tester.assertRenderedPage(BlobStorePage.class);
        
        tester.assertComponent("selector", Form.class);
        tester.assertComponent("selector:typeOfBlobStore", DropDownChoice.class);
        tester.assertComponent("blobConfigContainer", MarkupContainer.class);
        
        tester.assertInvisible("blobConfigContainer:blobStoreForm");
        
        DropDownChoice typeOfBlobStore = (DropDownChoice) tester.getComponentFromLastRenderedPage("selector:typeOfBlobStore");
        assertEquals(2, typeOfBlobStore.getChoices().size());
        assertEquals("Azure BlobStore", typeOfBlobStore.getChoices().get(0).toString());
        assertEquals("File BlobStore", typeOfBlobStore.getChoices().get(1).toString());
        
        executeAjaxEventBehavior("selector:typeOfBlobStore", "change", "1");

        tester.assertComponent("blobConfigContainer:blobStoreForm:blobSpecificPanel", FileBlobStorePanel.class);
        tester.assertVisible("blobConfigContainer:blobStoreForm");
        
        executeAjaxEventBehavior("selector:typeOfBlobStore", "change", "0");
        tester.assertComponent("blobConfigContainer:blobStoreForm:blobSpecificPanel", AzureBlobStorePanel.class);
    }   
    
    @Test
    public void testNew() throws ConfigurationException {
        BlobStorePage page = new BlobStorePage();

        tester.startPage(page);
        executeAjaxEventBehavior("selector:typeOfBlobStore", "change", "0");
        
        FormTester formTester = tester.newFormTester("blobConfigContainer:blobStoreForm");
        formTester.setValue("id", "myblobstore");       
        formTester.setValue("enabled", false);
        formTester.setValue("blobSpecificPanel:blobContainer", "mycontainer");
        formTester.setValue("blobSpecificPanel:azureAccountName", "myaccountname");
        formTester.setValue("blobSpecificPanel:azureAccountKey", "myaccountkey");
        tester.executeAjaxEvent("blobConfigContainer:blobStoreForm:save", "click");
        
        List<BlobStoreConfig> blobStores = GWC.get().getBlobStores();
        BlobStoreConfig config = blobStores.get(0);
        assertTrue(config instanceof AzureBlobStoreConfig);
        assertEquals("myblobstore", config.getId());
        assertEquals("mycontainer", ((AzureBlobStoreConfig) config).getContainer());
        assertEquals("myaccountname", ((AzureBlobStoreConfig) config).getAzureAccountName());
        assertEquals("myaccountkey", ((AzureBlobStoreConfig) config).getAzureAccountKey());
        
        GWC.get().removeBlobStores(Collections.singleton("myblobstore"));
    }
    
    @Test
    public void testModify() throws Exception {
        AzureBlobStoreConfig sconfig = new AzureBlobStoreConfig();
        Field id = BlobStoreConfig.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(sconfig, "myblobstore");
        sconfig.setContainer("mycontainer");
        sconfig.setAzureAccountName("myaccountname");
        sconfig.setAzureAccountKey("myaccountkey");
        GWC.get().addBlobStore(sconfig);
        TileLayer layer = GWC.get().getTileLayerByName("cite:Lakes");
        layer.setBlobStoreId("myblobstore");
        GWC.get().save(layer);
        
        BlobStorePage page = new BlobStorePage(sconfig);

        tester.startPage(page);   
        tester.assertVisible("blobConfigContainer:blobStoreForm");        
        tester.assertComponent("blobConfigContainer:blobStoreForm:blobSpecificPanel", AzureBlobStorePanel.class);
        
        FormTester formTester = tester.newFormTester("blobConfigContainer:blobStoreForm");
        formTester.setValue("id", "yourblobstore");
        formTester.setValue("blobSpecificPanel:blobContainer", "yourcontainer");
        formTester.submit();
        tester.executeAjaxEvent("blobConfigContainer:blobStoreForm:save", "click");
        
        BlobStoreConfig config = GWC.get().getBlobStores().get(0);
        assertTrue(config instanceof AzureBlobStoreConfig);
        assertEquals("yourblobstore", config.getId());
        assertEquals("yourcontainer", ((AzureBlobStoreConfig) config).getContainer());
                
        //test updated id!
        layer = GWC.get().getTileLayerByName("cite:Lakes");
        assertEquals("yourblobstore", layer.getBlobStoreId());
        
        GWC.get().removeBlobStores(Collections.singleton("yourblobstore"));
    }
    
}
