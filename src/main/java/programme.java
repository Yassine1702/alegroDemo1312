import com.franz.agraph.repository.*;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.File;
import java.io.IOException;

public class programme {
    public static void main(String[] args) {
         final String SERVER_URL="http://localhost:10035";
         final String USERNAME="YassineAtos";
         final String PWD="YassineAtosPwd";
         final String REPO_ID="javatutorials";

        AGServer agServer = new  AGServer(SERVER_URL,USERNAME,PWD);
        System.out.println(""+ agServer.listCatalogs());
        AGRepository agRepository =   agServer.getRootCatalog().createRepository(REPO_ID);
        agRepository.initialize();
        System.out.println(""+agRepository.isWritable());
        AGRepositoryConnection agRepositoryConnection = agRepository.getConnection();
        System.out.println("before inserting the graph size is : "+agRepositoryConnection.size());

        // Asserting and retracting
        String exns_people = "http://exemple.org/people/";
        String exns_ontology = "http://exemple.org/ontology/";
        AGValueFactory factory = agRepositoryConnection.getRepository().getValueFactory();
      /*  IRI alice = factory.createIRI("http://exemple.org/people/alice");
        IRI bob = factory.createIRI("http://exemple.org/people/bob");
        IRI name = factory.createIRI("http://exemple.org/ontology/name");
        IRI person = factory.createIRI("http://exemple.org/ontology/person");*/

        IRI alice = factory.createIRI(exns_people,"alice");
        IRI bob = factory.createIRI(exns_people,"bob");
        IRI name = factory.createIRI(exns_ontology,"name");
        IRI person = factory.createIRI(exns_ontology,"person");
        IRI age = factory.createIRI(exns_ontology,"age");
        Literal bobName= factory.createLiteral("bob", XMLSchema.STRING);
        Literal aliceName=factory.createLiteral("alice",XMLSchema.STRING);

        agRepositoryConnection.add(alice,name,aliceName);
        agRepositoryConnection.add(alice, RDF.TYPE,person);
        agRepositoryConnection.add(bob,name,bobName);
        agRepositoryConnection.add(bob,RDF.TYPE,person);

            //asserting through statements
        Literal fortyTwoInt = factory.createLiteral("42",XMLSchema.INT);
        Statement alice_stmt = factory.createStatement(alice,age,fortyTwoInt);
        Statement bob_stmt = factory.createStatement(bob,age,fortyTwoInt);
        agRepositoryConnection.add(alice_stmt);
        agRepositoryConnection.add(bob_stmt);
        System.out.println("after inserting the graph size is :"+agRepositoryConnection.size());


       RepositoryResult result = agRepositoryConnection.getStatements(null,null,null,false);
        while(result.hasNext()){
            Statement stmt = (Statement) result.next();
            System.out.println("Subject: "+stmt.getSubject()+", Predicate: "+stmt.getPredicate()+", Object: "+stmt.getObject()+", Graph: "+stmt.getContext());
        }

        //  agRepositoryConnection.remove(bob,null,null);
        // agRepositoryConnection.remove(alice,null,null);
            agRepositoryConnection.clear();

        System.out.println("after deleting instances the graph size is :"+agRepositoryConnection.size());

        // SPARQL QUERY

        agRepositoryConnection.add(alice,name,aliceName);
        agRepositoryConnection.add(alice, RDF.TYPE,person);
        agRepositoryConnection.add(bob,name,bobName);
        agRepositoryConnection.add(bob,RDF.TYPE,person);
        agRepositoryConnection.add(alice_stmt);
        agRepositoryConnection.add(bob_stmt);
        System.out.println("after inserting the graph size is :"+agRepositoryConnection.size());

        String queryString ="select ?s ?p ?o where {?s ?o ?p}";
        String queryString2="select ?s ?p where {?s ?p \"42\"^^<http://www.w3.org/2001/XMLSchema#int>}";
        AGTupleQuery tupleQuery = agRepositoryConnection.prepareTupleQuery(AGQueryLanguage.SPARQL,queryString);
        AGTupleQuery tupleQuery1 = agRepositoryConnection.prepareTupleQuery(AGQueryLanguage.SPARQL,queryString2);
        TupleQueryResult queryResult =  tupleQuery.evaluate();
        TupleQueryResult queryResult1 = tupleQuery1.evaluate();
        System.out.println("the query retrieved "+tupleQuery.count()+" results");

        try {
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                Value s = bindingSet.getValue("s");
                Value p = bindingSet.getValue("p");
                System.out.println("SPARQL : Subject: " + s + ", Predicate: " + p);
            }
        }finally {
            queryResult.close();
        }

        try {
            while (queryResult1.hasNext()) {
                BindingSet bindingSet = queryResult1.next();
                Value s = bindingSet.getValue("s");
                Value p = bindingSet.getValue("p");
                Value o = bindingSet.getValue("o");
                System.out.println("SPARQL1 : Subject: " + s + ", Predicate: " + p + ", Object: " + o);
            }
        }finally {
            queryResult1.close();
        }
     agRepositoryConnection.clear();
        System.out.println("after deleting instances the graph size is :"+agRepositoryConnection.size());

        // Asserting through a file
        String baseURI = "http://example.org/example/local";
        AGRepository testRepo1 = agServer.getRootCatalog().createRepository("testRepo1");
        AGRepositoryConnection testRepo1Connection= testRepo1.getConnection();
        testRepo1Connection.clear();

        try {
            testRepo1Connection.add(new File("C:/Users/a835928/IdeaProjects/Alegro1/src/main/resources/human1.rdf"),baseURI, RDFFormat.RDFXML);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String test1RepoQueryString = "select ?s ?o ?p where {?s ?o ?p}";
        AGTupleQuery test1RepoQuery= testRepo1Connection.prepareTupleQuery(AGQueryLanguage.SPARQL,test1RepoQueryString);
        TupleQueryResult testRepo1Result = test1RepoQuery.evaluate();
        while(testRepo1Result.hasNext()){
         BindingSet testRepo1Set =  testRepo1Result.next();
         testRepo1Set.getValue("s");
            Value s = testRepo1Set.getValue("s");
            Value p = testRepo1Set.getValue("p");
            Value o = testRepo1Set.getValue("o");
            System.out.println("RDF FILE : Subject: " + s + ", Predicate: " + p + ", Object: " + o);
        }


    }

}
