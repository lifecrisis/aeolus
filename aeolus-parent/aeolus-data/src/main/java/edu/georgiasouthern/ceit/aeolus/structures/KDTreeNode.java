package edu.georgiasouthern.ceit.aeolus.structures;

import java.io.Serializable;

public class KDTreeNode<T extends Point> implements Serializable{

    private T element; 
    private int level;
    private KDTreeNode<T> parent; 
    private KDTreeNode<T> left; 
    private KDTreeNode<T> right; 

    public KDTreeNode(T element) {
        this.element = element;
        parent = null; 
        left = null; 
        right = null; 
        level = 0;
    }

    public double euclideanDistance(T p) { 
        return element.euclideanDistance(p);
    }

    public double get(int index) { 
        return element.get(index);
    }

    public int getDimension() {
        return element.getDimension();
    }

    public T getElement() { 
        return element;
    }

    public int getLevel() {
        return level;         
    }

    public KDTreeNode<T> getLeft() {
        return left;
    }

    public KDTreeNode<T> getRight() { 
        return right;
    }

    public KDTreeNode<T> getParent() { 
        return parent;
    }

    public void setLeft(KDTreeNode<T> left) {
        if (left.getDimension() != getDimension()) 
            throw new IllegalArgumentException();
        left.parent = this;
        left.level = this.level + 1;
        this.left = left;
    }

    public void setRight(KDTreeNode<T> right) { 
        if (right.getDimension() != getDimension()) 
            throw new IllegalArgumentException();
        right.parent = this; 
        right.level = this.level + 1;
        this.right = right;
    }

    @Override
    public String toString() {
        return element.toString();
    }
}
