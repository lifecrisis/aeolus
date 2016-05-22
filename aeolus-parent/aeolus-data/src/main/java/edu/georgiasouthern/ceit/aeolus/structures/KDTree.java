package edu.georgiasouthern.ceit.aeolus.structures;

import java.io.Serializable;
import java.util.*;

public class KDTree<T extends Point> implements Serializable {

    private final int dimension;
    private int size;
    private KDTreeNode<T> root;

    public KDTree(int dimension) {
        this.dimension = dimension;
        size = 0;
        root = null;
    }

    public void addElement(T element) {
        if (element.getDimension() != dimension)
            throw new IllegalArgumentException();
        int axis = 0;
        if (isEmpty())
            root = new KDTreeNode<T>(element);
        else {
            if (element.get(axis) <= root.get(axis))
                if (root.getLeft() == null)
                    root.setLeft(new KDTreeNode<T>(element));
                else
                    addElement(element, root.getLeft(), 
                        (axis + 1) % dimension);
            else
                if (root.getRight() == null)
                    root.setRight(new KDTreeNode<T>(element));
                else
                    addElement(element, root.getRight(), 
                        (axis + 1) % dimension);
        }
        size++;
    }

    // helper method
    private void addElement(T element, KDTreeNode<T> node, int axis) {
        if (element.get(axis) <= node.get(axis))
            if (node.getLeft() == null)
                node.setLeft(new KDTreeNode<T>(element));
            else
                addElement(element, node.getLeft(), (axis + 1) % dimension);
        else
            if (node.getRight() == null)
                node.setRight(new KDTreeNode<T>(element));
            else
                addElement(element, node.getRight(), (axis + 1) % dimension);
    }

    public void build(List<T> elements) {
        root = null;
        size = 0;
        if (elements.size() > 0) 
            build(elements, 0);
    }

    // helper method
    private void build(List<T> elements, int axis) {
        if (elements.size() <= 2) {
            for (T e : elements)
                addElement(e);
            return;
        }
        final int final_axis = axis;
        elements.sort(new Comparator<T>() {
            public int compare(T e1, T e2) {
              if (e1.get(final_axis) < e2.get(final_axis))
                  return -1;
              else if (e1.get(final_axis) == e2.get(final_axis))
                  return 0;
              else
                  return 1;
            }
        });
        int mid = elements.size() / 2;
        addElement(elements.get(mid));
        build(elements.subList(0, mid), (axis + 1) % dimension);
        build(elements.subList(mid + 1, elements.size()),
            (axis + 1) % dimension);
    }

    public int getDimension() {
        return dimension;
    }

    public T getRootElement() {
        return root.getElement();
    }

    public boolean isEmpty() { 
        return size == 0;
    }

    public int size() { 
        return size;
    }

    public NearestNeighborList<T> getNearestNeighbors(int k, T value) {
        NearestNeighborList<T> neighborList = 
                new NearestNeighborList(k, value);
        searchNode(value, root, neighborList);
        return neighborList;
    }

    private void searchNode(T value, KDTreeNode<T> curr, 
            NearestNeighborList<T> neighborList) {

        if (curr == null)
            return;

        // make darn sure this works as expected!
        neighborList.add(curr);

        int axis = curr.getLevel() % dimension;
        boolean left = true;
        if (value.get(axis) <= curr.get(axis)) 
            searchNode(value, curr.getLeft(), neighborList);
        else {
            searchNode(value, curr.getRight(), neighborList);
            left = false;
        }

        if (Math.abs(curr.get(axis) - value.get(axis)) < 
                neighborList.getLast().euclideanDistance(value)) {
            if (left)
                searchNode(value, curr.getRight(), neighborList);
            else
                searchNode(value, curr.getLeft(), neighborList);
        }
    }
}
