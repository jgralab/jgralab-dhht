/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package de.uni_koblenz.jgralab.impl.trans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.FreeIndexList;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.trans.CommitFailedException;
import de.uni_koblenz.jgralab.trans.InvalidSavepointException;
import de.uni_koblenz.jgralab.trans.ListPosition;
import de.uni_koblenz.jgralab.trans.Savepoint;
import de.uni_koblenz.jgralab.trans.Transaction;
import de.uni_koblenz.jgralab.trans.TransactionManager;
import de.uni_koblenz.jgralab.trans.TransactionState;
import de.uni_koblenz.jgralab.trans.VersionedDataObject;

/**
 * The implementation of a <code>Graph</edge> with versioning.
 * 
 * @author Jose Monte(monte@uni-koblenz.de)
 */
public abstract class GraphImpl extends
		de.uni_koblenz.jgralab.impl.GraphBaseImpl {
	// the transactions of this instance are managed by a transaction manager
	private TransactionManager transactionManager;

	// TODO think about representing edge, [revEdge] and vertex as
	// java.util.List!?
	// With this eCount and vCount maybe could be removed (for saving memory)!?
	// represents Eset
	protected VersionedArrayImpl<EdgeImpl[]> edge;
	// TODO maybe think about removing revEdge completely (for saving memory)?!
	protected VersionedArrayImpl<ReversedEdgeImpl[]> revEdge;
	private VersionedReferenceImpl<Integer> eCount;

	// represents Vset
	protected VersionedArrayImpl<VertexImpl[]> vertex;
	private VersionedReferenceImpl<Integer> vCount;

	// represents begin and end of Eseq
	private VersionedReferenceImpl<EdgeImpl> firstEdge;
	private VersionedReferenceImpl<EdgeImpl> lastEdge;
	protected VersionedReferenceImpl<Long> edgeListVersion;

	// represents begin and end of Vseq
	private VersionedReferenceImpl<VertexImpl> firstVertex;
	private VersionedReferenceImpl<VertexImpl> lastVertex;
	protected VersionedReferenceImpl<Long> vertexListVersion;

	// for synchronization when expanding graph...
	protected ReadWriteLock vertexSync;
	protected ReadWriteLock edgeSync;

	// holds indexes of <code>GraphElement</code>s which couldn't be freed after
	// a COMMIT or an ABORT...these indexes should be freed later.
	protected List<Integer> edgeIndexesToBeFreed;
	protected List<Integer> vertexIndexesToBeFreed;

	/**
	 * 
	 * @return increases value of persistentVersionCounter (graphVersion) if
	 *         allowed and returns it
	 */
	protected long incrPersistentVersionCounter() {
		Transaction transaction = getCurrentTransaction();
		if (transaction.getState() != TransactionState.WRITING) {
			throw new GraphException(
					"Increasing persistent version counter only allowed in writing-phase.");
		}
		// increase by value "1"
		setGraphVersion(getGraphVersion() + 1);
		return getGraphVersion();
	}

	/**
	 * 
	 * @param id
	 * @param cls
	 * @param max
	 * @param max2
	 */
	protected GraphImpl(String id, GraphClass cls, int max, int max2) {
		super(id, cls, max, max2);
		transactionManager = TransactionManagerImpl.getInstance(this);
	}

	/**
	 * @param id
	 * @param cls
	 */
	public GraphImpl(String id, GraphClass cls) {
		super(id, cls);
	}

	// --- getter ---//

	/**
	 * 
	 * @return the current value of persistentVersionCounter (graphVersion)
	 */
	protected long getPersistentVersionCounter() {
		return getGraphVersion();
	}

	@Override
	public int getECount() {
		if (eCount == null) {
			return 0;
		}
		Integer value = eCount.getValidValue(getCurrentTransaction());
		if (value == null) {
			return 0;
		}
		return value;
	}

	@Override
	protected EdgeImpl[] getEdge() {
		// accessing edge-Array while expanding it should not be allowed
		edgeSync.readLock().lock();
		EdgeImpl[] value = null;
		// important for correct loading of graph
		if (isLoading()) {
			value = edge.getLatestPersistentValue();
		} else {
			Transaction transaction = getCurrentTransaction();
			if (transaction == null) {
				throw new GraphException("Current transaction is null.");
			}
			// important for correct execution of validation-phase
			if (transaction.getState() == TransactionState.VALIDATING) {
				value = edge.getLatestPersistentValue();
			} else {
				value = edge.getValidValue(getCurrentTransaction());
			}
		}
		edgeSync.readLock().unlock();
		return value;
	}


	@Override
	protected VertexImpl[] getVertex() {
		// accessing vertex-Array while expanding it should not be allowed
		vertexSync.readLock().lock();
		VertexImpl[] value = null;
		// important for correct loading of graph
		if (isLoading()) {
			value = vertex.getLatestPersistentValue();
		} else {
			Transaction transaction = getCurrentTransaction();
			if (transaction == null) {
				throw new GraphException("Current transaction is null.");
			}
			// important for correct execution of validation-phase
			if (transaction.getState() == TransactionState.VALIDATING) {
				value = vertex.getLatestPersistentValue();
			} else {
				value = vertex.getValidValue(getCurrentTransaction());
			}
		}
		vertexSync.readLock().unlock();
		return value;
	}

	@Override
	public Edge getFirstEdge() {
		if (firstEdge == null) {
			return null;
		}
		return firstEdge.getValidValue(getCurrentTransaction());
	}

	@Override
	public Vertex getFirstVertex() {
		if (firstVertex == null) {
			return null;
		}
		return firstVertex.getValidValue(getCurrentTransaction());
	}

	@Override
	public Edge getLastEdge() {
		if (lastEdge == null) {
			return null;
		}
		return lastEdge.getValidValue(getCurrentTransaction());
	}

	@Override
	public Vertex getLastVertex() {
		if (lastVertex == null) {
			return null;
		}
		return lastVertex.getValidValue(getCurrentTransaction());
	}

	@Override
	public int getVCount() {
		if (vCount == null) {
			return 0;
		}
		Integer value = vCount.getValidValue(getCurrentTransaction());
		if (value == null) {
			return 0;
		}
		return value;
	}

	@Override
	protected FreeIndexList getFreeVertexList() {
		synchronized (freeVertexList) {
			return freeVertexList;
		}
	}

	@Override
	protected FreeIndexList getFreeEdgeList() {
		synchronized (freeEdgeList) {
			return freeEdgeList;
		}
	}

	@Override
	public long getVertexListVersion() {
		if (vertexListVersion == null) {
			return 0;
		}
		Long value = vertexListVersion.getValidValue(getCurrentTransaction());
		if (value == null) {
			return 0;
		}
		return value;
	}

	@Override
	public long getEdgeListVersion() {
		if (edgeListVersion == null) {
			return 0;
		}
		Long value = edgeListVersion.getValidValue(getCurrentTransaction());
		if (value == null) {
			return 0;
		}
		return value;
	}

	/**
	 * 
	 * @return the delete vertex list of the current transaction
	 */
	@Override
	protected List<de.uni_koblenz.jgralab.impl.VertexBaseImpl> getDeleteVertexList() {
		TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
		assert ((transaction != null) && ((transaction.getState() == TransactionState.RUNNING) || (transaction
				.getState() == TransactionState.WRITING)));
		if (transaction.deleteVertexList == null) {
			transaction.deleteVertexList = new LinkedList<de.uni_koblenz.jgralab.impl.VertexBaseImpl>();
		}
		return transaction.deleteVertexList;
	}

	// --- setter ---//

	/**
	 * 
	 * @param graphVersion
	 *            update value of graphVersion which is used as
	 *            persistentVersionCounter
	 * 
	 *            TODO rethink with -1 at the beginning. Maybe there is a more
	 *            elegant way?!
	 */
	@Override
	public void setGraphVersion(long graphVersion) {
		if ((getGraphVersion() != -1) && !isLoading()) {
			Transaction transaction = getCurrentTransaction();
			assert (transaction != null);
			// only update graphVersion (persistentVersionCounter) when
			// transaction is in writing-phase
			if (transaction.getState() == TransactionState.WRITING) {
				super.setGraphVersion(graphVersion);
			}
		} else {
			// for initialization
			super.setGraphVersion(graphVersion);
		}
	}

	@Override
	protected void setECount(int count) {
		if ((eCount == null) || isLoading()) {
			eCount = new VersionedReferenceImpl<Integer>(this, count);
		} else {
			eCount.setValidValue(count, getCurrentTransaction());
		}
	}

	@Override
	protected void setEdge(de.uni_koblenz.jgralab.impl.EdgeBaseImpl[] edge) {
		edgeSync.readLock().lock();
		try {
			this.edge.setValidValue((EdgeImpl[]) edge, getCurrentTransaction());
		} finally {
			edgeSync.readLock().unlock();
		}
	}

	@Override
	protected void setFirstEdge(
			de.uni_koblenz.jgralab.impl.EdgeBaseImpl firstEdge) {
		if ((this.firstEdge == null) || isLoading()) {
			this.firstEdge = new VersionedReferenceImpl<EdgeImpl>(this,
					(EdgeImpl) firstEdge);
		} else {
			this.firstEdge.setValidValue((EdgeImpl) firstEdge,
					getCurrentTransaction());
		}
	}

	@Override
	protected void setFirstVertex(
			de.uni_koblenz.jgralab.impl.VertexBaseImpl firstVertex) {
		if ((this.firstVertex == null) || isLoading()) {
			this.firstVertex = new VersionedReferenceImpl<VertexImpl>(this,
					(VertexImpl) firstVertex);
		} else {
			this.firstVertex.setValidValue((VertexImpl) firstVertex,
					getCurrentTransaction());
		}
	}

	@Override
	protected void setLastEdge(
			de.uni_koblenz.jgralab.impl.EdgeBaseImpl lastEdge) {
		if ((this.lastEdge == null) || isLoading()) {
			this.lastEdge = new VersionedReferenceImpl<EdgeImpl>(this,
					(EdgeImpl) lastEdge);
		} else {
			this.lastEdge.setValidValue((EdgeImpl) lastEdge,
					getCurrentTransaction());
		}
	}

	@Override
	protected void setLastVertex(
			de.uni_koblenz.jgralab.impl.VertexBaseImpl lastVertex) {
		if ((this.lastVertex == null) || isLoading()) {
			this.lastVertex = new VersionedReferenceImpl<VertexImpl>(this,
					(VertexImpl) lastVertex);
		} else {
			this.lastVertex.setValidValue((VertexImpl) lastVertex,
					getCurrentTransaction());
		}
	}


	@Override
	protected void setVCount(int count) {
		if ((vCount == null) || isLoading()) {
			vCount = new VersionedReferenceImpl<Integer>(this, count);
		} else {
			vCount.setValidValue(count, getCurrentTransaction());
		}
	}

	@Override
	protected void setVertex(de.uni_koblenz.jgralab.impl.VertexBaseImpl[] vertex) {
		vertexSync.readLock().lock();
		try {
			this.vertex.setValidValue((VertexImpl[]) vertex,
					getCurrentTransaction());
		} finally {
			vertexSync.readLock().unlock();
		}
	}

	@Override
	public void setVertexListVersion(long vertexListVersion) {
		if (this.vertexListVersion == null) {
			this.vertexListVersion = new VersionedReferenceImpl<Long>(this,
					vertexListVersion);
		}
		this.vertexListVersion.setValidValue(vertexListVersion,
				getCurrentTransaction());
	}

	@Override
	public void setEdgeListVersion(long edgeListVersion) {
		if (this.edgeListVersion == null) {
			this.edgeListVersion = new VersionedReferenceImpl<Long>(this,
					edgeListVersion);
		}
		this.edgeListVersion.setValidValue(edgeListVersion,
				getCurrentTransaction());
	}

	/**
	 * nothing needed here
	 */
	@Override
	protected void setDeleteVertexList(
			List<de.uni_koblenz.jgralab.impl.VertexBaseImpl> deleteVertexList) {
		// do nothing here
	}

	@Override
	public void abort() {
		if (getCurrentTransaction() == null) {
			throw new GraphException("Current transaction is null.");
		}
		getCurrentTransaction().abort();
	}

	@Override
	public void commit() throws CommitFailedException {
		if (getCurrentTransaction() == null) {
			throw new GraphException("Current transaction is null.");
		}
		getCurrentTransaction().commit();
	}

	@Override
	public Transaction newReadOnlyTransaction() {
		return transactionManager.createReadOnlyTransaction();
	}

	@Override
	public Transaction newTransaction() {
		return transactionManager.createTransaction();
	}

	@Override
	public Savepoint defineSavepoint() {
		if (getCurrentTransaction() == null) {
			throw new GraphException("Current transaction is null.");
		}
		return getCurrentTransaction().defineSavepoint();
	}

	@Override
	public Transaction getCurrentTransaction() {
		if (transactionManager == null) {
			transactionManager = TransactionManagerImpl.getInstance(this);
		}
		return transactionManager.getTransactionForThread(Thread
				.currentThread());
	}

	@Override
	public void restoreSavepoint(Savepoint savepoint)
			throws InvalidSavepointException {
		if (getCurrentTransaction() == null) {
			throw new GraphException("Current transaction is null.");
		}
		getCurrentTransaction().restoreSavepoint(savepoint);
	}

	@Override
	public void setCurrentTransaction(Transaction transaction) {
		transactionManager.setTransactionForThread(transaction, Thread
				.currentThread());
	}

	@Override
	public boolean isInConflict() {
		if (getCurrentTransaction() == null) {
			throw new GraphException("Current transaction is null.");
		}
		return getCurrentTransaction().isInConflict();
	}

	/**
	 * Should be called from generated <code>Graph</code> implementation classes
	 * whenever a versioned attribute is changed.
	 * 
	 * @param versionedAttribute
	 *            the changed attribute
	 */
	protected void attributeChanged(VersionedDataObject<?> versionedAttribute) {
		if (!isLoading()) {
			TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
			assert ((transaction != null)
					&& (transaction.getState() != TransactionState.NOTRUNNING)
					&& transaction.isValid() && !transaction.isReadOnly());
			if (transaction.changedAttributes == null) {
				transaction.changedAttributes = new HashMap<AttributedElement, Set<VersionedDataObject<?>>>(
						1, TransactionManagerImpl.LOAD_FACTOR);
			}
			Set<VersionedDataObject<?>> attributes = transaction.changedAttributes
					.get(this);
			if (attributes == null) {
				attributes = new HashSet<VersionedDataObject<?>>(1, 0.2f);
				transaction.changedAttributes.put(this, attributes);
			}
			attributes.add(versionedAttribute);
		}
	}

	@Override
	// TODO maybe support this?!
	public void defragment() {
		throw new UnsupportedOperationException(
				"Defragmentation of graph is not supported within the transaction concept.");
	}

	@Override
	protected int allocateVertexIndex(int currentId) {
		vertexSync.writeLock().lock();
		int vId = 0;
		try {
			Transaction transaction = getCurrentTransaction();
			assert (transaction.getState() != null);
			if (transaction.getState() == TransactionState.RUNNING) {
				synchronized (freeVertexList) {
					vId = freeVertexList.allocateIndex();
					if (vId == 0) {
						int newSize = getExpandedVertexCount();
						expandVertexArray(newSize);
						vId = freeVertexList.allocateIndex();
					}
				}
			} else {
				vId = currentId;
			}
		} finally {
			vertexSync.writeLock().unlock();
		}
		return vId;
	}

	@Override
	protected int allocateEdgeIndex(int currentId) {
		edgeSync.writeLock().lock();
		int eId = 0;
		try {
			Transaction transaction = getCurrentTransaction();
			assert (transaction.getState() != null);
			if (transaction.getState() == TransactionState.RUNNING) {
				eId = freeEdgeList.allocateIndex();
				if (eId == 0) {
					int newSize = getExpandedEdgeCount();
					expandEdgeArray(newSize);
					eId = freeEdgeList.allocateIndex();
				}
			} else {
				eId = currentId;
			}
		} finally {
			edgeSync.writeLock().unlock();
		}
		return eId;
	}

	@Override
	protected void freeEdgeIndex(int index) {
		Transaction transaction = getCurrentTransaction();
		assert ((transaction != null) && !transaction.isReadOnly() && (freeEdgeList != null));
		if ((transaction.getState() == TransactionState.COMMITTING)
				|| (transaction.getState() == TransactionState.ABORTING)) {
			synchronized (freeEdgeList) {
				if (isEdgeIndexReferenced(index)) {
					if (edgeIndexesToBeFreed == null) {
						edgeIndexesToBeFreed = new ArrayList<Integer>();
					}
					if (!edgeIndexesToBeFreed.contains(index)) {
						edgeIndexesToBeFreed.add(0, index);
					}
				} else {
					freeEdgeList.freeIndex(index);
					if (edgeIndexesToBeFreed != null) {
						edgeIndexesToBeFreed.remove((Object) index);
					}
				}
			}
		}
	}

	/**
	 * Checks whether an <code>Edge</code> with the given <code>index</code>
	 * exists for at least one other parallel running read-write
	 * <code>Transaction</code>. If so the given <code>index</code> may not be
	 * freed yet, but has to be marked as "to-be-freed" in the future by putting
	 * it into <code>edgeIndexesToBeFreed</code>.
	 * 
	 * @param index
	 * @return
	 */
	protected boolean isEdgeIndexReferenced(int index) {
		edgeSync.readLock().lock();
		boolean result = false;
		List<Transaction> transactionsList = transactionManager
				.getTransactions();
		Transaction currentTransaction = getCurrentTransaction();
		synchronized (transactionsList) {
			for (Transaction transaction : transactionsList) {
				if ((transaction != currentTransaction)
						&& (transaction.isValid()) && !transaction.isReadOnly()) {
					EdgeImpl[] tempEdge = edge
							.getPersistentValueAtBot(transaction);
					if (tempEdge != null) {
						if ((tempEdge.length > index)
								&& (tempEdge[index] != null)) {
							result = true;
							break;
						}
					}
				}
			}
		}
		edgeSync.readLock().unlock();
		return result;
	}

	@Override
	protected void freeVertexIndex(int index) {
		Transaction transaction = getCurrentTransaction();
		assert ((transaction != null) && !transaction.isReadOnly() && (freeVertexList != null));
		if ((transaction.getState() == TransactionState.COMMITTING)
				|| (transaction.getState() == TransactionState.ABORTING)) {
			synchronized (freeVertexList) {
				if (isVertexIndexReferenced(index)) {
					if (vertexIndexesToBeFreed == null) {
						vertexIndexesToBeFreed = new ArrayList<Integer>();
					}
					if (!vertexIndexesToBeFreed.contains(index)) {
						vertexIndexesToBeFreed.add(0, index);
					}
				} else {
					freeVertexList.freeIndex(index);
					if (vertexIndexesToBeFreed != null) {
						vertexIndexesToBeFreed.remove((Object) index);
					}
				}
			}
		}
	}

	/**
	 * Checks whether an <code>Vertex</code> with the given <code>index</code>
	 * exists for at least one other parallel running read-write
	 * <code>Transaction</code>. If so the given <code>index</code> may not be
	 * freed yet, but has to be marked as "to-be-freed" in the future by putting
	 * it into <code>vertexIndexesToBeFreed</code>.
	 * 
	 * @param index
	 * @return
	 */
	protected boolean isVertexIndexReferenced(int index) {
		vertexSync.readLock().lock();
		boolean result = false;
		List<Transaction> transactionsList = transactionManager
				.getTransactions();
		Transaction currentTransaction = getCurrentTransaction();
		synchronized (transactionsList) {
			for (Transaction transaction : transactionsList) {
				if ((transaction != currentTransaction)
						&& (transaction.isValid()) && !transaction.isReadOnly()) {
					VertexImpl[] tempVertex = vertex
							.getPersistentValueAtBot(transaction);
					if (tempVertex != null) {
						if ((tempVertex.length > index)
								&& (tempVertex[index] != null)) {
							result = true;
							break;
						}
					}
				}
			}
		}
		vertexSync.readLock().unlock();
		return result;
	}

	@Override
	protected boolean canAddGraphElement(int graphElementId) {
		Transaction transaction = getCurrentTransaction();
		assert (transaction != null);
		return ((transaction.getState() == TransactionState.WRITING) || (transaction
				.getState() == TransactionState.RUNNING));
	}

	@Override
	protected synchronized void expandVertexArray(int newSize) {
		if (vertexSync == null) {
			vertexSync = new ReentrantReadWriteLock(true);
		}
		vertexSync.writeLock().lock();
		try {
			if (newSize <= vMax) {
				throw new GraphException("newSize must > vSize: vSize=" + vMax
						+ ", newSize=" + newSize);
			}
			// mark if freeVertexList has been initialized in this method
			// invocation...
			boolean firstInit = false;
			// should be done with initialization of graph...
			if (freeVertexList == null) {
				firstInit = true;
				freeVertexList = new FreeIndexList(newSize);
			}
			synchronized (freeVertexList) {
				// initialization of vertex should be done with initialization
				// of graph...
				if (vertex == null) {
					vertex = new VersionedArrayImpl<VertexImpl[]>(this,
							new VertexImpl[newSize + 1]);
				} else {
					synchronized (vertex) {
						// expand all vertex-values for all active
						// transactions
						vertex.expandVertexArrays(newSize);
					}
				}
				// only expand, if freeVertexList hasn't been initialized in
				// this method invocation...
				if (!firstInit) {
					freeVertexList.expandBy(newSize - vMax);
				}
				vMax = newSize;
				notifyMaxVertexCountIncreased(newSize);
			}
		} finally {
			vertexSync.writeLock().unlock();
		}
	}

	@Override
	protected synchronized void expandEdgeArray(int newSize) {
		if (edgeSync == null) {
			edgeSync = new ReentrantReadWriteLock(true);
		}
		edgeSync.writeLock().lock();
		try {
			if (newSize <= eMax) {
				throw new GraphException("newSize must be > eSize: eSize="
						+ eMax + ", newSize=" + newSize);
			}
			// mark if freeEdgeList has been initialized in this method
			// invocation...
			boolean firstInit = false;
			if (freeEdgeList == null) {
				firstInit = true;
				freeEdgeList = new FreeIndexList(newSize);
			}
			synchronized (freeEdgeList) {
				// initialization edge and revEdge
				if (edge == null) {
					edge = new VersionedArrayImpl<EdgeImpl[]>(this,
							new EdgeImpl[newSize + 1]);
					assert (revEdge == null);
					revEdge = new VersionedArrayImpl<ReversedEdgeImpl[]>(this,
							new ReversedEdgeImpl[newSize + 1]);
				} else {
					// lock Array edge
					synchronized (edge) {
						edge.expandEdgeArrays(newSize);
						// lock Array revEdge
						synchronized (revEdge) {
							// expand all edge- and revEdge-values for all
							// active
							// transactions
							revEdge.expandRevEdgeArrays(newSize);
						}
					}
				}
				if (!firstInit) {
					freeEdgeList.expandBy(newSize - eMax);
				}
				eMax = newSize;
				notifyMaxEdgeCountIncreased(newSize);
			}
		} finally {
			edgeSync.writeLock().unlock();
		}
	}
	
	@Override
	protected void addEdge(Edge newEdge) {
		throw new UnsupportedOperationException();
	}	

//	@Override
//	protected void addEdge(Edge newEdge) {
//		if (isLoading()) {
//			super.addEdge(newEdge);
//		} else {
//			TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
//			if (transaction == null) {
//				throw new GraphException("Current transaction is null.");
//			}
//			if (transaction.isReadOnly()) {
//				throw new GraphException(
//						"Read-only transactions are not allowed to add edges.");
//			}
//			// It should not be possible to add newEdge, if alpha isn't
//			// valid in the current transaction.
//			if (!alpha.isValid()) {
//				throw new GraphException("Alpha-vertex " + alpha
//						+ " is not valid within the current transaction "
//						+ transaction + ".");
//			}
//			// It should not be possible to add newEdge, if omega isn't
//			// valid in the current transaction.
//			if (!omega.isValid()) {
//				throw new GraphException("Omega-vertex " + omega
//						+ " is not valid within the current transaction "
//						+ transaction + ".");
//			}
//			synchronized (transaction) {
//				// create temporary versions of edge and revEdge if not already
//				// existing
//				if (transaction.getState() == TransactionState.RUNNING) {
//					edgeSync.writeLock().lock();
//					edge.prepareValueChangeAfterReference(transaction);
//					revEdge.prepareValueChangeAfterReference(transaction);
//					edgeSync.writeLock().unlock();
//				}
//				try {
//					super.addEdge(newEdge, alpha, omega);
//				} catch (GraphException e) {
//					throw e;
//				}
//				assert ((transaction != null) && !transaction.isReadOnly()
//						&& transaction.isValid() && (transaction.getState() != TransactionState.NOTRUNNING));
//				if (transaction.getState() == TransactionState.RUNNING) {
//					if (transaction.addedEdges == null) {
//						transaction.addedEdges = new ArrayList<EdgeImpl>(1);
//					}
//					transaction.addedEdges
//							.add((de.uni_koblenz.jgralab.impl.trans.EdgeImpl) (newEdge));
//					if (transaction.deletedEdges != null) {
//						transaction.deletedEdges.remove(newEdge);
//					}
//				}
//			}
//		}
//	}

	@Override
	protected void addVertex(Vertex newVertex) {
		if (isLoading()) {
			super.addVertex(newVertex);
		} else {
			TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
			if (transaction == null) {
				throw new GraphException("Current transaction is null.");
			}
			if (transaction.isReadOnly()) {
				throw new GraphException(
						"Read-only transactions are not allowed to add vertices.");
			}
			synchronized (transaction) {
				if (transaction.getState() == TransactionState.RUNNING) {
					vertexSync.writeLock().lock();
					vertex.prepareValueChangeAfterReference(transaction);
					vertexSync.writeLock().unlock();
				}
				try {
					super.addVertex(newVertex);
				} catch (GraphException e) {
					throw e;
				}
				assert ((transaction != null) && !transaction.isReadOnly()
						&& transaction.isValid() && (transaction.getState() != TransactionState.NOTRUNNING));
				if (transaction.getState() == TransactionState.RUNNING) {
					if (transaction.addedVertices == null) {
						transaction.addedVertices = new ArrayList<VertexImpl>(1);
					}
					transaction.addedVertices
							.add((de.uni_koblenz.jgralab.impl.trans.VertexImpl) (newVertex));
					if (transaction.deletedVertices != null) {
						transaction.deletedVertices.remove(newVertex);
					}
				}
			}
		}
	}

	@Override
	protected Edge internalCreateEdge(Class<? extends Edge> cls) {
		return graphFactory.createEdgeWithTransactionSupport(cls, 0, this);
	}

	@Override
	protected Vertex internalCreateVertex(Class<? extends Vertex> cls) {
		return graphFactory.createVertexWithTransactionSupport(cls, 0, this);
	}

	@Override
	public void deleteEdge(Edge edgeToBeDeleted) {
		TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
		if (transaction == null) {
			throw new GraphException("Current transaction is null.");
		}
		if (transaction.isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to delete edges.");
		}
		// It should not be possible to delete edgeToBeDeleted, if it isn't
		// valid in the current transaction.
		if (!edgeToBeDeleted.isValid()) {
			throw new GraphException("Edge " + edgeToBeDeleted
					+ " isn't valid within current transaction.");
		}
		synchronized (transaction) {
			if (transaction.getState() == TransactionState.RUNNING) {
				edgeSync.writeLock().lock();
				edge.prepareValueChangeAfterReference(transaction);
				revEdge.prepareValueChangeAfterReference(transaction);
				edgeSync.writeLock().unlock();
			}
			try {
				super.deleteEdge(edgeToBeDeleted);
			} catch (GraphException e) {
				throw e;
			}

		}
	}

	@Override
	protected void edgeAfterDeleted(Edge edgeToBeDeleted) {
		TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
		assert ((transaction != null) && !transaction.isReadOnly()
				&& transaction.isValid() && (transaction.getState() != TransactionState.NOTRUNNING));
		if (transaction.getState() == TransactionState.RUNNING) {
			if ((transaction.addedEdges != null)
					&& transaction.addedEdges.contains(edgeToBeDeleted)) {
				transaction.addedEdges.remove(edgeToBeDeleted);
			} else {
				if (transaction.deletedEdges == null) {
					transaction.deletedEdges = new ArrayList<EdgeImpl>(1);
				}
				transaction.deletedEdges
						.add((de.uni_koblenz.jgralab.impl.trans.EdgeImpl) (edgeToBeDeleted
								));
			}
			// delete references to edgeToBeDeleted in other change sets
			if (transaction.changedAttributes != null) {
				transaction.changedAttributes.remove(edgeToBeDeleted);
			}
			if (transaction.changedEdges != null) {
				transaction.changedEdges.remove(edgeToBeDeleted);
			}
			throw new UnsupportedOperationException();
			//TODO: Incidences anpassend
//			if (transaction.changedEseqEdges != null) {
//				transaction.changedEseqEdges.remove(edgeToBeDeleted);
//				Edge prevEdge = edgeToBeDeleted.getPrevIncidence();
//				if (transaction.changedEseqEdges.containsKey(prevEdge)) {
//					if (transaction.changedEseqEdges.get(prevEdge).containsKey(
//							ListPosition.NEXT)) {
//						transaction.changedEseqEdges.remove(prevEdge);
//					}
//				}
//				Edge nextEdge = edgeToBeDeleted.getNextIncidence();
//				// check if current (temporary) nextEdge has been changed
//				// explicitly
//				if (transaction.changedEseqEdges.containsKey(nextEdge)) {
//					if (transaction.changedEseqEdges.get(nextEdge).containsKey(
//							ListPosition.PREV)) {
//						transaction.changedEseqEdges.remove(nextEdge);
//					}
//				}
//			}
//			if (transaction.changedIncidences != null) {
//				// remove edgeToBeDeleted from incidence lists
//				Map<IncidenceImpl, Map<ListPosition, Boolean>> changedAlphaIncidences = transaction.changedIncidences
//						.get(oldAlpha);
//				if (changedAlphaIncidences != null) {
//					changedAlphaIncidences.remove(edgeToBeDeleted);
//				}
//				Map<IncidenceImpl, Map<ListPosition, Boolean>> changedOmegaIncidences = transaction.changedIncidences
//						.get(oldOmega);
//				if (changedOmegaIncidences != null) {
//					changedOmegaIncidences.remove(edgeToBeDeleted);
//				}
//			}
		}
	}

	@Override
	public void deleteVertex(Vertex vertexToBeDeleted) {
		TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
		if (transaction == null) {
			throw new GraphException("Current transaction is null.");
		}
		if (transaction.isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to delete vertices.");
		}
		// It should not be possible to delete vertexToBeDeleted, if it isn't
		// valid in
		// the current transaction.
		if (!vertexToBeDeleted.isValid()) {
			throw new GraphException("Vertex " + vertexToBeDeleted
					+ " isn't valid within current transaction.");
		}
		synchronized (transaction) {
			if (transaction.getState() == TransactionState.RUNNING) {
				vertexSync.writeLock().lock();
				vertex.prepareValueChangeAfterReference(transaction);
				vertexSync.writeLock().unlock();
			}
			try {
				super.deleteVertex(vertexToBeDeleted);
			} catch (GraphException e) {
				throw e;
			}
		}
	}

	@Override
	protected void vertexAfterDeleted(Vertex vertexToBeDeleted) {
		TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
		assert ((transaction != null) && !transaction.isReadOnly()
				&& transaction.isValid() && (transaction.getState() != TransactionState.NOTRUNNING));
		if (transaction.getState() == TransactionState.RUNNING) {
			if ((transaction.addedVertices != null)
					&& transaction.addedVertices.contains(vertexToBeDeleted)) {
				transaction.addedVertices.remove(vertexToBeDeleted);
			} else {
				if (transaction.deletedVertices == null) {
					// transaction.deletedVertices = new HashSet<VertexImpl>(1,
					// 0.2f);
					transaction.deletedVertices = new ArrayList<VertexImpl>(1);
				}
				transaction.deletedVertices
						.add((de.uni_koblenz.jgralab.impl.trans.VertexImpl) (vertexToBeDeleted));
			}
			if (transaction.changedAttributes != null) {
				// delete references to vertexToBeDeleted in other change sets
				transaction.changedAttributes.remove(vertexToBeDeleted);
			}
			if (transaction.changedVseqVertices != null) {
				transaction.changedVseqVertices.remove(vertexToBeDeleted);
				Vertex prevVertex = vertexToBeDeleted.getPrevVertex();
				if (transaction.changedVseqVertices.containsKey(prevVertex)) {
					if (transaction.changedVseqVertices.get(prevVertex)
							.containsKey(ListPosition.NEXT)) {
						transaction.changedVseqVertices.remove(prevVertex);
					}
				}
				Vertex nextVertex = vertexToBeDeleted.getNextVertex();
				// check if current (temporary) nextVertex has been changed
				// explicitly
				if (transaction.changedVseqVertices.containsKey(nextVertex)) {
					if (transaction.changedVseqVertices.get(nextVertex)
							.containsKey(ListPosition.PREV)) {
						transaction.changedVseqVertices.remove(nextVertex);
					}
				}
			}
			throw new UnsupportedOperationException();
//			if (transaction.changedIncidences != null) {
//				transaction.changedIncidences.remove(vertexToBeDeleted);
//			}
		}
		if (transaction.getState() == TransactionState.WRITING) {
			if (transaction.deletedVerticesWhileWriting == null) {
				transaction.deletedVerticesWhileWriting = new ArrayList<VertexImpl>(
						1);
			}
			transaction.deletedVerticesWhileWriting
					.add((de.uni_koblenz.jgralab.impl.trans.VertexImpl) vertexToBeDeleted);
		}

	}

	@Override
	public void putEdgeBeforeInGraph(
			de.uni_koblenz.jgralab.impl.EdgeBaseImpl targetEdge,
			de.uni_koblenz.jgralab.impl.EdgeBaseImpl movedEdge) {
		TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
		if (transaction == null) {
			throw new GraphException("Current transaction is null.");
		}
		// It should not be possible to execute this method, if targetEdge
		// isn't valid
		// in the current transaction.
		if (!targetEdge.isValid()) {
			throw new GraphException("Edge " + targetEdge
					+ " is not valid within the current transaction "
					+ transaction + ".");
		}
		// It should not be possible to execute this method, if movedEdge
		// isn't valid
		// in the current transaction.
		if (!movedEdge.isValid()) {
			throw new GraphException("Edge " + movedEdge
					+ " is not valid within the current transaction "
					+ transaction + ".");
		}
		synchronized (transaction) {
			super.putEdgeBeforeInGraph(targetEdge, movedEdge);
			assert ((transaction != null) && !transaction.isReadOnly()
					&& transaction.isValid() && (transaction.getState() != TransactionState.NOTRUNNING));
			if (transaction.getState() == TransactionState.RUNNING) {
				if (transaction.changedEseqEdges == null) {
					transaction.changedEseqEdges = new HashMap<EdgeImpl, Map<ListPosition, Boolean>>(
							1, TransactionManagerImpl.LOAD_FACTOR);
				}
				Map<ListPosition, Boolean> positionsMap = transaction.changedEseqEdges
						.get(movedEdge);
				if (positionsMap == null) {
					positionsMap = new HashMap<ListPosition, Boolean>(1,
							TransactionManagerImpl.LOAD_FACTOR);
				}
				positionsMap.put(ListPosition.NEXT, true);
				if (transaction.changedEseqEdges.get(movedEdge) == null) {
					transaction.changedEseqEdges
							.put(
									(de.uni_koblenz.jgralab.impl.trans.EdgeImpl) movedEdge,
									positionsMap);
				}
				positionsMap = transaction.changedEseqEdges.get(targetEdge);
				if (positionsMap == null) {
					positionsMap = new HashMap<ListPosition, Boolean>(1,
							TransactionManagerImpl.LOAD_FACTOR);
				}
				positionsMap.put(ListPosition.PREV, false);
				if (transaction.changedEseqEdges.get(targetEdge) == null) {
					transaction.changedEseqEdges
							.put(
									(de.uni_koblenz.jgralab.impl.trans.EdgeImpl) targetEdge,
									positionsMap);
				}
			}
		}
	}

	@Override
	public void putEdgeAfterInGraph(
			de.uni_koblenz.jgralab.impl.EdgeBaseImpl targetEdge,
			de.uni_koblenz.jgralab.impl.EdgeBaseImpl movedEdge) {
		TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
		if (transaction == null) {
			throw new GraphException("Current transaction is null.");
		}
		// It should not be possible to execute this method, if targetEdge
		// isn't valid
		// in the current transaction.
		if (!targetEdge.isValid()) {
			throw new GraphException("Edge " + targetEdge
					+ " is not valid within the current transaction "
					+ transaction + ".");
		}
		// It should not be possible to execute this method, if movedEdge
		// isn't valid
		// in the current transaction.
		if (!movedEdge.isValid()) {
			throw new GraphException("Edge " + movedEdge
					+ " is not valid within the current transaction "
					+ transaction + ".");
		}
		synchronized (transaction) {
			super.putEdgeAfterInGraph(targetEdge, movedEdge);
			assert ((transaction != null) && !transaction.isReadOnly()
					&& transaction.isValid() && (transaction.getState() != TransactionState.NOTRUNNING));
			if (transaction.getState() == TransactionState.RUNNING) {
				if (transaction.changedEseqEdges == null) {
					transaction.changedEseqEdges = new HashMap<EdgeImpl, Map<ListPosition, Boolean>>(
							1, TransactionManagerImpl.LOAD_FACTOR);
				}
				Map<ListPosition, Boolean> positionsMap = transaction.changedEseqEdges
						.get(movedEdge);
				if (positionsMap == null) {
					positionsMap = new HashMap<ListPosition, Boolean>(1,
							TransactionManagerImpl.LOAD_FACTOR);
				}
				positionsMap.put(ListPosition.PREV, true);
				if (transaction.changedEseqEdges.get(movedEdge) == null) {
					transaction.changedEseqEdges
							.put(
									(de.uni_koblenz.jgralab.impl.trans.EdgeImpl) movedEdge,
									positionsMap);
				}
				positionsMap = transaction.changedEseqEdges.get(targetEdge);
				if (positionsMap == null) {
					positionsMap = new HashMap<ListPosition, Boolean>(1,
							TransactionManagerImpl.LOAD_FACTOR);
				}
				positionsMap.put(ListPosition.NEXT, false);
				if (transaction.changedEseqEdges.get(targetEdge) == null) {
					transaction.changedEseqEdges
							.put(
									(de.uni_koblenz.jgralab.impl.trans.EdgeImpl) targetEdge,
									positionsMap);
				}
			}
		}
	}

	@Override
	protected void putVertexAfter(
			de.uni_koblenz.jgralab.impl.VertexBaseImpl targetVertex,
			de.uni_koblenz.jgralab.impl.VertexBaseImpl movedVertex) {
		TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
		if (transaction == null) {
			throw new GraphException("Current transaction is null.");
		}
		// It should not be possible to execute this method, if targetVertex
		// isn't valid
		// in the current transaction.
		if (!targetVertex.isValid()) {
			throw new GraphException("Edge " + targetVertex
					+ " is not valid within the current transaction "
					+ transaction + ".");
		}
		// It should not be possible to execute this method, if movedVertex
		// isn't valid
		// in the current transaction.
		if (!movedVertex.isValid()) {
			throw new GraphException("Edge " + movedVertex
					+ " is not valid within the current transaction "
					+ transaction + ".");
		}
		synchronized (transaction) {
			super.putVertexAfter(targetVertex, movedVertex);
			assert ((transaction != null) && !transaction.isReadOnly()
					&& transaction.isValid() && (transaction.getState() != TransactionState.NOTRUNNING));
			if (transaction.getState() == TransactionState.RUNNING) {
				if (transaction.changedVseqVertices == null) {
					transaction.changedVseqVertices = new HashMap<VertexImpl, Map<ListPosition, Boolean>>(
							1, TransactionManagerImpl.LOAD_FACTOR);
				}
				Map<ListPosition, Boolean> positionsMap = transaction.changedVseqVertices
						.get(movedVertex);
				if (positionsMap == null) {
					positionsMap = new HashMap<ListPosition, Boolean>(1,
							TransactionManagerImpl.LOAD_FACTOR);
				}
				positionsMap.put(ListPosition.PREV, true);
				if (transaction.changedVseqVertices.get(movedVertex) == null) {
					transaction.changedVseqVertices
							.put(
									(de.uni_koblenz.jgralab.impl.trans.VertexImpl) movedVertex,
									positionsMap);
				}
				positionsMap = transaction.changedVseqVertices
						.get(targetVertex);
				if (positionsMap == null) {
					positionsMap = new HashMap<ListPosition, Boolean>(1,
							TransactionManagerImpl.LOAD_FACTOR);
				}
				positionsMap.put(ListPosition.NEXT, false);
				if (transaction.changedVseqVertices.get(targetVertex) == null) {
					transaction.changedVseqVertices
							.put(
									(de.uni_koblenz.jgralab.impl.trans.VertexImpl) targetVertex,
									positionsMap);
				}
			}
		}
	}

	@Override
	protected void putVertexBefore(
			de.uni_koblenz.jgralab.impl.VertexBaseImpl targetVertex,
			de.uni_koblenz.jgralab.impl.VertexBaseImpl movedVertex) {
		TransactionImpl transaction = (TransactionImpl) getCurrentTransaction();
		if (transaction == null) {
			throw new GraphException("Current transaction is null.");
		}
		// It should not be possible to execute this method, if targetVertex
		// isn't valid
		// in the current transaction.
		if (!targetVertex.isValid()) {
			throw new GraphException("Edge " + targetVertex
					+ " is not valid within the current transaction "
					+ transaction + ".");
		}
		// It should not be possible to execute this method, if movedVertex
		// isn't valid
		// in the current transaction.
		if (!movedVertex.isValid()) {
			throw new GraphException("Edge " + movedVertex
					+ " is not valid within the current transaction "
					+ transaction + ".");
		}
		synchronized (transaction) {
			super.putVertexBefore(targetVertex, movedVertex);
			assert ((transaction != null) && !transaction.isReadOnly()
					&& transaction.isValid() && (transaction.getState() != TransactionState.NOTRUNNING));
			if (transaction.getState() == TransactionState.RUNNING) {
				if (transaction.changedVseqVertices == null) {
					transaction.changedVseqVertices = new HashMap<VertexImpl, Map<ListPosition, Boolean>>(
							1, TransactionManagerImpl.LOAD_FACTOR);
				}
				Map<ListPosition, Boolean> positionsMap = transaction.changedVseqVertices
						.get(movedVertex);
				if (positionsMap == null) {
					positionsMap = new HashMap<ListPosition, Boolean>(1,
							TransactionManagerImpl.LOAD_FACTOR);
				}
				positionsMap.put(ListPosition.NEXT, true);
				if (transaction.changedVseqVertices.get(movedVertex) == null) {
					transaction.changedVseqVertices
							.put(
									(de.uni_koblenz.jgralab.impl.trans.VertexImpl) movedVertex,
									positionsMap);
				}
				positionsMap = transaction.changedVseqVertices
						.get(targetVertex);
				if (positionsMap == null) {
					positionsMap = new HashMap<ListPosition, Boolean>(1,
							TransactionManagerImpl.LOAD_FACTOR);
				}
				positionsMap.put(ListPosition.PREV, false);
				if (transaction.changedVseqVertices.get(targetVertex) == null) {
					transaction.changedVseqVertices
							.put(
									(de.uni_koblenz.jgralab.impl.trans.VertexImpl) targetVertex,
									positionsMap);
				}
			}
		}
	}

	@Override
	public void internalLoadingCompleted(int[] firstIncidence,
			int[] nextIncidence) {
		setLoading(true);
		super.internalLoadingCompleted(firstIncidence, nextIncidence);
	}

	@Override
	public void loadingCompleted() {
		setLoading(false);
	}

	@Override
	public Iterable<Vertex> getVertices() {
		return new AttributedElementIterable<Vertex>(super.getVertices(), this);
	}

	@Override
	public Iterable<Vertex> getVertices(Class<? extends Vertex> vertexClass) {
		return new AttributedElementIterable<Vertex>(super
				.getVertices(vertexClass), this);
	}

	@Override
	public Iterable<Vertex> getVertices(VertexClass vertexClass) {
		return new AttributedElementIterable<Vertex>(super
				.getVertices(vertexClass), this);
	}

	@Override
	public Iterable<Edge> getEdges() {
		return new AttributedElementIterable<Edge>(super.getEdges(), this);
	}

	@Override
	public Iterable<Edge> getEdges(Class<? extends Edge> edgeClass) {
		return new AttributedElementIterable<Edge>(super.getEdges(edgeClass), this);
	}

	@Override
	public Iterable<Edge> getEdges(EdgeClass edgeClass) {
		return new AttributedElementIterable<Edge>(super.getEdges(edgeClass), this);
	}

	@Override
	protected void appendEdgeToESeq(de.uni_koblenz.jgralab.impl.EdgeBaseImpl e) {
		edgeSync.readLock().lock();
		try {
			super.appendEdgeToESeq(e);
		} finally {
			edgeSync.readLock().unlock();
		}
	}

	@Override
	protected void appendVertexToVSeq(
			de.uni_koblenz.jgralab.impl.VertexBaseImpl v) {
		vertexSync.readLock().lock();
		try {
			super.appendVertexToVSeq(v);
		} finally {
			vertexSync.readLock().unlock();
		}
	}

	/**
	 * Trying to free cached <code>Vertex</code>- and <code>Edge</code> indexes.
	 */
	protected void freeStoredIndexes() {
		synchronized (freeVertexList) {
			if (vertexIndexesToBeFreed != null) {
				for (Integer index : new ArrayList<Integer>(
						vertexIndexesToBeFreed)) {
					freeVertexIndex(index);
				}
			}
		}
		synchronized (freeEdgeList) {
			if (edgeIndexesToBeFreed != null) {
				for (Integer index : new ArrayList<Integer>(
						edgeIndexesToBeFreed)) {
					freeEdgeIndex(index);
				}
			}
		}
	}

	@Override
	public final boolean hasStandardSupport() {
		return false;
	}

	@Override
	public final boolean hasTransactionSupport() {
		return true;
	}

	@Override
	public final boolean hasSavememSupport() {
		return false;
	}
	
	@Override
	public final boolean hasDatabaseSupport(){
		return false;
	}

	@Override
	public <T> JGraLabList<T> createList() {
		if (!isLoading() && getCurrentTransaction().isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to create Lists.");
		}
		return new JGraLabListImpl<T>(this);
	}

	@Override
	public <T> JGraLabList<T> createList(Collection<? extends T> collection) {
		if (!isLoading() && getCurrentTransaction().isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to create Lists.");
		}
		return new JGraLabListImpl<T>(this, collection);
	}

	@Override
	public <T> JGraLabList<T> createList(int initialCapacity) {
		if (!isLoading() && getCurrentTransaction().isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to create Lists.");
		}
		return new JGraLabListImpl<T>(this, initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet() {
		if (!isLoading() && getCurrentTransaction().isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to create Sets.");
		}
		return new JGraLabSetImpl<T>(this);
	}

	@Override
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection) {
		if (!isLoading() && getCurrentTransaction().isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to create Sets.");
		}
		return new JGraLabSetImpl<T>(this, collection);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity) {
		if (!isLoading() && getCurrentTransaction().isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to create Sets.");
		}
		return new JGraLabSetImpl<T>(this, initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor) {
		if (!isLoading() && getCurrentTransaction().isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to create Sets.");
		}
		return new JGraLabSetImpl<T>(this, initialCapacity, loadFactor);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap() {
		if (!isLoading() && getCurrentTransaction().isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to create Maps.");
		}
		return new JGraLabMapImpl<K, V>(this);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map) {
		if (!isLoading() && getCurrentTransaction().isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to create Maps.");
		}
		return new JGraLabMapImpl<K, V>(this, map);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity) {
		if (!isLoading() && getCurrentTransaction().isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to create Maps.");
		}
		return new JGraLabMapImpl<K, V>(this, initialCapacity);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) {
		if (!isLoading() && getCurrentTransaction().isReadOnly()) {
			throw new GraphException(
					"Read-only transactions are not allowed to create Maps.");
		}
		return new JGraLabMapImpl<K, V>(this, initialCapacity, loadFactor);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) {
		T record = graphFactory.createRecordWithTransactionSupport(recordClass,
				this);
		try {
			record.readComponentValues(io);
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) {
		T record = graphFactory.createRecordWithTransactionSupport(recordClass,
				this);
		record.setComponentValues(fields);
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Object... components) {
		T record = graphFactory.createRecordWithTransactionSupport(recordClass,
				this);
		record.setComponentValues(components);
		return record;
	}

	private boolean isWriting() {
		return getCurrentTransaction().getState() == TransactionState.WRITING;
	}

	@Override
	protected void internalVertexDeleted(
			de.uni_koblenz.jgralab.impl.VertexBaseImpl v) {
		if (isWriting()) {
			super.internalVertexDeleted(v);
		}
	}

	@Override
	protected void internalVertexAdded(
			de.uni_koblenz.jgralab.impl.VertexBaseImpl v) {
		if (isWriting()) {
			super.internalVertexAdded(v);
		}
	}

	@Override
	protected void internalEdgeDeleted(
			de.uni_koblenz.jgralab.impl.EdgeBaseImpl e) {
		if (isWriting()) {
			super.internalEdgeDeleted(e);
		}
	}

	@Override
	protected void internalEdgeAdded(de.uni_koblenz.jgralab.impl.EdgeBaseImpl e) {
		if (isWriting()) {
			super.internalEdgeAdded(e);
		}
	}

	@Override
	public void addGraphStructureChangedListener(
			GraphStructureChangedListener newListener) {
		synchronized (graphStructureChangedListeners) {
			super.addGraphStructureChangedListener(newListener);
		}
	}

	@Override
	public void removeGraphStructureChangedListener(
			GraphStructureChangedListener listener) {
		synchronized (graphStructureChangedListeners) {
			super.removeGraphStructureChangedListener(listener);
		}
	}

	@Override
	public void removeAllGraphStructureChangedListeners() {
		synchronized (graphStructureChangedListeners) {
			super.removeAllGraphStructureChangedListeners();
		}
	}

	@Override
	public void initializeAttributesWithDefaultValues() {
		try {
			Transaction defaultValuesTransaction = newTransaction();
			super.initializeAttributesWithDefaultValues();
			defaultValuesTransaction.commit();
		} catch (Exception e) {
			throw new GraphException(
					"The initialization for the default values of the graph failed.\n"
							+ " Caused by: " + e.getClass() + " - "
							+ e.getLocalizedMessage(), e);
		}
	}

	@Override
	protected void internalSetDefaultValue(Attribute attr)
			throws GraphIOException {
		attr.setDefaultTransactionValue(this);
	}
}
