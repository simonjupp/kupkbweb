package uk.ac.manchester.ac.uk.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.lang.StringUtils;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import uk.ac.manchester.ac.uk.client.DAGTreeModel;
import uk.ac.manchester.ac.uk.client.KUPKBOWLQuery;
import uk.ac.manchester.ac.uk.client.MyTreeNode;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;/*
 * Copyright (C) 2010, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Simon Jupp<br>
 * Date: May 10, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class KUPKBOWLQueryImpl extends RemoteServiceServlet implements KUPKBOWLQuery {

    private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

    private OWLDataFactory factory = manager.getOWLDataFactory();

    private static String PART_OF = "http://purl.org/obo/owl/OBO_REL#part_of";
    private static String MIMICS = "http://www.kupkb.org/data/kupo/mimics";

    // create the three branches for cell, anatomy and disease
    private MyTreeNode cellRoot = new MyTreeNode("http://www.kupkb.org/data/kupo/IKUP_0000001", "Cell");
    private MyTreeNode anatomyRoot = new MyTreeNode("http://www.kupkb.org/data/kupo/IKUP_0000002", "Anatomy");
    private MyTreeNode diseaseRoot = new MyTreeNode("http://www.kupkb.org/data/kupo/IKUP_0000003", "Disease/Model");
    private MyTreeNode bpRoot = new MyTreeNode("http://www.kupkb.org/data/kupo/IKUP_0000004", "Biological process");
    private MyTreeNode unknownRoot = new MyTreeNode("http://www.kupkb.org/data/kupo/unkown", "Other");

    private Reasoner hermitReasoner;

    private Map<IRI, MyTreeNode> treeNodes;

    private Set<OWLClass> anatomyFilterSet = new HashSet<OWLClass>();
    private Set<OWLClass> cellFilterSet = new HashSet<OWLClass>();
    private Set<OWLClass> diseaseFilterSet = new HashSet<OWLClass>();
    private Set<OWLClass> bpFilterSet = new HashSet<OWLClass>();


    public KUPKBOWLQueryImpl () {

        System.setProperty("entityExpansionLimit", "100000000");
        try {
            // for testing locally
//            OWLOntology kupo = manager.loadOntology(IRI.create("file:/Users/simon/Documents/e-lico/svn/trunk/Public/kupo/kupo-core.owl"));
//
//            manager.loadOntology(IRI.create("file:/Users/jupp/Documents/e-lico/svn/trunk/Public/kupo/imports/mao.owl"));
//            manager.loadOntology(IRI.create("file:/Users/jupp/Documents/e-lico/svn/trunk/Public/kupo/imports/cto.owl"));
//            manager.loadOntology(IRI.create("file:/Users/jupp/Documents/e-lico/svn/trunk/Public/kupo/imports/go_bp.owl"));
//            OWLOntology go_slim = manager.loadOntology(IRI.create("file:/Users/jupp/Documents/e-lico/svn/trunk/Public/kupo/imports/goslim_goa.owl"));
//            manager.loadOntology(IRI.create("file:/Users/bhig/dev/kupDB_scripts/elicosvn/Public/kupo/imports/mao.owl"));
//            manager.loadOntology(IRI.create("file:/Users/bhig/dev/kupDB_scripts/elicosvn/Public/kupo/imports/cto.owl"));
//            manager.loadOntology(IRI.create("file:/Users/bhig/dev/kupDB_scripts/elicosvn/Public/kupo/imports/go_bp.owl"));
//            OWLOntology go_slim = manager.loadOntology(IRI.create("file:/Users/bhig/dev/kupDB_scripts/elicosvn/Public/kupo/imports/goslim_goa.owl"));

            manager.loadOntologyFromOntologyDocument(KUPKBOWLQueryImpl.class.getClassLoader().getResourceAsStream("resources/ontologies/mao.owl"));
            manager.loadOntologyFromOntologyDocument(KUPKBOWLQueryImpl.class.getClassLoader().getResourceAsStream("resources/ontologies/cto.owl"));
            manager.loadOntologyFromOntologyDocument(KUPKBOWLQueryImpl.class.getClassLoader().getResourceAsStream("resources/ontologies/go_bp.owl"));
            OWLOntology go_slim = manager.loadOntologyFromOntologyDocument(KUPKBOWLQueryImpl.class.getClassLoader().getResourceAsStream("resources/ontologies/goslim_goa.owl"));


//            manager.loadOntology(IRI.create("http://www.kupkb.org/public/kupo/kupo-emap.owl"));
//            OWLOntology kupo = manager.loadOntology(IRI.create("http://www.kupkb.org/public/kupo/kupo-core.owl"));

            OWLOntology kupo = manager.loadOntologyFromOntologyDocument(KUPKBOWLQueryImpl.class.getClassLoader().getResourceAsStream("resources/ontologies/kupkb-merged.owl"));

//            manager.loadOntology(IRI.create("http://purl.org/obo/owl/adult_mouse_anatomy"));
//            manager.loadOntology(IRI.create("http://purl.org/obo/owl/cell"));
//            manager.loadOntology(IRI.create("http://purl.org/obo/owl/biological_process"));

//

            OWLImportsDeclaration impdec1 = factory.getOWLImportsDeclaration(IRI.create("http://purl.org/obo/owl/adult_mouse_anatomy"));
            OWLImportsDeclaration impdec2 = factory.getOWLImportsDeclaration(IRI.create("http://purl.org/obo/owl/cell"));
            OWLImportsDeclaration impdec3 = factory.getOWLImportsDeclaration(IRI.create("http://purl.org/obo/owl/biological_process"));
            OWLImportsDeclaration impdec4 = factory.getOWLImportsDeclaration(IRI.create("http://www.kupkb.org/public/kupo/kupo-emap.owl"));


            manager.applyChange(new AddImport(kupo, impdec1));
            manager.applyChange(new AddImport(kupo, impdec2));
            manager.applyChange(new AddImport(kupo, impdec3));
            manager.applyChange(new AddImport(kupo, impdec4));



            /* Little hack to remove unwanted bits of ontology hierarchy from the display
             * In the future we need to create a new ontology that imports the existing ontologies, in this ontology
             * we add annotations to classes to indicate whether it should be shown or hidden, that way we can manage this
             * at the ontology level rather than deep in the code
             *  UPDATE... this is getting out of hand!
             *  */
            // OWLClass kupoCore = factory.getOWLClass(IRI.create("http://www.kupkb.org/data/kupo/KUPO_0000001"));

            OWLClass kupoanatomy = factory.getOWLClass(IRI.create("http://www.kupkb.org/data/kupo/KUPO_0000003"));
            OWLClass mouseAnatomy = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/MA#MA_0000001"));
            OWLClass mouseAdult = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/MA#MA_0002405"));
            OWLClass abdominalSegmentOrgan = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/MA#MA_0000529"));
            OWLClass abdomanOrgan = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/MA#MA_0000029"));
            OWLClass abdoman = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/MA#MA_0000522"));
            OWLClass abdomanSegmentTrunkOrgan = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/MA#MA_0000021"));
            OWLClass trunkOrgan = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/MA#MA_0000516"));
            OWLClass trunkSegmentTrunkOrgan = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/MA#MA_0000004"));
            OWLClass renalUrinarySystem = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/MA#MA_0000325"));
            OWLClass visceralOrganSystem = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/MA#MA_0000019"));
            OWLClass organSystem = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/MA#MA_0000003"));

            OWLClass kupodisease = factory.getOWLClass(IRI.create("http://www.kupkb.org/data/kupo/KUPO_0000007"));
            OWLClass kupocell = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/CL#CL_0000000"));
            OWLClass ctocell = factory.getOWLClass(IRI.create("http://www.kupkb.org/data/kupo/KUPO_0000002"));
            OWLClass animalCell = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/CL#CL_0000548"));
            OWLClass cellFunction = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/CL#CL_0000144"));
            OWLClass cellHistology = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/CL#CL_0000063"));
            OWLClass cellLineage = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/CL#CL_0000220"));
            OWLClass cellNuclear = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/CL#CL_0000224"));
            OWLClass cellPloidy = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/CL#CL_0000414"));
            OWLClass stuffCell = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/CL#CL_0000325"));
            OWLClass cellNonTerm = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/CL#CL_0000055"));
            OWLClass supportCell = factory.getOWLClass(IRI.create("http://purl.org/obo/owl/CL#CL_0000630"));
            OWLClass kupoModel = factory.getOWLClass(IRI.create("http://www.kupkb.org/data/kupo/KUPO_0000004"));

