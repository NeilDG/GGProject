/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.minimax;

import java.util.ArrayList;

/**
 * Represents a generic tree node for the game tree
 * @author user
 *
 */
public class TreeNode {

	private final static String TAG = "TreeNode";
	
	protected TreeNode parentNode;		
	protected ArrayList<TreeNode> children;
	
	protected boolean discovered = false;
	protected float heuristicScore;
	
	public TreeNode() {
		this.parentNode = null;
		this.children = new ArrayList<TreeNode>();
		this.heuristicScore = 0.0f;
	}
	public float getHeuristic() {
		return this.heuristicScore;
	}
	
	public void setHeuristicScore(float value) {
		this.heuristicScore = value;
	}
	
	public void markAsDiscovered() {
		this.discovered = true;
	}
	
	public void resetDiscovery() {
		this.discovered = false;
	}
	
	public boolean isDiscovered() {
		return this.discovered;
	}
	
	public boolean isLeaf() {
		if(this.getChildCount() == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public int getDepth() {
		int depth = 0;
		if(this.isRoot()) {
			return depth;
		}
		else {
			TreeNode tmpNode = this;
			
			while(tmpNode != null) {
				tmpNode = tmpNode.getParent();
				depth++;
			}
			return depth - 1;
		}
	}
	
	public void setParent(TreeNode treeNode) {
		this.parentNode = treeNode;
	}
	
	public void addChild(TreeNode treeNode) {
		this.children.add(treeNode);
		treeNode.setParent(this);
	}
	
	public boolean hasChildren() {
		if(this.children.size() != 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public int getChildCount() {
		return this.children.size();
	}
	
	public TreeNode getChild(int index) {
		return this.children.get(index);
	}
	
	public TreeNode getParent() {
		return this.parentNode;
	}
	
	public boolean isRoot() {
		if(this.parentNode == null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
}
