package tree;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mayezhou on 16/5/7.
 */
public class FormationTree{
    public TreeNode<String> root;
    public static final TreeNode<String> NIL = new TreeNode<String>(null, null, null,null);
    public static final String AND = "\\and";
    public static final String OR = "\\or";
    public static final String NOT = "\\not";
    public static final String IMPLY = "\\imply";
    public static final String EQUAL = "\\eq";
    public boolean wellDefined;

    public FormationTree(String proposition, boolean wellDefined) {
        TreeNode<String> root = new TreeNode<String>(proposition, NIL, NIL, NIL);
        this.root = root;
        this.wellDefined = wellDefined;
    }

    public TreeNode<String> build(TreeNode<String> node) {
        try {
            //init
            String proposition = node.element;
            String[] strings = proposition.split(" ");
            if (strings.length == 1) {//leaf  proposition letter
                if (!isPropositionLetter(strings[0])) {
                    throw new Exception("Not a proper proposition");
                }
                node.left = NIL;
                node.right = NIL;
                System.out.println(node.element);
                return node;
            }
            //assert false
            if (!strings[0].equals("(") || !strings[strings.length-1].equals(")")) {
                throw new Exception("Not a proper proposition");
            }
            ArrayList<String> symbols = new ArrayList<>();
            for (int i = 1; i < strings.length-1; i++) {
                symbols.add(strings[i]);
            }
            //analyze
            if (symbols.get(0).equals(NOT)) {
                String nextProposition = "";
                for (int i = 1; i < symbols.size(); i++) {
                    nextProposition += symbols.get(i) + " ";
                }
                TreeNode<String> child = new TreeNode<String>(nextProposition);
                child.parent = node;
                child = build(child);//complement its left&right
                node.left = child;
                node.right = NIL;
            } else if (symbols.size() > 1){//binary connectives
                int count = 0;
                int i;
                for (i = 0; i < symbols.size(); i++) {
                    if (symbols.get(i).equals("(")) {
                        count++;
                    } else if (symbols.get(i).equals(")")) {
                        count--;
                    } else if (count==0 && (symbols.get(i).equals(AND)||symbols.get(i).equals(OR)||symbols.get(i).equals(EQUAL)||symbols.get(i).equals(IMPLY))) {
                        break;
                    }
                }
                //left
                String nextPropositionL = "";
                for (int j = 0; j < i; j++) {
                    nextPropositionL += symbols.get(j) + " ";
                }
                TreeNode<String> leftChild = new TreeNode<String>(nextPropositionL);
                leftChild.parent = node;
                leftChild = build(leftChild);//complement its left&right
                node.left = leftChild;
                //right
                String nextPropositionR = "";
                for (int j = i+1; j < symbols.size(); j++) {
                    nextPropositionR += symbols.get(j) + " ";
                }
                TreeNode<String> rightChild = new TreeNode<String>(nextPropositionR);
                rightChild.parent = node;
                rightChild = build(rightChild);//complement its left&right
                node.right = rightChild;
            } else {
                throw new Exception("Not a proper proposition");
            }
            System.out.println(node.element);
            return node;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            wellDefined = false;
        }
        return null;
    }

    private boolean isPropositionLetter(String element) {
        Pattern pattern0 = Pattern.compile("[A-Z]");
        Pattern pattern1 = Pattern.compile("[A-Z]+(_\\{[0-9]+\\})?");
        Matcher matcher0 = pattern0.matcher(element);
        Matcher matcher1 = pattern1.matcher(element);
        if (matcher0.matches() || matcher1.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        String test = "A       B";
        String[] strings = test.split(" ");
        for (String s:
             strings) {
            System.out.println(s);
        }
    }
}
