package tree;

/**
 * Created by mayezhou on 16/5/7.
 */
public class TreeNode<E> {
    public E element;
    public TreeNode<E> left;
    public TreeNode<E> right;
    public TreeNode<E> parent;

    public TreeNode(E element) {
        this.element = element;
    }

    public TreeNode(E element, TreeNode<E> left, TreeNode<E> right, TreeNode<E> parent) {
        this.element = element;
        this.left = left;
        this.right = right;
        this.parent = parent;
    }
}
