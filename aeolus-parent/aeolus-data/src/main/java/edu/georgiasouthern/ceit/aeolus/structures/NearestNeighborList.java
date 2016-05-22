package edu.georgiasouthern.ceit.aeolus.structures;

import java.util.*; 

public class NearestNeighborList<T extends Point> {

    // the maximum number of elements that can be added to this structure
    int capacity;

    // reference point (the query) for all comparisons of elements
    T ref;

    // the underlying container for this class
    LinkedList<T> list;

    /**
     * Allocate an empty NearestNeighborList with capacity k and refernce point
     * ref.
     *  
     * @param k the capacity of this nnl
     * @param ref the reference point for comparisons of elements
     */
    public NearestNeighborList(int k, T ref) { 
        capacity = k;
        this.ref = ref;
        list = new LinkedList<T>();
    }

    /**
     * Add node to this NearestNeighborList.
     *
     * @param node the KDTreeNode to be added to this NNL
     */
    public void add(KDTreeNode<T> node) { 

        // this first if block seems to be the source of an error in that it 
        // allows for TOO MANY elements to be added to the NNL... thus we have longer
        // than expected NNLs, this leads to unexpected results... think about this 
        // carefully!!!
        if (size() > 0)
            if (node.euclideanDistance(ref) == getLast().euclideanDistance(ref))
                list.addLast(node.getElement());

        list.add(node.getElement());
        final T final_ref = ref;
        Collections.sort(list, new Comparator<T>() {
            public int compare(T p1, T p2) { 
                if (p1.euclideanDistance(final_ref) <
                        p2.euclideanDistance(final_ref))
                    return -1;
                else if (p1.euclideanDistance(final_ref) ==
                        p2.euclideanDistance(final_ref))
                    return 0;
                else
                    return 1;
            }
        });
        if (size() > capacity) removeLast();
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }

    public int size() {
        return list.size();
    }

    public T getLast() { 
        return list.getLast();
    }

    // probably should replace this by implementing Iterable, then
    // this method is unnecessary!
    public List<T> getList() {
        return this.list;
    }

    public void removeLast() {
        list.removeLast();
    }

    @Override
    public String toString() {
        String result = ""; 
        for (T p: list) {
            result += ("\t" + p.euclideanDistance(ref) + "\t" +
                p.toString() + "\n" );
        }
        return result; 
    }
}