//            filterSet.add(kupoCore);

            anatomyFilterSet.add(mouseAnatomy);
            anatomyFilterSet.add(kupoanatomy);
            anatomyFilterSet.add(mouseAdult);
            anatomyFilterSet.add(abdominalSegmentOrgan);
            anatomyFilterSet.add(abdomanOrgan);
            anatomyFilterSet.add(abdoman);
            anatomyFilterSet.add(abdomanSegmentTrunkOrgan);
            anatomyFilterSet.add(trunkOrgan);
            anatomyFilterSet.add(trunkSegmentTrunkOrgan);
            anatomyFilterSet.add(renalUrinarySystem);
            anatomyFilterSet.add(visceralOrganSystem);
            anatomyFilterSet.add(organSystem);
            anatomyFilterSet.add(factory.getOWLThing());

            cellFilterSet.add(kupocell);
            cellFilterSet.add(supportCell);
            cellFilterSet.add(stuffCell);
            cellFilterSet.add(ctocell);
            cellFilterSet.add(animalCell);
            cellFilterSet.add(cellFunction);
            cellFilterSet.add(cellHistology);
            cellFilterSet.add(cellLineage);
            cellFilterSet.add(cellNuclear);
            cellFilterSet.add(cellPloidy);
            cellFilterSet.add(cellNonTerm);
            cellFilterSet.add(factory.getOWLThing());

            diseaseFilterSet.add(kupodisease);
            diseaseFilterSet.add(kupoModel);
            diseaseFilterSet.add(factory.getOWLThing());

            bpFilterSet.add(factory.getOWLThing());
            bpFilterSet.add(factory.getOWLClass(IRI.create("http://purl.org/obo/owl/GO#GO_0008150")));
            for (OWLClassExpression sub : factory.getOWLClass(IRI.create("http://purl.org/obo/owl/GO#GO_0008150")).getSubClasses(go_slim)) {
                if (!sub.isAnonymous()) {
                    bpFilterSet.add(sub.asOWLClass());
                }
            }

            // hack over...

            hermitReasoner = new Reasoner(kupo);

            System.out.println("starting reasoner...");
            long t1 = System.currentTimeMillis();
            if (hermitReasoner.isConsistent()) {
                System.out.println("Classified!");
            }
            else {
                // should never happen!
                System.err.println("Inconsistent");
            }

            hermitReasoner.getUnsatisfiableClasses();
            long t2 = System.currentTimeMillis();

            System.out.println("Finished:" + (t2-t1) + "ms");

            treeNodes = new HashMap<IRI, MyTreeNode>();


        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }



    public DAGTreeModel getTreeModel (Map<String, Set<String>> ids) {

        treeNodes = new HashMap<IRI, MyTreeNode>();
        cellRoot = new MyTreeNode("http://www.kupkb.org/data/kupo/IKUP_0000001", "Cell");
        anatomyRoot = new MyTreeNode("http://www.kupkb.org/data/kupo/IKUP_0000002", "Anatomy");
        diseaseRoot = new MyTreeNode("http://www.kupkb.org/data/kupo/IKUP_0000003", "Disease/Model");
        bpRoot = new MyTreeNode("http://www.kupkb.org/data/kupo/IKUP_0000004", "Biological process");

        
        DAGTreeModel dagTreeModel = new DAGTreeModel();
        dagTreeModel.addParentChildRelationship(dagTreeModel.getRoot(), cellRoot);
        dagTreeModel.addParentChildRelationship(dagTreeModel.getRoot(), anatomyRoot);
        dagTreeModel.addParentChildRelationship(dagTreeModel.getRoot(), diseaseRoot);
        dagTreeModel.addParentChildRelationship(dagTreeModel.getRoot(), bpRoot);

//        dagTreeModel.addParentChildRelationship(dagTreeModel.getRoot(), unkownRoot);

        // if we have models, get the disease terms as well, if we don't already have them

        for (String iri : ids.keySet()) {
            getParents(dagTreeModel, iri, ids.get(iri));
        }

        return dagTreeModel;


    }


    public String[] getChildren(String[] termIRI) {

        Set<String> children = new HashSet<String>();


        for (String t : termIRI) {
            OWLClass cls = factory.getOWLClass(IRI.create(t));
            //System.out.println("searching for children of : " + getLabel(cls));
            OWLObjectSomeValuesFrom partRest = factory.getOWLObjectSomeValuesFrom(factory.getOWLObjectProperty(IRI.create(PART_OF)), cls);
            OWLObjectSomeValuesFrom mimicsRest = factory.getOWLObjectSomeValuesFrom(factory.getOWLObjectProperty(IRI.create(MIMICS)), cls);

            Set<OWLClassExpression> unionSet = new HashSet<OWLClassExpression>();
            unionSet.add(cls);
            unionSet.add(partRest);
            unionSet.add(mimicsRest);
            OWLObjectUnionOf union = factory.getOWLObjectUnionOf(unionSet);

            //System.out.println("query" + union.toString()) ;
            for (OWLClass subClass : hermitReasoner.getSubClasses(union, false).getFlattened()) {
//                System.out.println("Child term: " + getLabel(subClass));
                children.add(subClass.getIRI().toString());
            }
        }

        return children.toArray( new String[children.size()]);

    }

    public void getParents (DAGTreeModel model, String string, Set<String> annotatedGenes) {

        OWLClass cls = factory.getOWLClass(IRI.create(string));

    
        Set<OWLClass> directParentsNoRoots = removeRoots(getParents(cls));
        Set<OWLClass> partOfParentsNoRoots = removeRoots(getPartOf(cls));

        MyTreeNode child = getNode(cls);

        child.getChildGenes().addAll(annotatedGenes);

        for (OWLClass parentClass : directParentsNoRoots ) {

            MyTreeNode parentNode  = getNode(parentClass);

            parentNode.getChildGenes().addAll(child.getChildGenes());
            model.addParentChildRelationship(parentNode, child);

            getParents(model, parentClass.getIRI().toString(), parentNode.getChildGenes());
        }


        for (OWLClass partOfClass : partOfParentsNoRoots) {

            MyTreeNode parentNode  = getNode(partOfClass);

            parentNode.getChildGenes().addAll(child.getChildGenes());

            model.addParentChildRelationship(parentNode, child);
            getParents(model, partOfClass.getIRI().toString(), parentNode.getChildGenes());

        }

        if (partOfParentsNoRoots.size() + directParentsNoRoots.size() == 0) {


            Set<OWLClass> directParents = getParents(cls);
            Set<OWLClass> partOfParents = getPartOf(cls);

            directParents.addAll(partOfParents);
            MyTreeNode root = null;

            for (OWLClass parent : directParents) {

                if (parent.equals(factory.getOWLThing())) {
                    continue;
                }

                if (anatomyFilterSet.contains(parent)) {
                    root = anatomyRoot;
                }
                else if (cellFilterSet.contains(parent)) {
                    root = cellRoot;
                }
                else if (diseaseFilterSet.contains(parent)) {
                    root = diseaseRoot;
                }
                else if (bpFilterSet.contains(parent)) {
                    root = bpRoot;
                }
            }

            if (root != null) {
                root.getChildGenes().addAll(child.getChildGenes());
//                System.out.println("Adding" + child.getLabel() + " to root " + root.getLabel());
                model.addParentChildRelationship(root, child);
            }
        }
    }

    public MyTreeNode getNode (OWLClass cls) {

        if (treeNodes.containsKey(cls.getIRI())) {
            return treeNodes.get(cls.getIRI());
        }

        String label = getLabel(cls);

        MyTreeNode node = new MyTreeNode(cls.getIRI().toString(), label);
        treeNodes.put(cls.getIRI(), node);
        return node;

    }

    private String getLabel(OWLClass cls) {

        Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
        for (OWLOntology onto : manager.getOntologies()) {
            annotations.addAll(cls.getAnnotations(onto));
        }
        for (OWLAnnotation anno : annotations) {
            if (anno.getProperty().getIRI().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI())) {
                return prettyString(anno.getValue().toString());
            }
        }

        if (cls.getIRI().getFragment() != null) {
            return prettyString(cls.getIRI().getFragment());
        }

        return cls.toString();
    }

    public String prettyString (String s ) {
        s = s.replace("@en", "");
        s= s.replaceAll("\"", "");
        s= s.replaceAll("\\^\\^xsd:string", "");

        return StringUtils.capitalize(s);
    }

    public Set<OWLClass> getPartOf(OWLClass cls) {

        Set<OWLClass> parents = new HashSet<OWLClass>();
        for (OWLOntology onto : manager.getOntologies()) {
            for (OWLClassExpression exp :  cls.getSuperClasses(onto)) {
                RestrictionVisitor visitor = new RestrictionVisitor();
                exp.accept(visitor);
                if (visitor.getParent() != null) {
                    parents.add(visitor.getParent());
                }

            }
        }

        return parents;
    }


    private class RestrictionVisitor extends OWLClassExpressionVisitorAdapter {

        private OWLClass parent;
        @Override
        public void visit(OWLObjectSomeValuesFrom desc) {

            if (desc.getProperty().asOWLObjectProperty().getIRI().equals(IRI.create(PART_OF))) {
                if (!desc.getFiller().isAnonymous()) {
                    if (desc.getFiller() instanceof OWLClass) {
                        parent = desc.getFiller().asOWLClass();
                    }

                }
            }
       }

        public OWLClass getParent() {
            return parent;
        }
    }

    public Set<OWLClass> getParents(OWLClass cls) {
        return hermitReasoner.getSuperClasses(cls, true).getFlattened();
    }

    private Set<OWLClass> removeRoots(Set<OWLClass> parents) {

        parents.removeAll(anatomyFilterSet);
        parents.removeAll(cellFilterSet);
        parents.removeAll(diseaseFilterSet);
        parents.removeAll(bpFilterSet);
        return parents;
    }

    public static void main(String[] args) {

        KUPKBOWLQueryImpl kb = new KUPKBOWLQueryImpl();

        Map<String, Set<String>> iriSet = new HashMap<String, Set<String>>();

            Set<String> a = new HashSet<String>();
            Set<String> b = new HashSet<String>();
            Set<String> c = new HashSet<String>();
            Set<String> d = new HashSet<String>();

            a.add("100");
            a.add("222");
            a.add("333");
            a.add("444");
            a.add("1345");
            a.add("134");

            b.add("444");
            b.add("635727364324");
            b.add("73647");
            b.add("72467");
            b.add("27264");
            b.add("72");
            b.add("247");

            c.add("2467");
            c.add("7245");
        c.add("59086");
        c.add("7040");
        c.add("21803");

            d.add("2475");
            d.add("7245");
            d.add("762");
            d.add("72427");
            d.add("2457");
            d.add("754272547245");
            d.add("7245");
            d.add("2457");

            iriSet.put("http://purl.org/obo/owl/MA#MA_0000368", c);
            iriSet.put("http://www.kupkb.org/data/kupo/KUPO_0200120", c);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0000372", c);
            iriSet.put("http://www.kupkb.org/data/kupo/KUPO_0200079", c);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0001657", c);
            iriSet.put("http://www.kupkb.org/data/kupo/KUPO_0200047", c);

            iriSet.put("http://www.kupkb.org/data/kupo/KUPO_0001126", a);
            iriSet.put("http://www.kupkb.org/data/kupo/KUPO_0001032", b);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0000368", c);
            iriSet.put("http://www.kupkb.org/data/kupo/KUPO_0001127", d);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0002579", d);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0001666", c);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0002615", b);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0002585", a);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0002602", a);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0001658", b);
            iriSet.put("http://www.kupkb.org/data/kupo/KUPO_0001032", c);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0001657", d);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0002599", d);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0001669", b);
            iriSet.put("http://purl.org/obo/owl/MA#MA_0002614", a);
            iriSet.put("http://purl.org/obo/owlapi/disease_ontology#DOID_5199", b);
//        JTree tree = kb.getBrowserTree(iriSet);

        DAGTreeModel  model = kb.getTreeModel(iriSet);

        JTree tree = new JTree((TreeModel) model);

        JScrollPane treeView = new JScrollPane(tree);

        JFrame panel = new JFrame();
        panel.add(treeView);

        panel.setVisible(true);
    }

    // inner classes
// implements TreeModel for testing!




}
