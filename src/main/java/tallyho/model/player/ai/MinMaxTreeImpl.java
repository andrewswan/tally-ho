/*
 * Created on 19/07/2005
 */
package tallyho.model.player.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import tallyho.model.Board;
import tallyho.model.Team;
import tallyho.model.turn.Turn;

/**
 * An implementation of the MinMaxTree interface
 */
public class MinMaxTreeImpl implements MinMaxTree {

  // Properties
  private TreeModel turnTree;
  private final Team team;
  
  /**
   * Constructor
   * 
   * @param team the team whose move is being chosen
   * @param board the initial board position from which the tree of moves will
   *   be generated
   * @param depth the maximum depth of the tree, i.e. the number of moves to
   *   look ahead (beyond the next move)
   */
  public MinMaxTreeImpl(final Team team, final Board board, final int depth) {
    // Populate it to the given depth from the given board position
    generateTurnTree(board, depth);
    this.team = team;
  }

  /**
   * Generates the possible turns for each side, starting from the given board
   * position and looking ahead the given number of turns beyond the next one.
   * 
   * @param board can't be <code>null</code>
   * @param depth 0 means only consider the next turn, 1 means also consider
   *   the other player's next turn, and so on. Cannot be negative.
   */
  private void generateTurnTree(Board board, int depth) {
    if (board == null) {
      throw new IllegalArgumentException("BoardImpl can't be null");
    }
    if (depth < 0) {
      throw new IllegalArgumentException("Depth must be zero or greater");
    }
    /*
     * Create an empty turn tree with a dummy root node (it's not used by our
     * min-max algorithm, as its children are the current player's set of next
     * available moves
     */
    turnTree = new DefaultTreeModel(new DefaultMutableTreeNode(null));
    
    // Find all the moves from the given board position to the given depth
    
    // TODO Auto-generated method stub
  }
  
  /*
   * (non-Javadoc)
   * @see tallyho.model.player.ai.MinMaxTree#getBestTurn()
   */
  public Turn getBestTurn() {
    // Find the next TurnNodes with the best min-max values
    List<TurnNode> bestNextNodes = getBestNextNodes();
    // Pick one at random to prevent the AI being predictable
    Collections.shuffle(bestNextNodes);
    // Return the Turn with which it's associated
    return bestNextNodes.get(0).getTurn();
  }

  /**
   * Finds the node or nodes (leading from the root node of the turn tree) that
   * have the highest min-max values; i.e. the nodes representing the best next
   * turn(s).
   * 
   * @return a non-empty List
   */
  private List<TurnNode> getBestNextNodes() {
    TreeNode root = (TreeNode) turnTree.getRoot();
    int nextMoveCount = root.getChildCount();
    if (nextMoveCount == 0) {
      // This shouldn't happen as the caller should have checked first
      throw new IllegalStateException("AI has no next move");
    }
    // Go through the next nodes and find the ones with the highest min-max val
    List<TurnNode> bestNextNodes = new ArrayList<TurnNode>();
    // Start by assuming the first candidate is one of the best
    TurnNode firstRootChild = (TurnNode) root.getChildAt(0);
    bestNextNodes.add(firstRootChild);
    int highestMinMaxValue = firstRootChild.getMinMaxValue(team);
    // Go through the others looking for ones as good or better
    for (int i = 1; i < nextMoveCount; i++) {
      TurnNode rootChild = (TurnNode) root.getChildAt(i);
      int rootChildMinMaxValue = rootChild.getMinMaxValue(team);
      if (rootChildMinMaxValue == highestMinMaxValue) {
        // As good as the best found so far - add it to the list
        bestNextNodes.add(rootChild);
      }
      else if (rootChildMinMaxValue > highestMinMaxValue) {
        // Better than the best found so far
        bestNextNodes.clear();
        bestNextNodes.add(rootChild);
        highestMinMaxValue = rootChildMinMaxValue;
      }
      else {
        // It's worse than the best already found - ignore it
      }
    }
    return bestNextNodes;
  }
  
