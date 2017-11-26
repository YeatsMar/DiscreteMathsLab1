package tree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by mayezhou on 16/5/6.
 */
public class TreeControl extends JPanel {
    private JButton nextBtn = new JButton("Next");
    private JButton previousBtn = new JButton("Previous");
    private TreeView treeView = new TreeView();
    private FormationTree tree;
    private JLabel definition = new JLabel();
    private static ArrayList<Integer> bad = new ArrayList<>();
    private static ArrayList<String> proposition = read("input.txt");
    private int index = 0;
    private boolean wellDifined;

    public TreeControl() {
        wellDifined = check(proposition.get(index));
        if (bad.contains(index)) {
            wellDifined = false;
        }
        this.tree = new FormationTree(proposition.get(index), wellDifined);
        tree.build(tree.root);
        definition.setText(tree.wellDefined? "well-defined" : "Bad proposition");
        setUI();
    }

    private static boolean check(String s) {
        String[] symbols = s.split(" ");
        for (String symbol:
             symbols) {
            if (!(symbol.equals("(") || symbol.equals(")") ||
                    symbol.matches("[A-Z]+(_\\{[0-9]+\\})?") ||
                    symbol.equals(FormationTree.IMPLY) ||
                    symbol.equals(FormationTree.EQUAL) ||
                    symbol.equals(FormationTree.OR) ||
                    symbol.equals(FormationTree.AND) ||
                    symbol.equals(FormationTree.NOT)
            )) {
                return false;
            }
        }
        return true;
    }

    private void setUI() {
        setLayout(new BorderLayout());
        add(treeView, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.add(definition);
        panel.add(previousBtn);
        panel.add(nextBtn);
        add(panel, BorderLayout.SOUTH);

        previousBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index > 0) {
                    boolean wellDifined = check(proposition.get(--index));
                    tree = new FormationTree(proposition.get(index), wellDifined);
                    tree.build(tree.root);
                    definition.setText(tree.wellDefined? "well-defined" : "Bad proposition");
                    treeView.repaint();
                } else {
                    definition.setText("This is the first one.");
                }
            }
        });

        nextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index + 1 < proposition.size()) {
                    boolean wellDifined = check(proposition.get(++index));
                    tree = new FormationTree(proposition.get(index), wellDifined);
                    tree.build(tree.root);
                    definition.setText(tree.wellDefined? "well-defined" : "Bad proposition");
                    treeView.repaint();
                } else {
                    definition.setText("No more, all finished.");
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        TreeControl treeControl = new TreeControl();
        frame.add(treeControl);
        frame.setTitle("test");
        frame.setLocationRelativeTo(null);
        frame.setSize(1050, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(true);
    }

    private static ArrayList<String> read(String file) {
        ArrayList<String> propositons = new ArrayList<>();
        try {
            Scanner input = new Scanner(new File(file));
            for (int a = 0; input.hasNextLine(); a++) {
                try {
                    String line = input.nextLine();
                    char[] chars = line.toCharArray();
                    String tmp = "";
                    int flag = 0;
                    for (int i = 0; i < chars.length; i++) {
                        if (chars[i] >= 'A' && chars[i] <= 'Z') {//proposition letter
                            if (i+1 < chars.length) {
                                if (chars[i+1] != '_') {
                                    tmp += chars[i] + " ";
                                } else {
                                    flag = i;
                                }
                            } else if (i != 0) {
                                bad.add(a);
                                throw new Exception("bad proposition!");
                            } else {
                                tmp += chars[i];
                            }
                        } else if (chars[i] == '}') {
                            for (int j = flag; j <= i; j++) {
                                tmp += chars[j];
                            }
                            tmp += " ";
                        } else if (chars[i] == '\\') {//connectives
                            flag = i;
                            switch (chars[i+1]) {
                                case 'a':
                                    if (!line.substring(i, i+4).equals(FormationTree.AND)) {
                                        bad.add(a);
                                        throw new Exception("bad proposition!");
                                    }
                                    i += 3;
                                    break;
                                case 'o':
                                    if (!line.substring(i, i+3).equals(FormationTree.OR)) {
                                        bad.add(a);
                                        throw new Exception("bad proposition!");
                                    }
                                    i += 2;
                                    break;
                                case 'n':
                                    if (!line.substring(i, i+4).equals(FormationTree.NOT)) {
                                        bad.add(a);
                                        throw new Exception("bad proposition!");
                                    }
                                    i += 3;
                                    break;
                                case 'i':
                                    if (!line.substring(i, i+6).equals(FormationTree.IMPLY)) {
                                        bad.add(a);
                                        throw new Exception("bad proposition!");
                                    }
                                    i += 5;
                                    break;
                                case 'e':
                                    if (!line.substring(i, i+3).equals(FormationTree.EQUAL)) {
                                        bad.add(a);
                                        throw new Exception("bad proposition!");
                                    }
                                    i += 2;
                                    break;
                            }
                            for (int j = flag; j <= i; j++) {
                                tmp += chars[j];
                            }
                            tmp += " ";
                        } else if (chars[i] == ' ') {
//                        i++;
                        } else if (chars[i] == '(' || chars[i] == ')') {
                            tmp += chars[i] + " ";
                        }
                    }
                    propositons.add(tmp);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return propositons;
    }

    /**
     * Created by mayezhou on 16/5/9.
     */
    class TreeView extends JPanel {
        private int radius = 20;
        private int vGap = 50;//gap between 2 levels in a tree

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (tree.wellDefined) {
                displayTree(g, tree.root, getWidth() / 2, 30, getWidth() / 4);
            }
        }

        private void displayTree(Graphics g, TreeNode<String> root, int x, int y, int hGap) {
            g.drawString(root.element, x - getLength(root.element), y);
            if (root.left != FormationTree.NIL) {
                connectLeftChild(g, x - hGap, y + vGap, x, y);
                displayTree(g, root.left, x - hGap, y + vGap, hGap / 2);
            }
            if (root.right != FormationTree.NIL) {
                connectRightChild(g, x + hGap, y + vGap, x, y);
                displayTree(g, root.right, x + hGap, y + vGap, hGap / 2);
            }
        }

        private void connectRightChild(Graphics g, int x1, int y1, int x2, int y2) {
            double d = Math.sqrt(vGap * vGap + (x2 - x1) * (x2 - x1));
            int x11 = (int) (x1 + radius * (x2 - x1) / d);
            int y11 = (int) (y1 - radius * vGap / d);
            int x21 = (int) (x2 - radius * (x2 - x1) / d);
            int y21 = (int) (y2 + radius * vGap / d);
            g.drawLine(x11, y11, x21, y21);
        }

        private void connectLeftChild(Graphics g, int x1, int y1, int x2, int y2) {
            double d = Math.sqrt(vGap * vGap + (x2 - x1) * (x2 - x1));
            int x11 = (int) (x1 - radius * (x1 - x2) / d);
            int y11 = (int) (y1 - radius * vGap / d);
            int x21 = (int) (x2 + radius * (x1 - x2) / d);
            int y21 = (int) (y2 + radius * vGap / d);
            g.drawLine(x11, y11, x21, y21);
        }

        private int getLength(String element) {
            return (int) (element.length() * 3);
        }
    }
}
