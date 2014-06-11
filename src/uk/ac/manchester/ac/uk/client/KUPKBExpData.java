package uk.ac.manchester.ac.uk.client;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Author: Simon Jupp<br>
 * Date: May 2, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class KUPKBExpData implements Serializable{

    private String experimentID;

    private String geneSymbol;

    private String experimentType;

    private String species;

    private String experimentDesc;

    private String pmid;

    private String experimentDisplayLabel;

    private String controlAnatomy;

    private String controlDisease;

    private String analyteAnatomy;

    private String analyteAnatomyURI;

    private String analyteDisease;

    private String maturity;

    private String severity;

    private String deviationFromNormal;

    private String pValue;

    private String foldChange;

    private String analyteDiseaseURI;

    private String geneid;

    private String expression;

    private String seeAlsoLink;

    private String[] goAnnotations;

    private String[] orthologs;

    public String[] getOrthologs() {
        return orthologs;
    }

    public void setOrthologs(String[] orthologs) {
        this.orthologs = orthologs;
    }

    public String getExpressionURI() {
        return expressionURI;
    }

    public void setExpressionURI(String expressionURI) {
        this.expressionURI = expressionURI;
    }

    private String expressionURI;

    public KUPKBExpData() {

    }

    public String[] getGoAnnotations() {
        return goAnnotations;
    }

    public void setGoAnnotations(String[] goAnnotations) {
        this.goAnnotations = goAnnotations;
    }

    public String getpValue() {
        return pValue;
    }

    public void setpValue(String pValue) {
        this.pValue = pValue;
    }

    public String getDeviationFromNormal() {
        return deviationFromNormal;
    }

    public void setDeviationFromNormal(String deviationFromNormal) {
        this.deviationFromNormal = deviationFromNormal;
    }

    public String getPValue() {
        return pValue;
    }

    public void setPValue(String pValue) {
        this.pValue = pValue;
    }

    public String getFoldChange() {
        return foldChange;
    }

    public void setFoldChange(String foldChange) {
        this.foldChange = foldChange;
    }


    public String getSeeAlsoLink() {
        return seeAlsoLink;
    }

    public String getAnalyteAnatomyURI() {
        return analyteAnatomyURI;
    }

    public void setAnalyteAnatomyURI(String analyteAnatomyURI) {
        this.analyteAnatomyURI = analyteAnatomyURI;
    }

    public String getAnalyteDiseaseURI() {
        return analyteDiseaseURI;
    }

    public void setAnalyteDiseaseURI(String analyteDiseaseURI) {
        this.analyteDiseaseURI = analyteDiseaseURI;
    }

    public String getExperimentID() {
        return experimentID;
    }

    public void setExperimentID(String experimentID) {
        this.experimentID = experimentID;
    }

    public String getExperimentDesc() {
        return experimentDesc;
    }

    public void setExperimentDesc(String experimentDesc) {
        this.experimentDesc = experimentDesc;
    }

    public String getControlAnatomy() {
        return controlAnatomy;
    }

    public void setControlAnatomy(String controlAnatomy) {
        this.controlAnatomy = controlAnatomy;
    }

    public String getControlDisease() {
        return controlDisease;
    }

    public void setControlDisease(String controlDisease) {
        this.controlDisease = controlDisease;
    }

    public String getAnalyteAnatomy() {
        return analyteAnatomy;
    }

    public void setAnalyteAnatomy(String analyteAnatomy) {
        this.analyteAnatomy = analyteAnatomy;
    }

    public String getAnalyteDisease() {
        return analyteDisease;
    }

    public void setAnalyteDisease(String analyteDisease) {
        this.analyteDisease = analyteDisease;
    }

    public String getGeneid() {
        return geneid;
    }

    public void setGeneid(String geneid) {
        this.geneid = geneid;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }


    public String getExperimentType() {
        return experimentType;
    }

    public void setExperimentType(String experimentType) {
        this.experimentType = experimentType;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public void setExperimentDisplayLabel(String s) {
        this.experimentDisplayLabel = s;
    }

    public String getExperimentDisplayLabel() {
        return experimentDisplayLabel;
    }


    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getPmid () {
        return pmid;
    }

    public void setSeeAlso(String link) {
        this.seeAlsoLink = link;
    }

    public void setMaturity(String label) {
        this.maturity = label;
    }

    public void setSeverity(String label) {
        this.severity = label;
    }

    public String getMaturity() {
        return maturity;
    }

    public String getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return "KUPKBExpData{" +
                "experimentID='" + experimentID + '\'' +
                ", geneSymbol='" + geneSymbol + '\'' +
                ", experimentType='" + experimentType + '\'' +
                ", species='" + species + '\'' +
                ", experimentDesc='" + experimentDesc + '\'' +
                ", pmid='" + pmid + '\'' +
                ", experimentDisplayLabel='" + experimentDisplayLabel + '\'' +
                ", controlAnatomy='" + controlAnatomy + '\'' +
                ", controlDisease='" + controlDisease + '\'' +
                ", analyteAnatomy='" + analyteAnatomy + '\'' +
                ", analyteAnatomyURI='" + analyteAnatomyURI + '\'' +
                ", analyteDisease='" + analyteDisease + '\'' +
                ", maturity='" + maturity + '\'' +
                ", severity='" + severity + '\'' +
                ", deviationFromNormal='" + deviationFromNormal + '\'' +
                ", pValue='" + pValue + '\'' +
                ", foldChange='" + foldChange + '\'' +
                ", analyteDiseaseURI='" + analyteDiseaseURI + '\'' +
                ", geneid='" + geneid + '\'' +
                ", expression='" + expression + '\'' +
                ", seeAlsoLink='" + seeAlsoLink + '\'' +
                ", goAnnotations=" + (goAnnotations == null ? null : Arrays.asList(goAnnotations)) +
                '}';
    }
}
