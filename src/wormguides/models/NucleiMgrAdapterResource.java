/*
 * Bao Lab 2016
 */

package wormguides.models;

import java.util.ListResourceBundle;

import acetree.lineagedata.LineageData;

public class NucleiMgrAdapterResource extends ListResourceBundle {
    private LineageData lineageData;

    public NucleiMgrAdapterResource(LineageData data) {
        this.lineageData = data;
    }

    @Override
	protected Object[][] getContents() {
        return new Object[][]{
                {"lineageData", lineageData}
        };
    }
}