package com.zxb.model;

import java.io.Serializable;


public class RateModel  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * province name
     */
    private String IDFCHANNEL;
    private String DFEERAT;
    private String IDFID;
    

    public String getIDFCHANNEL() {
		return IDFCHANNEL;
	}

	public void setIDFCHANNEL(String iDFCHANNEL) {
		IDFCHANNEL = iDFCHANNEL;
	}

	public String getDFEERAT() {
		return DFEERAT;
	}

	public void setDFEERAT(String dFEERAT) {
		DFEERAT = dFEERAT;
	}

	public String getIDFID() {
		return IDFID;
	}

	public void setIDFID(String iDFID) {
		IDFID = iDFID;
	}

    public RateModel(){
    	super();
    }

   
    @Override
    public String toString() {
        return IDFCHANNEL;
    }
}