/*
 * Bao Lab 2016
 */

package wormguides.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import static search.SearchUtil.getCellsWithGeneExpression;

/**
 * Service that returns the cells with a certain gene expression. This is a wrapper for the actual HTTP request method.
 */
public class GeneSearchService extends Service<List<String>> {

    /** Map of previously fetched genes (in lower case) to their results */
    private final Map<String, List<String>> geneResultsCache;

    /** The searched gene */
    private String searchedGene;

    /**
     * Constructor
     */
    public GeneSearchService() {
        geneResultsCache = new HashMap<>();
        searchedGene = "";
    }

    public void initTestDB() {
        // TODO remove method
        final String pha4ResultsString = "ABala ABalaa ABalaaaa ABalaaap ABalaapa ABalaapp ABalap ABalapa ABalapaa "
                + "ABalapaaa ABalapap ABalapapa ABalappa ABalappaa ABalappap ABalappp ABalapppa ABalapppp ABalp "
                + "ABalpa ABalpapa ABalpapaa ABalpapaaa ABalpapap ABalpp ABalppaa ABalppaaa ABalppap ABalppapa "
                + "ABalppapp ABalpppaaa ABalpppapp ABara ABaraa ABaraaaa ABaraaap ABaraaapp ABaraaappa ABaraaappp "
                + "ABaraapa ABaraapaa ABaraapp ABaraappa ABaraappp ABarap ABarapaa ABarapaaa ABarapaap ABarapap "
                + "ABarapapa ABarappa ABarappp ABarp ABarpa ABarpaaa ABarpapa ABarpapp ABarpappa ABarpp ABarppaa "
                + "ABarppap ABarpppa ABarpppp ABpla ABplaa ABplaaaa ABplaaap ABplaaapa ABplaapa ABplaapaa ABplaapap "
                + "ABplaapp ABplaappa ABplaappaa ABplaappp ABplap ABplapaa ABplapaaa ABplapaap ABplapap ABplapapa "
                + "ABplapapp ABplappa ABplappp ABplapppa ABplp ABplpa ABplpaaa ABplpaaaa ABplpaaap ABplpaap ABplpaapa"
                + " ABplpaapp ABplpappa ABplpappp ABplpp ABplppaa ABplppaap ABplppap ABplppapa ABplppapp ABplpppaa "
                + "ABpra ABpraa ABpraaaa ABpraaaaa ABpraaap ABpraaapap ABpraapa ABpraapaa ABpraapap ABpraapapa "
                + "ABpraapapp ABpraapp ABpraappaa ABprap ABprapaa ABprapaaaa ABprapaaap ABprapaap ABprapap ABprapapa "
                + "ABprapappp ABprappa ABprappp ABprapppa ABprapppp ABprp ABprpa ABprpaaa ABprpaaaa ABprpaaap "
                + "ABprpaap ABprpaapa ABprpaapp ABprpapap ABprpappa ABprpappp ABprpapppp ABprpp ABprppaa ABprppaap "
                + "ABprppap ABprppapa ABprppapp ABprpppaa ABprpppapa ABprppppp MSaapap MSpaap MSpapa MSpapap MSppaa "
                + "MSppaap MSppap MSpppa MSpppp P11 P12 P9/10L neuroblast";
        final List<String> pha4Results = new ArrayList<>(asList(pha4ResultsString.split(" ")));
        geneResultsCache.put("pha-4", pha4Results);

        final String cnd1String = "ABalaaaa ABalaapp ABalpaa ABalpaaa ABalpaaaa ABalpaaap ABalpaap ABalpaapa "
                + "ABalpaapp ABalpap ABalpapa ABalpapaa ABalpapap ABalpapp ABalpappa ABalpappp ABalppaap ABaraaa "
                + "ABaraaaa ABaraaaaa ABaraaaap ABaraaap ABaraaapa ABaraaapaa ABaraaapap ABaraaapp ABaraaappa "
                + "ABaraaappp ABaraap ABaraapa ABaraapaa ABaraapaaa ABaraapap ABaraapp ABaraappa ABaraappp ABarapaa "
                + "ABarapaaa ABarapaaaa ABarapaaap ABarapaap ABarapaapa ABarapaapp ABarapap ABarapapa ABarapapaa "
                + "ABarapapap ABarapapp ABarapappa ABarapappp ABarpaapaa ABplaaaapa ABplaapppa ABplpaaaa ABplpaaap "
                + "ABplpaapa ABplpaapp ABplpap ABplpappp ABplpapppa ABplpapppp ABplppaaa ABplppaap ABplppapa "
                + "ABplpppap ABpraaaaa ABpraappa ABpraappp ABprappaa ABprappap ABprpaaap ABprpaapa ABprpapp "
                + "ABprpapppa ABprpapppp ABprppap ABprpppppaa AVG Caaaa Caaap Caap Capp Cpapa Cpapp Cpppa Eal Eala "
                + "Ealp Ear Eara Earp Epl Epla Eplp Epr Epra Eprp MSaaa MSaaaa MSaaaaa MSaaaap MSaaap MSaaapa MSaaapp"
                + " MSaap MSaapa MSaapaa MSaapap MSaapp MSaappa MSaappp MSapa MSapaa MSapap MSapp MSappa MSappp MSpaa"
                + " MSpaaa MSpaaaa MSpaaaaa MSpaaap MSpaap MSpaapa MSpaapp MSpap MSpapa MSpapaa MSpapap MSpapp "
                + "MSpappa MSpappp MSppa MSppaa MSppap MSppp MSpppp PVT RIR gon_herm_dtc_A gon_herm_dtc_P gonad head "
                + "intestine mu_int_L oocyte pharynx rect_D rectum tail virL virR";
        final List<String> cnd1Results = new ArrayList<>(asList(cnd1String.split(" ")));
        geneResultsCache.put("cnd-1", cnd1Results);

        final String nhr25String = "ABalappppp ABarpaap ABarpaapa ABarpaapp ABarpap ABarpapapa ABarpapapp ABarpappa "
                + "ABarpappp ABarppaa ABarppaaa ABarppaap ABarppap ABarppapa ABarppapp ABarppp ABarpppaa ABarpppap "
                + "ABarppppa ABarppppp ABplaaaa ABplaaaapa ABplaaaapp ABplaaap ABplaaapa ABplaaapp ABplaap ABplaappa "
                + "ABplaappp ABplapaa ABplapaapp ABplapap ABplapapa ABplapapp ABplapp ABplappaa ABplappap ABplapppa "
                + "ABplapppp ABplpppap ABplpppapa ABplpppapp ABpraappa ABpraappp ABprapaap ABprapapa ABprapapp "
                + "ABprappaa ABprappap ABprappp ABprapppa ABprapppp ABprpppapa Caaaa Caaap Caapa Caapp Cpaaa Cpaap "
                + "Cpapa Cpapp G2 P1 P1.p P10 P10.p P11 P11.p P12 P12.p P2 P2.p P3 P4 P5 P6 P7 P8 P9 P9.p V1L V1R V2L"
                + " V2R V3L V3R V4L V4R V5L V5R V6L V6R Z1 Z1.p Z4 Z4.a gonad hyp1 hyp10 hyp11 hyp12 hyp2 hyp3 hyp4 "
                + "hyp5 hyp6 hyp8 hyp9 hypodermis intestine neuroblast pharynx tail";
        final List<String> nhr25Results = new ArrayList<>(asList(nhr25String.split(" ")));
        geneResultsCache.put("nhr-25", nhr25Results);
    }

