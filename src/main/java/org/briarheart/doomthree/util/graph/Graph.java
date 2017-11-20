package org.briarheart.doomthree.util.graph;

import org.briarheart.doomthree.Identifiable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Roman Chigvintsev
 */
public class Graph<T extends Identifiable> {
    private final Map<Long, Node<T>> nodes = new HashMap<>();

    public void add(T element) {
        nodes.put(element.getId(), new Node<>(element));
    }

    public boolean addIfNotPresent(T element) {
        Node<T> node = nodes.get(element.getId());
        if (node == null) {
            add(element);
            return true;
        }
        return false;
    }

    public void link(T element1, T element2) {
        addIfNotPresent(element1);
        Node<T> node1 = nodes.get(element1.getId());

        addIfNotPresent(element2);
        Node<T> node2 = nodes.get(element2.getId());

        node1.adjacentNodes.add(node2);
        node2.adjacentNodes.add(node1);
    }

    public int size() {
        return nodes.size();
    }

    private static class Node<T> {
        private final T value;
        private final Set<Node<T>> adjacentNodes = new HashSet<>();

        public Node(T value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node<?> node = (Node<?>) o;

            return value.equals(node.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}
