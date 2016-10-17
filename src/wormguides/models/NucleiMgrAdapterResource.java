/*
 * Bao Lab 2016
 */

package wormguides.models;

import java.util.ListResourceBundle;

import acetree.LineageData;

public class NucleiMgrAdapterResource extends ListResourceBundle {

    private final LineageData lineageData;

    public NucleiMgrAdapterResource(final LineageData lineageData) {
        this.lineageData = lineageData;
    }

    @Override
    protected Object[][] getContents() {
        return new Object[][]{{"lineageData", lineageData}};
    }
}