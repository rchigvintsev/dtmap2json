package org.briarheart.doomthree.util.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Roman Chigvintsev
 */
public class Node<T> {
    private final T value;
    private final Set<Node<T>> adjacentNodes = new HashSet<>();

    public Node(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public Set<Node<T>> getAdjacentNodes() {
        return adjacentNodes;
    }
}
