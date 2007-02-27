/*
 * Created on 12-Nov-06
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package abc.tm.weaving.weaver.tmanalysis.query;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import soot.EntryPoints;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;
import abc.main.Main;
import abc.weaving.aspectinfo.AbcClass;
import abc.weaving.aspectinfo.GlobalAspectInfo;
import abc.weaving.aspectinfo.MethodCategory;

/**
 * Provides functionality to list all weavable methods, i.e. all methods (which have a body) in all weavable classes or
 * only those that are reachable over a given call graph from any application entry point.
 * 
 * @see EntryPoints#application() 
 * @author Eric Bodden
 */
public class WeavableMethods {
	
	/** set of all weavable methods in the program */
	protected Set weavableMethods;
	
	/** cache mapping each call graph to its set of reachable weavable methods */
	protected Map cgToReachable;
	
	/**
	 * Standard constructor, only executed once on initialization.
	 * Registers all weavable methods.
	 */
	private WeavableMethods() {

		cgToReachable = new IdentityHashMap();
		weavableMethods = new HashSet();
		
		GlobalAspectInfo gai = Main.v().getAbcExtension().getGlobalAspectInfo();
		
		//for each weavable class
		for (Iterator abcClassIter = gai.getWeavableClasses().iterator(); abcClassIter.hasNext();) {
			AbcClass abcClass = (AbcClass) abcClassIter.next();
			SootClass sc = abcClass.getSootClass();
			//for each method			
			for (Iterator methodIter = sc.methodIterator(); methodIter.hasNext();) {
				SootMethod sm = (SootMethod) methodIter.next();
				//if it has a body, register it
				if(sm.hasActiveBody() && MethodCategory.weaveInside(sm)) {
					weavableMethods.add(sm);
				}
			}
		}		
		
		weavableMethods = Collections.unmodifiableSet(weavableMethods);		
	}
	
	/**
	 * Returns the set of all weavable methods.
	 * @return all weavable methods; those are assured to have an active body
	 */
	public Set getAll() {
		return weavableMethods; 
	}

	/**
	 * Returns all weavable methods reachable from application entry points over edges in cg.
	 * The result is cached per call graph, i.e. the client has to make sure that the call graph passed in
	 * does not change over time. 
	 * @param cg any call graph
	 * @return the set of all reachable weavable methods
	 * @see EntryPoints#application()
	 */
	public Set getReachable(CallGraph cg) {
		//return cached value if present
		if(cgToReachable.containsKey(cg)) {
			return (Set) cgToReachable.get(cg);
		}
		
		//get all reachable methods
		ReachableMethods rm = new ReachableMethods(cg,EntryPoints.v().application());
		rm.update();
		
		QueueReader reader = rm.listener();
		Set reachableWeavableMethods = new HashSet();
		
		//check for weavable ones
        while(reader.hasNext()) {
            SootMethod method = (SootMethod) reader.next();
            
            if(weavableMethods.contains(method)) {
            	reachableWeavableMethods.add(method);
            }
        }
        
        //cache
        cgToReachable.put(cg,reachableWeavableMethods);
        
        return reachableWeavableMethods;
	}
	
	//singleton pattern
	
	protected static WeavableMethods instance;
	
	public static WeavableMethods v() {
		if(instance==null) {
			instance = new WeavableMethods();
		}
		return instance;		
	}

	/**
	 * Frees the singleton object. 
	 */
	public static void reset() {
		instance = null;
	}

}