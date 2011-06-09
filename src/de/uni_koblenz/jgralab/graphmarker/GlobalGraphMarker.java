package de.uni_koblenz.jgralab.graphmarker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl;

public class GlobalGraphMarker<T extends GraphElement<?, ?, ?>> implements GraphMarker<T> {

	enum GraphMarkerImplementationType {
		ARRAY,
		BITSET;
	}
	
	private static final int DEFAULT_PARTIAL_GRAPH_COUNT = 1000;
	
	private GraphMarkerImplementationType implementationTypeForLocalMarkers;
	
	private Graph markedGraph;
	
	private SimpleGraphMarker<T>[] localGraphMarkers;
	
	/**
	 * Creates a new global graph marker marking the given graph and using the 
	 * given implementation type for the local graph markers
	 * @param globalGraph the global graph to be marked
	 * @param implementationType the impelemtation type to be used for the local markers
	 */
	public GlobalGraphMarker(Graph globalGraph, GraphMarkerImplementationType implementationType) {
		implementationTypeForLocalMarkers = implementationType;
		this.markedGraph = globalGraph;
		localGraphMarkers = new SimpleGraphMarker[DEFAULT_PARTIAL_GRAPH_COUNT];
	}

	@Override
	public boolean isMarked(T graphElement) {
		long elementId = graphElement.getId();
		int partialGraphId = GraphDatabaseBaseImpl.getPartialGraphId(elementId);
		SimpleGraphMarker<T> localMarker = localGraphMarkers[partialGraphId];
		if (localMarker == null)
			return false;
		else return localMarker.isMarked(graphElement);  		
	}

	@Override
	public boolean removeMark(T graphElement) {
		long elementId = graphElement.getId();
		int partialGraphId = GraphDatabaseBaseImpl.getPartialGraphId(elementId);
		SimpleGraphMarker<T> localMarker = localGraphMarkers[partialGraphId];
		if (localMarker == null)
			return false;
		else return localMarker.removeMark(graphElement);
	}

	@Override
	public long size() {
		long count = 0;
		for (int i=0; i<localGraphMarkers.length; i++) {
			if (localGraphMarkers[i] != null)
				count += localGraphMarkers[i].size();
		}
		return count;
	}

	@Override
	public boolean isEmpty() {
		for (int i=0; i<localGraphMarkers.length; i++) {
			if ((localGraphMarkers[i] != null) && (!localGraphMarkers[i].isEmpty()))
				return false;
		}
		return true;
	}

	@Override
	public void clear() {
		for (int i=0; i<localGraphMarkers.length; i++) {
			if (localGraphMarkers[i] != null)
				localGraphMarkers[i].clear();
		}
	}

	@Override
	public Graph getGraph() {
		return markedGraph;
	}

	@Override
	public Iterable<T> getMarkedElements() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private class GlobalGraphMarkerIterable<T extends GraphElement<?,?,?>> implements Iterable<T> {

		@Override
		public Iterator<T> iterator() {
			return new GlobalGraphMarkerIterator<T>(this);
		}
		
	}
	
	private class GlobalGraphMarkerIterator<T extends GraphElement<?,?,?>> implements Iterator<T> {

		private GlobalGraphMarker<T> graphMarker;
		
		private List<Iterator<T>> localIterators;
				
		private Iterator<T> currentIterator;
		
		public GlobalGraphMarkerIterator(
				GlobalGraphMarkerIterable<T> globalGraphMarkerIterable) {
			localIterators = new LinkedList<Iterator<T>>();
			for (int i=0; i<graphMarker.localGraphMarkers.length; i++) {
				if (graphMarker.localGraphMarkers[i] != null)
					localIterators.add((Iterator<T>) graphMarker.localGraphMarkers[i].getMarkedElements().iterator());
			}
			currentIterator = localIterators.remove(0);
		}

		@Override
		public boolean hasNext() {
			if (currentIterator == null)
				return false;
			while (!currentIterator.hasNext()) {
				if (localIterators.size() > 0)
					currentIterator = localIterators.remove(0);
				else
					throw new NoSuchElementException("There is no next element marked in this graph marker");
			}
			return true;
		}

		@Override
		public T next() {
			if (currentIterator == null)
				throw new NoSuchElementException("There is no next element marked in this graph marker");
			while (!currentIterator.hasNext()) {
				if (localIterators.size() > 0)
					currentIterator = localIterators.remove(0);
				else
					throw new NoSuchElementException("There is no next element marked in this graph marker");
			}
			return currentIterator.next();
		}

		@Override
		public void remove() {
			currentIterator.remove();
		}
		
	}
	
	
	
}
