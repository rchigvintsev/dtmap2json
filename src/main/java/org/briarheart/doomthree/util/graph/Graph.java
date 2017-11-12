package org.briarheart.doomthree.util.graph;

import org.briarheart.doomthree.Identifiable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Roman Chigvintsev
 */
public class Graph<T extends Identifiable> implements Iterable<T> {
    private final long id;
    private final Map<Long, Node<T>> nodes = new HashMap<>();

    public Graph(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void add(T element) {
        nodes.put(element.getId(), new Node<>(element));
    }

    public void linkNodes(T e1, T e2) {
        Node<T> node1 = addIfNotPresent(e1);
        Node<T> node2 = addIfNotPresent(e2);
        node1.getAdjacentNodes().add(node2);
        node2.getAdjacentNodes().add(node1);
    }

    public int size() {
        return nodes.size();
    }

    private Node<T> addIfNotPresent(T element) {
        Node<T> node = nodes.get(element.getId());
        if (node == null) {
            add(element);
            node = nodes.get(element.getId());
        }
        return node;
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<Long> keyIter = nodes.keySet().iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return keyIter.hasNext();
            }

            @Override
            public T next() {
                Long key = keyIter.next();
                Node<T> node = nodes.get(key);
                return node.getValue();
            }
        };
    }
}
