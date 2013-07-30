package org.geoserver.importer.bdb;

import java.io.File;


import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.importer.Directory;
import org.geoserver.importer.ImportContext;
import org.geoserver.importer.ImportTask;
import org.geoserver.importer.ImporterTestSupport;

import com.sleepycat.je.DatabaseEntry;

public class SerialVersionSafeSerialBindingTest extends ImporterTestSupport {

    public void testSerialize() throws Exception {
        createH2DataStore("sf", "data");

        Catalog cat = importer.getCatalog();

        WorkspaceInfo ws = cat.getFactory().createWorkspace();
        ws.setName("sf");

        DataStoreInfo ds = cat.getFactory().createDataStore();
        ds.setName("data");

        File dir = unpack("shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(new Directory(dir), ws, ds);

        SerialVersionSafeSerialBinding binding = new SerialVersionSafeSerialBinding();

        DatabaseEntry e = new DatabaseEntry();
        binding.objectToEntry(context, e);
        
        ImportContext context2 = (ImportContext) binding.entryToObject(e);
        context2.reattach(cat, true);

        assertNotNull(context2.getTargetWorkspace().getId());
        assertNotNull(context2.getTargetStore().getId());

        assertEquals(1, context2.getTasks().size());
        ImportTask task = context2.getTasks().get(0);

        assertNotNull(task.getStore());
        assertNotNull(task.getStore().getId());
        assertNotNull(task.getStore().getWorkspace());

        assertNotNull(task.getLayer());
        assertNotNull(task.getLayer().getResource());
        assertNull(task.getLayer().getId());
        assertNull(task.getLayer().getResource().getId());

        assertNotNull(task.getLayer().getResource().getStore());
        assertNotNull(task.getLayer().getResource().getStore().getId());
    }
}
