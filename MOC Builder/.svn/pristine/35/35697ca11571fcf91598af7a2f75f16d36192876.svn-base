package LDraw.Support;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 *  LDrawFastSet.h
 *  Bricksmith
 *
 *  Created by bsupnik on 9/15/12.
 *  Copyright 2012 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * @Class LDrawFastSet
 * 
 * @Represent LDrawFastSet.h of Bricksmith
 * 
 * @author funface2
 * @since 2014-03-13
 * 
 */

// LDrawFastSet - THEORY OF OPERATION
//
// Here's the problem: the overhead of NS collections is relatively high, and
// attaching a
// mutable set to each directive to keep track of who is observing it basically
// doubles
// the number of NS containers and load time.
//
// Buuuuut - in nearly every important case, the nubmer of objects in a
// container is 1.
// So the fast set structure optimizes away the NS mutable set for the simple
// cases where
// we don't need a real set.
//
// ENCODING
//
// The fast set is a pair of pointers with the following encoding rules:
//
// p1 p2 meaning
// NULL NULL The set is empty.
// id1 NULL The set contains one object, refered to via id1.
// id1 id2 The set contains two objects, referred to via id1 and id2.
// NULL id2 The set contains at least 3 objects, contained in an NSMutableSet
// stored in id2.


public class LDrawFastSet<T> {
	// 2개 이하일땐 p1, p2에 보관. 그 이상일땐 Set에 보관. 이유? 대부분의 경우는 2개 이하라서 속도 올리기 위해.
	/**
	 * @uml.property  name="isContainsMoreThan2Items"
	 */
	boolean isContainsMoreThan2Items = false;
	/**
	 * @uml.property  name="p1"
	 */
	T p1;
	/**
	 * @uml.property  name="p2"
	 */
	T p2;

	/**
	 * @uml.property  name="tree_set"
	 */
	TreeSet<T> tree_set;
	private Lock mutex;
	public LDrawFastSet() {
		p1 = p2 = null;
		mutex = new ReentrantLock(true);
	}

	public boolean isContains(T item) {
		if (isContainsMoreThan2Items == false) {
			if (p1 == item)
				return true;
			if (p2 == item)
				return true;
			return false;
		} else
			return tree_set.contains(item);
	}

	public void remove(T item) {
		
		if (isContainsMoreThan2Items == true) {
			mutex.lock();
			tree_set.remove(item);
			mutex.unlock();
			if (tree_set.size() == 0)
				isContainsMoreThan2Items = false;
		} else {
			if (p1 == item) {
				p1 = p2;
				p2 = null;
			} else if (p2 == item) {
				p2 = null;
			}
		}		
	}

	public void add(T item) {
		assert item!=null;
		
		if (isContains(item) == true)
			return;
		if (p1 == null)
			p1 = item;
		else if (p2 == null)
			p2 = item;
		else {
			isContainsMoreThan2Items = true;
			if (tree_set == null)
				tree_set = new TreeSet<T>();
			mutex.lock();
			tree_set.add(item);
			mutex.unlock();
		}
	}
	
	public void removeAll(){
		isContainsMoreThan2Items=false;
		mutex.lock();
		tree_set.clear();
		mutex.unlock();
		p1=p2=null;
	}
	
	public Set<T> getAllItems(){
		TreeSet<T> treeSet = new TreeSet<T>();
		if(p1!=null)
			treeSet.add(p1);
		if(p2!=null)
			treeSet.add(p2);
		if(isContainsMoreThan2Items==true){
			mutex.lock();
			treeSet.addAll(tree_set);
			mutex.unlock();
		}
		
		
		return treeSet;
	}
}