    /**
     * Retrieves the cells with the specified gene expression if the results were previously fetched
     *
     * @param gene
     *         the gene
     *
     * @return cells with that gene expression
     */
    public List<String> getResultsIfPreviouslyFetched(String gene) {
        gene = gene.trim().toLowerCase();
        if (geneResultsCache.containsKey(gene)) {
            return geneResultsCache.get(gene);
        }
        return new ArrayList<>();
    }

    /**
     * Resets the searched gene to an empty string
     */
    public void resetSearchedGene() {
        searchedGene = "";
    }

    /**
     * @return the previously searched gene
     */
    public String getSearchedGene() {
        return searchedGene;
    }

    /**
     * Issues a gene search for the specified gene
     *
     * @param searchedGene
     *         the gene to search, non null, non empty, and in the gene format SOME_STRING-SOME_NUMBER
     */
    public void setSearchedGene(final String searchedGene) {
        if (!requireNonNull(searchedGene).isEmpty()) {
            this.searchedGene = searchedGene.toLowerCase();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return cells with the searched gene expression
     */
    @Override
    protected final Task<List<String>> createTask() {
        return new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                if (geneResultsCache.containsKey(searchedGene)) {
                    return geneResultsCache.get(searchedGene);
                }
                final List<String> results = getCellsWithGeneExpression(searchedGene);
                // save results in cache
                geneResultsCache.put(searchedGene, results);
                return results;
            }
        };
    }
}