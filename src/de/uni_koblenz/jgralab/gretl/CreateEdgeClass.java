package de.uni_koblenz.jgralab.gretl;

import de.uni_koblenz.jgralab.greql2.jvalue.JValueSet;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class CreateEdgeClass extends Transformation<EdgeClass> {

	protected String qualifiedName;
	private IncidenceClassSpec from;
	private IncidenceClassSpec to;
	private JValueSet archetypes;
	private String semanticExpression;

	protected CreateEdgeClass(final Context c, final String qualifiedName,
			final IncidenceClassSpec from, final IncidenceClassSpec to) {
		super(c);
		this.qualifiedName = qualifiedName;
		this.from = from;
		this.to = to;
	}

	public CreateEdgeClass(final Context c, final String qualifiedName,
			final IncidenceClassSpec from, final IncidenceClassSpec to,
			final JValueSet archetypes) {
		this(c, qualifiedName, from, to);
		this.archetypes = archetypes;
	}

	public CreateEdgeClass(final Context c, final String qualifiedName,
			final IncidenceClassSpec from, final IncidenceClassSpec to,
			final String semanticExpression) {
		this(c, qualifiedName, from, to);
		this.semanticExpression = semanticExpression;
	}

	public static CreateEdgeClass parseAndCreate(ExecuteTransformation et) {
		String qname = et.matchQualifiedName();
		IncidenceClassSpec from = et.matchIncidenceClassSpec();
		IncidenceClassSpec to = et.matchIncidenceClassSpec();
		et.matchTransformationArrow();
		String semanticExpression = et.matchSemanticExpression();
		return new CreateEdgeClass(et.context, qname, from, to,
				semanticExpression);
	}

	@Override
	protected EdgeClass transform() {
		switch (context.phase) {
		case SCHEMA:
			if ((from.aggregationKind != AggregationKind.NONE)
					&& (to.aggregationKind != AggregationKind.NONE)) {
				throw new GReTLException(context, "Only one IncidenceClass of "
						+ qualifiedName + " map be SHARED or COMPOSITE.");
			}

			setDefaultMultiplicities(from, to);
			setDefaultMultiplicities(to, from);

			EdgeClass element = context.targetSchema
					.getGraphClass()
					.createEdgeClass(qualifiedName, from.connectedVertexClass,
							from.minMultiplicity, from.maxMultiplicity,
							from.roleName, from.aggregationKind,
							to.connectedVertexClass, to.minMultiplicity,
							to.maxMultiplicity, to.roleName, to.aggregationKind);
			context.ensureMappings(element);
			return element;
		case GRAPH:
			EdgeClass edgeClass = context.targetGraph.getGraphClass()
					.getEdgeClass(qualifiedName);

			if (archetypes != null) {
				new CreateEdges(context, edgeClass, archetypes).execute();
			} else {
				new CreateEdges(context, edgeClass, semanticExpression)
						.execute();
			}

			return edgeClass;
		default:
			throw new GReTLException(context, "Unknown TransformationPhase "
					+ context.phase + "!");
		}
	}

	public static final class IncidenceClassSpec {
		private boolean defaultMultis = false;
		protected VertexClass connectedVertexClass = null;
		protected int minMultiplicity = 0;
		protected int maxMultiplicity = Integer.MAX_VALUE;
		protected String roleName = null;
		protected AggregationKind aggregationKind = AggregationKind.NONE;

		/**
		 * @param connectedVC
		 *            the connected {@link VertexClass}
		 * @param min
		 *            the minimum multiplicity
		 * @param max
		 *            the maximum multiplicity
		 * @param roleName
		 *            the role name
		 * @param aggrKind
		 *            the aggregation kind
		 */
		public IncidenceClassSpec(final VertexClass connectedVC, int min,
				int max, String roleName, AggregationKind aggrKind) {
			connectedVertexClass = connectedVC;
			minMultiplicity = min;
			maxMultiplicity = max;
			aggregationKind = aggrKind;
			this.roleName = roleName;
			if ((min == -1) || (max == -1)) {
				defaultMultis = true;
			}
		}

		public IncidenceClassSpec(final VertexClass connectedVC, int min,
				int max, String roleName) {
			this(connectedVC, min, max, roleName, AggregationKind.NONE);
		}

		public IncidenceClassSpec(final VertexClass connectedVC, int min,
				int max) {
			this(connectedVC, min, max, "", AggregationKind.NONE);
		}

		public IncidenceClassSpec(final VertexClass connectedVC,
				AggregationKind aggrKind, String roleName) {
			this(connectedVC, -1, -1, roleName, aggrKind);
		}

		public IncidenceClassSpec(final VertexClass connectedVC,
				AggregationKind aggrKind) {
			this(connectedVC, aggrKind, "");
		}

		public IncidenceClassSpec(final VertexClass connectedVC, String roleName) {
			this(connectedVC, -1, -1, roleName);
		}

		public IncidenceClassSpec(final VertexClass connectedVC) {
			this(connectedVC, "");
		}

		public IncidenceClassSpec(final VertexClass connectedVC, int min,
				int max, AggregationKind aggrKind) {
			this(connectedVC, min, max, "", aggrKind);
		}

		/**
		 * Creates a new {@link IncidenceClassSpec} connected to
		 * <code>connectedVC</code> and copying all information from
		 * <code>incClass</code>.
		 * 
		 * @param connectedVC
		 * @param incClass
		 */
		public IncidenceClassSpec(final VertexClass connectedVC,
				IncidenceClass incClass) {
			this(connectedVC, incClass.getMin(), incClass.getMax(), incClass
					.getRolename(), incClass.getAggregationKind());
		}

	}

	private void setDefaultMultiplicities(IncidenceClassSpec specToBeSet,
			IncidenceClassSpec oppositeSpec) {
		if (specToBeSet.defaultMultis) {
			if (oppositeSpec.aggregationKind == AggregationKind.COMPOSITE) {
				specToBeSet.minMultiplicity = 1;
				specToBeSet.maxMultiplicity = 1;
			} else {
				specToBeSet.minMultiplicity = 0;
				specToBeSet.maxMultiplicity = Integer.MAX_VALUE;
			}
		}
		specToBeSet.defaultMultis = false;
	}
}
