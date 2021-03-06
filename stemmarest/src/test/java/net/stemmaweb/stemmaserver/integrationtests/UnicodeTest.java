package net.stemmaweb.stemmaserver.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.stemmaweb.rest.ERelations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 * 
 * Contains all tests for the api calls related to unicode compatibility.
 * 
 * @author PSE FS 2015 Team2
 *
 */
public class UnicodeTest {

	GraphDatabaseService graphDb;

	@Before
	public void prepareTestDatabase() {
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
		// create a new Graph Database
	}

	@Test
	public void testUnicodeCapability() {
		Node n = null;
		try (Transaction tx = graphDb.beginTx()) {
			n = graphDb.createNode();
			n.setProperty("name", "Ã¤Ã¶Ã¼×“×’×›Î±Î²Î³");
			tx.success();
		}

		// The node should have a valid id
		assertTrue(n.getId() > -1L);

		// Retrieve a node by using the id of the created node. The id's and
		// property should match.
		try (Transaction tx = graphDb.beginTx()) {
			Node foundNode = graphDb.getNodeById(n.getId());
			assertTrue(foundNode.getId() == n.getId());
			assertTrue(((String) foundNode.getProperty("name"))
					.equals("Ã¤Ã¶Ã¼×“×’×›Î±Î²Î³"));
		}
	}

	@Test
	public void testHebrewCapabilityMatch() {
		Node node1 = null, node2 = null;
		Relationship relationship = null;
		try (Transaction tx = graphDb.beginTx()) {
			node1 = graphDb.createNode();
			node1.setProperty("name", "בדיקה");
			node2 = graphDb.createNode();
			node2.setProperty("name", "עוד בדיקה");
			
			relationship = node1.createRelationshipTo(node2, ERelations.NORMAL);
			relationship.setProperty("type", "יחס");
			tx.success();
		}

		// The node should have a valid id
		assertTrue(node1.getId() > -1L);
		assertTrue(node2.getId() > -1L);
		assertTrue(relationship.getId() > -1L);



		// Retrieve nodes and relationship by using the id of the created node. The id's and
		// property should match.
		try (Transaction tx = graphDb.beginTx()) {
			Node foundNode1 = graphDb.getNodeById(node1.getId());
			Node foundNode2 = graphDb.getNodeById(node2.getId());
			Relationship foundRelationship = graphDb.getRelationshipById(relationship.getId());
			
			assertTrue(foundNode1.getId() == node1.getId());
			assertEquals("בדיקה", (String) foundNode1.getProperty("name"));
			assertEquals("עוד בדיקה", (String) foundNode2.getProperty("name"));
			assertEquals("יחס", (String) foundRelationship.getProperty("type"));
		}
	}

	@Test
	public void testHebrewCapabilityNoMatch() {
		Node n = null;
		try (Transaction tx = graphDb.beginTx()) {
			n = graphDb.createNode();
			n.setProperty("name", "בדיקה");
			tx.success();
		}

		// The node should have a valid id
		assertTrue(n.getId() > -1L);

		// Retrieve a node by using the id of the created node. The id's and
		// property should match.
		try (Transaction tx = graphDb.beginTx()) {
			Node foundNode = graphDb.getNodeById(n.getId());
			assertTrue(foundNode.getId() == n.getId());
			assertFalse(((String) foundNode.getProperty("name"))
					.equals("בליקה"));
		}
	}

	@Test
	public void testGreekCapabilityMatch() {
		Node n = null;
		try (Transaction tx = graphDb.beginTx()) {
			n = graphDb.createNode();
			n.setProperty("name", "ειπον");
			tx.success();
		}

		// The node should have a valid id
		assertTrue(n.getId() > -1L);

		// Retrieve a node by using the id of the created node. The id's and
		// property should match.
		try (Transaction tx = graphDb.beginTx()) {
			Node foundNode = graphDb.getNodeById(n.getId());
			assertTrue(foundNode.getId() == n.getId());
			assertEquals("ειπον", (String) foundNode.getProperty("name"));
		}
	}

	@Test
	public void testGreekCapabilityNoMatch() {
		Node n = null;
		try (Transaction tx = graphDb.beginTx()) {
			n = graphDb.createNode();
			n.setProperty("name", "ειπον");
			tx.success();
		}

		// The node should have a valid id
		assertTrue(n.getId() > -1L);

		// Retrieve a node by using the id of the created node. The id's and
		// property should match.
		try (Transaction tx = graphDb.beginTx()) {
			Node foundNode = graphDb.getNodeById(n.getId());
			assertTrue(foundNode.getId() == n.getId());
			assertFalse(((String) foundNode.getProperty("name"))
					.equals("ειπων"));
		}
	}

	@Test
	public void testArabicCapabilityMatch() {
		Node n = null;
		try (Transaction tx = graphDb.beginTx()) {
			n = graphDb.createNode();
			n.setProperty("name", "المطلق");
			tx.success();
		}

		// The node should have a valid id
		assertTrue(n.getId() > -1L);

		// Retrieve a node by using the id of the created node. The id's and
		// property should match.
		try (Transaction tx = graphDb.beginTx()) {
			Node foundNode = graphDb.getNodeById(n.getId());
			assertTrue(foundNode.getId() == n.getId());
			assertEquals("المطلق", (String) foundNode.getProperty("name"));
		}
	}

	@Test
	public void testArabicCapabilityNoMatch() {
		Node n = null;
		try (Transaction tx = graphDb.beginTx()) {
			n = graphDb.createNode();
			n.setProperty("name", "المطلق");
			tx.success();
		}

		// The node should have a valid id
		assertTrue(n.getId() > -1L);

		// Retrieve a node by using the id of the created node. The id's and
		// property should match.
		try (Transaction tx = graphDb.beginTx()) {
			Node foundNode = graphDb.getNodeById(n.getId());
			assertTrue(foundNode.getId() == n.getId());
			assertFalse(((String) foundNode.getProperty("name"))
					.equals("المطلو"));
		}
	}

	@After
	public void destroyTestDatabase() {
		graphDb.shutdown();
		// destroy the test database
	}
}