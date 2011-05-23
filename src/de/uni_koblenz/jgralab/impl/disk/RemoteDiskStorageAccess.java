package de.uni_koblenz.jgralab.impl.disk;

public interface RemoteDiskStorageAccess {

	public void setFirstIncidence(long elementId, long l);

	public void setLastIncidence(long elementId, long id);

	public int getSigma(long elementId);

	public int getKappa(long elementId);

	public void setKappa(long elementId, int kappa);

}
