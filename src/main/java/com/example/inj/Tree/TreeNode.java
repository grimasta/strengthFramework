/*
 Copyright 2010 Visin Suresh Paliath
 Distributed under the BSD license

*/

package com.example.inj.Tree;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    private int data;
    private List<TreeNode> children;
    private TreeNode parent;
    private int num;
    public TreeNode() {
        super();
        children = new ArrayList<>();
        data=-1;
        num=0;
    }

    public void incrementNumByOne(){
        this.num++;
    }
    public int getNum(){
        return this.num;
    }
    public TreeNode(int data) {
        this();
        setData(data);
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public List<TreeNode> getChildren() {
        return this.children;
    }

    public int getNumberOfChildren() {
        return getChildren().size();
    }

    public boolean hasChildren() {
        return (getNumberOfChildren() > 0);
    }

    public void setChildren(List<TreeNode> children) {
        for(TreeNode child : children) {
            child.parent = this;
        }

        this.children = children;
    }

    public void addChild(TreeNode child) {
        child.parent = this;
        children.add(child);
    }

    public void addChildAt(int index, TreeNode child) throws IndexOutOfBoundsException {
        child.parent = this;
        children.add(index, child);
    }

    public void removeChildren() {
        this.children = new ArrayList<TreeNode>();
    }

    public void removeChildAt(int index) throws IndexOutOfBoundsException {
        children.remove(index);
    }

    public TreeNode getChildAt(int index) throws IndexOutOfBoundsException {
        return children.get(index);
    }

    public int getData() {
        return this.data;
    }

    public void setData(int data) {
        this.data = data;
    }

    /*public String toString() {
        return getData().toString();
    }*/

    /*@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GenericTreeNode other = (GenericTreeNode) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        return true;
    }*/

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    /*@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        return result;
    }*/

    /*public String toStringVerbose() {
        String stringRepresentation = getData().toString() + ":[";

        for (GenericTreeNode node : getChildren()) {
            stringRepresentation += node.getData().toString() + ", ";
        }

        //Pattern.DOTALL causes ^ and $ to match. Otherwise it won't. It's retarded.
        Pattern pattern = Pattern.compile(", $", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(stringRepresentation);

        stringRepresentation = matcher.replaceFirst("");
        stringRepresentation += "]";

        return stringRepresentation;
    }*/
}
