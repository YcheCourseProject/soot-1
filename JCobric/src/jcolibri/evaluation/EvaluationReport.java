/**
 * EvaluationReport.java
 * jCOLIBRI2 framework. 
 * @author Juan A. Recio-Garc�a.
 * GAIA - Group for Artificial Intelligence Applications
 * http://gaia.fdi.ucm.es
 * 07/05/2007
 */
package jcolibri.evaluation;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

/**
 * This class stores the result of an evaluation. It is configured and filled by an Evaluator.
 * This info is also used to represent graphically the result of an evaluation. 
 * The stored information can be:
 * <ul>
 * <li>Several series of data. The lengh of the series is the number of executed cycles. And several series can be stored using diferent labels.
 * <li>Any other information. The put/getOtherData() methods allow to store any other kind of data.
 * <li>Number of cycles.
 * <li>Total evaluation time.
 * <li>Time per cycle.
 * </ul>
 * 
 * @author Juan A. Recio Garc�a
 * @version 2.0
 */
public class EvaluationReport
{
    private long totalTime;
    private int numberOfCycles;
    
    /** Stores the series info */
    protected Hashtable data;
    
    /** Stores other info */
    protected Hashtable other;
    
    /** Default constructor */
    public EvaluationReport()
    {
        data = new Hashtable();
        other = new Hashtable();
    }

    /**
     * Gets the evaluation info identified by the label
     * @param label Identifies the evaluation serie.
     */
    public Vector getSeries(String label)
    {
        return (Vector)data.get(label);
    }
    
    /**
     * Stes the evaluation info
     * @param label Identifier of the info
     * @param evaluation Evaluation result
     */
    public void setSeries(String label, Vector<Double> evaluation)
    {
   
        data.put(label, evaluation);
    }
    
    public void addDataToSeries(String label, Double value)
    {
    	Vector v = (Vector)data.get(label);
    	if(v == null)
    	{
    		v = new Vector();
    		data.put(label, v);
    	}
    	v.add(value);
    }
    
    /** 
     * Returns the name of the contained evaluation series
     */
    public String[] getSeriesLabels()
    {
        Set set = data.keySet();
        String[] res = new String[set.size()];
        int i=0;
        for(Object o : set){
        	String e = (String)o;
        	res[i++] = e;
        }
            
        return res;
    }

    
    public void putOtherData(String label, String data)
    {
    	this.other.put(label, data);
    }
    
    public String getOtherData(String label)
    {
    	return (String)this.other.get(label);
    }
    
    public String[] getOtherLabels()
    {
        Set set = other.keySet();
        String[] res = new String[set.size()];
        int i=0;
        for(Object o : set){
        	String e = (String)o;
        	res[i++] = e;
        }
        return res; 	
    }
    
    
    
    public int getNumberOfCycles()
    {
        return numberOfCycles;
    }

    public void setNumberOfCycles(int numberOfCycles)
    {
        this.numberOfCycles = numberOfCycles;
    }

    public long getTotalTime()
    {
        return totalTime;
    }

    public void setTotalTime(long totalTime)
    {
        this.totalTime = totalTime;
    }
    
    public double getTimePerCycle()
    {
        return (double)this.totalTime / (double)numberOfCycles;
    }
    
    /**
     * Checks if the evaluation series are correct. This is: all them must have the same length
     */
    public boolean checkData()
    {
        boolean ok = true;
        int l = -1;
        for(Enumeration iter = data.elements(); iter.hasMoreElements() && ok ;)
        {
            Vector v = (Vector)iter.nextElement();
            if(l == -1)
                l = v.size();
            else
                ok = (l == v.size());      
        }
        return ok;      
    }
    
    
    public String toString()
    {
    	StringBuffer s = new StringBuffer();
    	s.append("Series:\n");
    	String[] series = this.getSeriesLabels();
    	for(int i=0; i<series.length; i++)
    	{
    		s.append("  "+series[i]+": \n    ");
    		Vector v = this.getSeries(series[i]);
    		for(Object o: v){
    			Double d = (Double)o;
    			s.append(d+",");
    		}
    			
    		s.append("\n");
    	}
    	
    	s.append("\nOther data:\n");
    	String[] other = this.getOtherLabels();
    	for(int i=0; i<other.length; i++)
    	{
    		s.append("  "+other[i]+": "+ this.getOtherData(other[i])+"\n");
    	}
    	
    	s.append("\nNumber of Cycles: "+ this.getNumberOfCycles());
    	s.append("\nTime per Cycle:   "+ this.getTimePerCycle()+" ms");
    	s.append("\nTotal time:       "+ this.getTotalTime()+" ms");
    	
    	return s.toString();
    }
}
