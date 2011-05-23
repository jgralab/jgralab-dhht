package de.uni_koblenz.jgralab.impl.disk;

public interface RemoteDiskStorageAccess {

	public void setFirstIncidence(long elementId, int id);

	public void setLastIncidence(long elementId, int id);

	public int getSigma(long elementId);

	public int getKappa(long elementId);

	public void setKappa(long elementId, int kappa);

}