  /**
   * Encapsulates a node in the min-max tree. It has the following properties:
   * <ul>
   *   <li>The game turn with which it's associated<li>
   *   <li>Its parent node, if any</li>
   *   <li>Its child nodes, if any</li>
   *   <li>Its min-max value</li>
   * </ul>
   */
  static class TurnNode implements MutableTreeNode {

    // Properties
    private final MutableTreeNode baseNode;
    private final Turn turn;
    
    /**
     * Constructor
     * 
     * @param turn the turn with which this node is associated; can be
     *   <code>null</code> for the root node
     */
    public TurnNode(Turn turn) {
      this.turn = turn;
      baseNode = new DefaultMutableTreeNode();
    }

    /**
     * Returns the min-max value of this TurnNode
     * 
     * @param team the team whose move is being chosen
     * @return see above
     */
    public int getMinMaxValue(Team team) {
      // For any node, part of the min-max value is the turn's own score
      int minMaxValue = turn.getScore();
      if (!isLeaf()) {
        // Add the min or max of any child nodes (depending whose turn is next)
        if (team.equals(turn.getTeam())) {
          // Next turn is opponent's => add smallest child
          minMaxValue += getMinimumChildValue(team);
        }
        else {
          // Next turn is ours => add biggest child
          minMaxValue += getMaximumChildValue(team);
        }
      }
      return minMaxValue;
    }

    /**
     * Returns the smallest min/max value out of this node's children
     * 
     * @param team the team whose move is being chosen
     * @return see above
     */
    private int getMinimumChildValue(Team team) {
      // Start by assuming the first child is the lowest
      int minimumChild = ((TurnNode) getChildAt(0)).getMinMaxValue(team);
      // Check any others
      for (int i = 1; i < getChildCount(); i++) {
        TurnNode child = (TurnNode) getChildAt(i);
        int childValue = child.getMinMaxValue(team);
        if (childValue < minimumChild) {
          // Found a smaller one
          minimumChild = childValue;
        }
      }
      return minimumChild;
    }

    /**
     * Returns the biggest min/max value out of this node's children
     * 
     * @param team the team whose move is being chosen
     * @return see above
     */
    private int getMaximumChildValue(Team team) {
      // Start by assuming the first child is the highest
      int maximumChild = ((TurnNode) getChildAt(0)).getMinMaxValue(team);
      // Check any others
      for (int i = 1; i < getChildCount(); i++) {
        TurnNode child = (TurnNode) getChildAt(i);
        int childValue = child.getMinMaxValue(team);
        if (childValue > maximumChild) {
          // Found a bigger one
          maximumChild = childValue;
        }
      }
      return maximumChild;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#children()
     */
    public Enumeration children() {
      return baseNode.children();
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    public boolean getAllowsChildren() {
      return true;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     */
    public TreeNode getChildAt(int childIndex) {
      return baseNode.getChildAt(childIndex);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    public int getChildCount() {
      return baseNode.getChildCount();
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    public int getIndex(TreeNode node) {
      return baseNode.getIndex(node);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getParent()
     */
    public TreeNode getParent() {
      return baseNode.getParent();
    }
    
    /**
     * Returns the turn with which this node is associated
     * 
     * @return <code>null</code> for the root node
     */
    Turn getTurn() {
      return turn;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    public boolean isLeaf() {
      return baseNode.isLeaf();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.tree.MutableTreeNode#insert(javax.swing.tree.MutableTreeNode, int)
     */
    public void insert(MutableTreeNode child, int index) {
      baseNode.insert(child, index);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.MutableTreeNode#remove(int)
     */
    public void remove(int index) {
      baseNode.remove(index);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.MutableTreeNode#remove(javax.swing.tree.MutableTreeNode)
     */
    public void remove(MutableTreeNode node) {
      baseNode.remove(node);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.MutableTreeNode#removeFromParent()
     */
    public void removeFromParent() {
      baseNode.removeFromParent();
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.MutableTreeNode#setParent(javax.swing.tree.MutableTreeNode)
     */
    public void setParent(MutableTreeNode newParent) {
      baseNode.setParent(newParent);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.MutableTreeNode#setUserObject(java.lang.Object)
     */
    public void setUserObject(Object object) {
      throw new UnsupportedOperationException("User objects not supported");
    }
  }
}
