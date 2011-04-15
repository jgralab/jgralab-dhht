package de.uni_koblenz.jgralab.dhhttest;

import java.rmi.RemoteException;
import java.util.ArrayList;

import de.uni_koblenz.jgralab.dhhttest.schema.Activity;
import de.uni_koblenz.jgralab.dhhttest.schema.BusinessProcess;
import de.uni_koblenz.jgralab.dhhttest.schema.DHHTTestGraph;
import de.uni_koblenz.jgralab.dhhttest.schema.DHHTTestSchema;
import de.uni_koblenz.jgralab.dhhttest.schema.Feature;
import de.uni_koblenz.jgralab.dhhttest.schema.TraceabilityLink;
import de.uni_koblenz.jgralab.dhhttest.schema.TraceabilityLink_feature;
import de.uni_koblenz.jgralab.dhhttest.schema.TraceabilityLink_process;
import de.uni_koblenz.jgralab.dhhttest.schema.TraceabilityLink_rule;
import de.uni_koblenz.jgralab.dhhttest.schema.TraceabilityLink_target;
import de.uni_koblenz.jgralab.dhhttest.schema.TransformationRule;

public class PerformaceTest {

	private static int factor = 1;
	
	private static int activityCount = 374002;
	
	private static int ruleCount = 1541;
	
	private static int featureCount = 123074;
	
	private static int processCount = 1383;
	
	private static int linkCount = 500000;
	
	public static void main(String[] args) {
		try {
			activityCount *= factor;
			featureCount *= factor;
			ruleCount *= factor;
			processCount *= factor;
			linkCount *= factor;
		ArrayList<Activity> activityList = new ArrayList<Activity>(activityCount);
		ArrayList<Feature> featureList = new ArrayList<Feature>(featureCount);
		ArrayList<BusinessProcess> processList = new ArrayList<BusinessProcess>(processCount);
		ArrayList<TransformationRule> ruleList = new ArrayList<TransformationRule>(ruleCount);
		DHHTTestGraph graph = DHHTTestSchema.instance().createDHHTTestGraph();
		System.out.println("Creating example DHHTGraph");
		long startTime = System.currentTimeMillis();
		for (int i=0; i<activityCount;i++) {
			Activity a = graph.createActivity();
			activityList.add(a);
		}
		for (int i=0; i<featureCount;i++) {
			Feature f = graph.createFeature();
			featureList.add(f);
		}
		for (int i=0; i<processCount;i++) {
			BusinessProcess p = graph.createBusinessProcess();
			processList.add(p);
		}
		for (int i=0; i<ruleCount;i++) {
			TransformationRule r = graph.createTransformationRule();
			ruleList.add(r);
		}
		for (int i=0; i<linkCount; i++) {
			TraceabilityLink l = graph.createTraceabilityLink();
			//add 1 activity, 1 feature, one process, one rule
			//System.out.println("i: " + i + " i%act: " + i%activityCount);
			l.connect(TraceabilityLink_target.class, activityList.get(i%activityCount));
			l.connect(TraceabilityLink_feature.class, featureList.get(i%featureCount));
			l.connect(TraceabilityLink_process.class, processList.get(i%processCount));
			l.connect(TraceabilityLink_rule.class, ruleList.get(i%ruleCount));
		}
		
		
		long time = System.currentTimeMillis() - startTime;
		System.out.println("Sucessfully created graph in " + time + " milliseconds");
		System.out.println("Graph has: " + graph.getVCount() + " vertices and " + graph.getECount() + " edges");
		
		System.out.println("Starting search");
		startTime = System.currentTimeMillis();
		CountHypergraphSearchAlgorithm algo = new CountHypergraphSearchAlgorithm();
		algo.run(graph.getFirstVertex());
		time = System.currentTimeMillis() - startTime;
		System.out.println("Applied BFS on hypergraph in " + time + " milliseconds");
		
		System.out.println("Visited " + algo.getVertexCount() + " of " + graph.getVCount() + " vertices");
		System.out.println("Visited " + algo.getEdgeCount() + " of " + graph.getECount() + " edges");
		
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}
	
}
