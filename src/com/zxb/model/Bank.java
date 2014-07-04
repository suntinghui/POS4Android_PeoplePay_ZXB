package com.zxb.model;

import java.io.Serializable;


public class Bank  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * province name
     */
    private String name;

    /**
     * province code
     */
    private int code;

    public Bank(String name, int code) {
        super();
        this.name = name;
        this.code = code;
    }

    public Bank(){
    	super();
    }
    /**
     * Getter of name
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of name
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of code
     * 
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * Setter of code
     * 
     * @param code
     *            the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }


    /**
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}