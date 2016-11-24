package be.uantwerpen.cobra.blockgen.tools.export.uppaal;

import be.uantwerpen.cobra.blockgen.models.blocks.*;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.models.Link;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.models.Node;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.models.NodeLinker;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Thomas on 25/04/2016.
 */
public class TimedAutomaton
{
    private String name;
    private Vector<Node> nodes;
    private Vector<Link> links;
    private Node endNode;

    public TimedAutomaton()
    {
        this.name = "AUTOMATON";
        this.nodes = new Vector<Node>();
        this.links = new Vector<Link>();
        this.endNode = new NodeLinker();
        this.endNode.setName("END");
        this.endNode.setComments("");
    }

    public int createAutomaton(Block model, int abstractionDepth) throws Exception
    {
        Vector<Node> endNodes = new Vector<Node>();

        if(model.getClass() != MethodBlock.class)
        {
            throw new Exception("Top block needs to be of class type MethodBlock!");
        }

        //Set automaton name
        this.name = ((MethodBlock)model).getMethodName();

        //Set initial node
        Node node = new NodeLinker();
        node.setId(getNextFreeId());
        node.setName(((MethodBlock) model).getMethodName());
        node.setInitial(true);

        if(abstractionDepth != 0)
        {
            node.setComments(model.getCodeSegment().toString());
        }
        else
        {
            node.setComments(model.getCodeString());
        }

        this.nodes.add(node);

        if(abstractionDepth != 0)
        {
            Vector<Node> parentNodes = new Vector<Node>();

            parentNodes.add(node);

            endNodes = addNodesRecursive(model, parentNodes, abstractionDepth - 1);
        }
        else
        {
            endNodes.add(node);
        }

        //Set id of end node
        this.endNode.setId(getNextFreeId());

        //Link to end if no return node is available
        if(!endNodes.isEmpty())
        {
            //Create links
            createLinks(endNodes, this.endNode);
        }

        this.prettifyLayout();

        return 0;
    }

    private Vector<Node> addNodesRecursive(Block currentBlock, Vector<Node> parentNodes, int leftAbstractionLevel)
    {
        int startRow = 0;
        int endRow = 0;
        int nextAbstractionLevel = leftAbstractionLevel - 1;
        Vector<Node> lastNodes = new Vector<Node>(parentNodes);

        for(Block block : currentBlock.getChildBlocks())
        {
            if(block instanceof IterationBlock)
            {
                lastNodes = createIterationNode((IterationBlock)block, lastNodes, leftAbstractionLevel);
            }
            else if(block instanceof SelectionBlock)
            {
                lastNodes = createSelectionNode((SelectionBlock)block, lastNodes, leftAbstractionLevel);
            }
            else if(block instanceof JumpBlock)
            {
                lastNodes = createJumpNode((JumpBlock)block, lastNodes, leftAbstractionLevel);
            }
            else
            {
                Node node = createBasicNode(block, leftAbstractionLevel);

                this.nodes.add(node);

                //Create links with last nodes
                lastNodes = createLinks(lastNodes, node);

                //Set new last node
                lastNodes.add(node);

                if(block.getNumOfChildren() > 0 && leftAbstractionLevel != 0)
                {
                    lastNodes = addNodesRecursive(block, lastNodes, nextAbstractionLevel);
                }
            }
        }

        return lastNodes;
    }

    private Link getEmptyTargetLink(Node node)
    {
        Link link = null;
        boolean found = false;
        Iterator<Link> it = node.getLinks().iterator();

        while(it.hasNext() && !found)
        {
            link = it.next();

            if(link.getTargetNode() == null)
            {
                found = true;
            }
        }

        if(found)
        {
            return link;
        }
        else
        {
            link = new Link();
            node.addLink(link);

            return link;
        }
    }

    private Node createBasicNode(Block block, int leftAbstractionLevel)
    {
        Node node;
        int startRow;
        int endRow;

        //Create node
        node = new NodeLinker();
        node.setId(getNextFreeId());

        startRow = block.getStartRowNumber();

        if(leftAbstractionLevel != 0)
        {
            endRow = block.getEndRowNumber();
            node.setComments(block.getCodeSegment().toString());
        }
        else
        {
            endRow = block.getLeafs().lastElement().getEndRowNumber();
            node.setComments(block.getCodeString());
        }

        if(startRow == endRow)
        {
            node.setName("r" + startRow);
        }
        else
        {
            node.setName("r" + startRow + "_r" + endRow);
        }

        return node;
    }

    private Vector<Node> createIterationNode(IterationBlock iterationBlock, Vector<Node> parentNodes, int leftAbstractionLevel)
    {
        int nextAbstractionLevel = leftAbstractionLevel - 1;
        Vector<Node> lastNodes = new Vector<Node>(parentNodes);

        if(leftAbstractionLevel == 0)
        {
            Node node = createBasicNode(iterationBlock, leftAbstractionLevel);

            this.nodes.add(node);

            //Create link
            lastNodes = createLinks(lastNodes, node);

            //Set new last node
            lastNodes.add(node);
        }
        else
        {
            if(iterationBlock.getCodeSegment().toString().startsWith("for"))
            {
                String initString = iterationBlock.getCodeSegment().toString();
                initString = initString.split(";")[0];

                try
                {
                    initString = initString.split("\\(")[1];
                }
                catch(ArrayIndexOutOfBoundsException e)
                {
                    //No space placed between ( and ;
                    initString = new String();
                }

                //Create initialisation node
                if(!initString.isEmpty())
                {
                    Node node = new NodeLinker();
                    node.setId(getNextFreeId());
                    node.setName("r" + iterationBlock.getStartRowNumber() + "_init");
                    node.setComments(initString + ";");

                    this.nodes.add(node);

                    //Create links with last nodes
                    lastNodes = createLinks(lastNodes, node);

                    //Set new last node
                    lastNodes.add(node);
                }
            }

            //Create iteration condition node
            Node conditionNode = new NodeLinker();
            String conditionString = iterationBlock.getCodeSegment().toString();

            if(!iterationBlock.isDoWhileStatement())
            {
                if(iterationBlock.getCodeSegment().toString().startsWith("for"))
                {
                    conditionNode.setName("r" + iterationBlock.getStartRowNumber() + "_cond");
                    conditionString = conditionString.split(";")[1];
                    conditionString = "for( ; " + conditionString + " ; )";
                }
                else
                {
                    conditionNode.setName("r" + iterationBlock.getStartRowNumber());
                }

                conditionNode.setComments(conditionString);

                //Create true statement links
                Link trueStatementLink = new Link();
                trueStatementLink.setGuard("true");

                conditionNode.addLink(trueStatementLink);
            }
            else
            {
                conditionNode.setName("r" + iterationBlock.getStartRowNumber() + "_do");
                conditionNode.setComments("do");
            }

            conditionNode.setId(getNextFreeId());

            this.nodes.add(conditionNode);

            //Create link with last nodes
            lastNodes = createLinks(lastNodes, conditionNode);

            //Set new last node
            lastNodes.add(conditionNode);

            Vector<Node> iterationInNodes = new Vector<Node>(lastNodes);

            //Add iteration body
            lastNodes = addNodesRecursive(iterationBlock, lastNodes, nextAbstractionLevel);

            Vector<Node> postIterationNodes = new Vector<Node>();

            if(iterationBlock.getCodeSegment().toString().startsWith("for"))
            {
                String postString = iterationBlock.getCodeSegment().toString();
                postString = postString.split(";")[2];
                postString = postString.substring(0, postString.lastIndexOf(")"));

                //Create post statement node
                if(!postString.isEmpty())
                {
                    Node node = new NodeLinker();
                    node.setId(getNextFreeId());
                    node.setName("r" + iterationBlock.getStartRowNumber() + "_post");
                    node.setComments(postString + ";");

                    this.nodes.add(node);

                    //Create link with last nodes
                    lastNodes = createLinks(lastNodes, node);

                    //Add post iteration nodes to vector
                    postIterationNodes.add(node);
                }
            }
            else if(iterationBlock.isDoWhileStatement())
            {
                //Create iteration condition node at the end
                Node conditionDoWhileNode = new NodeLinker();

                conditionDoWhileNode.setName("r" + iterationBlock.getStartRowNumber());
                conditionDoWhileNode.setId(getNextFreeId());
                conditionDoWhileNode.setComments(conditionString);

                this.nodes.add(conditionDoWhileNode);

                //Create link with last nodes
                lastNodes = createLinks(lastNodes, conditionDoWhileNode);

                //Add post iteration nodes to vector
                postIterationNodes.add(conditionDoWhileNode);
            }
            else
            {
                postIterationNodes.addAll(lastNodes);
            }

            //Create iteration loop links
            for(Node postNode : postIterationNodes)
            {
                if(((NodeLinker)postNode).tryLinking())
                {
                    //Get iteration start nodes
                    for(Node iterationInNode : iterationInNodes)
                    {
                        if(!((NodeLinker)iterationInNode).isLocked())
                        {
                            Link link = getEmptyTargetLink(postNode);

                            //Set link target
                            link.setTargetNode(iterationInNode);

                            //Add iteration loop link to last nodes of loop
                            postNode.addLink(link);

                            if(iterationBlock.isDoWhileStatement())
                            {
                                //Set link guard (True statement)
                                link.setGuard("true");
                            }

                            this.links.add(link);
                        }
                    }
                }
            }

            //Create iteration exit links
            //Get guard condition false
            if(!iterationBlock.isDoWhileStatement())
            {
                for(Node iterationInNode : iterationInNodes)
                {
                    if(!((NodeLinker)iterationInNode).isLocked())
                    {
                        Link link = getEmptyTargetLink(iterationInNode);

                        //Set link guard (False statement)
                        link.setGuard("false");

                        //Add node to last node list
                        Iterator<Node> it = lastNodes.iterator();

                        while(it.hasNext())
                        {
                            Node lastNode = it.next();

                            if(!((NodeLinker)lastNode).isLocked())
                            {
                                it.remove();
                            }
                        }

                        lastNodes.add(iterationInNode);
                    }
                }
            }
            else
            {
                lastNodes.addAll(postIterationNodes);

                for(Node postNodes : postIterationNodes)
                {
                    if(!((NodeLinker)postNodes).isLocked())
                    {
                        Link link = getEmptyTargetLink(postNodes);

                        //Set link guard (False statement)
                        link.setGuard("false");
                    }
                }
            }
        }

        return lastNodes;
    }

    private Vector<Node> createSelectionNode(SelectionBlock selectionBlock, Vector<Node> parentNodes, int leftAbstractionLevel)
    {
        int nextAbstractionLevel = leftAbstractionLevel - 1;
        Vector<Node> lastNodes = new Vector<Node>(parentNodes);

        if(leftAbstractionLevel == 0)
        {
            Node node = createBasicNode(selectionBlock, leftAbstractionLevel);

            this.nodes.add(node);

            //Create link
            lastNodes = createLinks(lastNodes, node);

            //Set new last nodes
            lastNodes.add(node);
        }
        else
        {
            //Create selection condition node
            Node selectionNode = new NodeLinker();
            String selectionString = selectionBlock.getCodeSegment().toString();

            selectionNode.setName("r" + selectionBlock.getStartRowNumber());
            selectionNode.setId(getNextFreeId());
            selectionNode.setComments(selectionString);

            this.nodes.add(selectionNode);

            //Create link with parent nodes
            lastNodes = createLinks(lastNodes, selectionNode);

            if(selectionBlock.getCodeSegment().toString().startsWith("if"))
            {
                //If-Else statement
                boolean trueStatement = false;
                boolean falseStatement = false;

                for(Block caseBlock : selectionBlock.getChildBlocks())
                {
                    String guardString;

                    if(caseBlock.getNumOfChildren() != 0)
                    {
                        if(((CaseBlock)caseBlock).getIfSelectionValue())
                        {
                            //True case
                            trueStatement = true;
                            guardString = "true";
                        }
                        else
                        {
                            //False case
                            falseStatement = true;
                            guardString = "false";
                        }

                        //Create link with selection statement node
                        Link link = getEmptyTargetLink(selectionNode);

                        //Set link guard
                        link.setGuard(guardString);

                        //Add selection branch body
                        lastNodes.add(selectionNode);

                        Vector<Node> newNodes = new Vector<Node>();
                        newNodes = addNodesRecursive(caseBlock, lastNodes, nextAbstractionLevel);

                        //Add link distance to blocks inside a selection branch if needed
                        int numOfLinkSkips = 0;

                        //Add link skips when switching to the false case branch
                        for(int i = caseBlock.getParentBlock().getChildBlocks().indexOf(caseBlock) + 1; i < caseBlock.getParentBlock().getNumOfChildren(); i++)
                        {
                            numOfLinkSkips += caseBlock.getParentBlock().getChildBlock(i).getNumOfDescendants();

                            //Add one link skip for each iteration loop
                            for(Block block : caseBlock.getParentBlock().getChildBlock(i).getChildBlocks())
                            {
                                if(block.getClass() == IterationBlock.class)
                                {
                                    numOfLinkSkips++;
                                }
                            }
                        }

                        if(numOfLinkSkips > 0)
                        {
                            for(Node node : newNodes)
                            {
                                //Check if node is a new added node
                                if(!lastNodes.contains(node))
                                {
                                    int linkDistance = ((NodeLinker)node).getLinkDistance();

                                    if(linkDistance <= 1)
                                    {
                                        //Add link distance according to following nodes in next case branches
                                        ((NodeLinker)node).setLinkDistance(linkDistance + numOfLinkSkips);
                                    }
                                }
                            }
                        }

                        lastNodes = newNodes;
                    }
                }

                if(!trueStatement && falseStatement)                    //Close true case if not existing
                {
                    //Create link with selection statement node
                    Link link = getEmptyTargetLink(selectionNode);

                    //Set link guard
                    link.setGuard("true");

                    ((NodeLinker)selectionNode).setLinkDistance(1);

                    lastNodes.add(selectionNode);
                }
                else if(!falseStatement && trueStatement)               //Close false case if not existing
                {
                    //Create link with selection statement node
                    Link link = getEmptyTargetLink(selectionNode);

                    //Set link guard
                    link.setGuard("false");

                    ((NodeLinker)selectionNode).setLinkDistance(1);

                    lastNodes.add(selectionNode);
                }
                else if(!(trueStatement && falseStatement))            //None completed if-else statement or non-functional selection statement if();
                {
                    ((NodeLinker)selectionNode).setLinkDistance(1);

                    lastNodes.add(selectionNode);
                }
            }
            else if(selectionBlock.getCodeSegment().toString().startsWith("switch"))
            {
                Vector<Node> previousCaseNodes = new Vector<Node>(Collections.singleton(selectionNode));
                boolean firstCase = true;

                //Switch-case statement
                for(Block caseBlock : selectionBlock.getChildBlocks())
                {
                    Node caseNode = new NodeLinker();
                    String caseString = caseBlock.getCodeSegment().toString();

                    caseNode.setName("r" + caseBlock.getStartRowNumber());
                    caseNode.setId(getNextFreeId());
                    caseNode.setComments(caseString);

                    this.nodes.add(caseNode);

                    //Create link with previous case nodes
                    //TODO: Check if this section is correct
                    for(Node previousCaseNode : previousCaseNodes)
                    {
                        Link link = getEmptyTargetLink(previousCaseNode);

                        //Set link target
                        link.setTargetNode(caseNode);

                        if(!firstCase)
                        {
                            //Set guard (False)
                            link.setGuard("false");
                        }
                        else
                        {
                            firstCase = false;
                        }

                        this.links.add(link);
                    }

                    //Create link for case body
                    Link caseBodyLink = getEmptyTargetLink(caseNode);

                    if(!caseString.startsWith("default:"))
                    {
                        //Set guard (True)
                        caseBodyLink.setGuard("true");
                    }

                    lastNodes.addAll(addNodesRecursive(caseBlock, new Vector<Node>(Collections.singleton(caseNode)), nextAbstractionLevel));

                    previousCaseNodes.clear();
                    previousCaseNodes.add(caseNode);
                }

                //Check for empty switch (Pass through)
                if(selectionBlock.getChildBlocks().isEmpty())
                {
                    lastNodes.add(selectionNode);
                }

                //TODO: Build check for cases without break statement!!
            }
        }

        return lastNodes;
    }

    private Vector<Node> createJumpNode(JumpBlock jumpBlock, Vector<Node> parentNodes, int leftAbstractionLevel)
    {
        int nextAbstractionLevel = leftAbstractionLevel - 1;
        Vector<Node> lastNodes = new Vector<Node>(parentNodes);

        //Create jump node
        Node jumpNode = new NodeLinker();
        String jumpString = jumpBlock.getCodeSegment().toString();

        jumpNode.setName("r" + jumpBlock.getStartRowNumber());
        jumpNode.setId(getNextFreeId());
        jumpNode.setComments(jumpString);

        this.nodes.add(jumpNode);

        //Create link with parent nodes
        lastNodes = createLinks(lastNodes, jumpNode);

        if(jumpNode.getComments().contains("return"))
        {
            //Return statement
            //Create link with exit node
            Link link = getEmptyTargetLink(jumpNode);

            //Set link target
            link.setTargetNode(this.endNode);

            this.links.add(link);
        }
        else if(jumpNode.getComments().contains("break"))
        {
            //Break statement
            //Check for iteration/case parent block
            boolean foundMatchingParent = false;
            Block checkParent = jumpBlock.getParentBlock();

            while(!foundMatchingParent && checkParent != null)
            {
                if(checkParent.getClass() == IterationBlock.class || checkParent.getClass() == CaseBlock.class)
                {
                    if(checkParent.getClass() == CaseBlock.class)
                    {
                        //Check if selection case is not a boolean case
                        if(checkParent.getCodeSegment() != null)
                        {
                            foundMatchingParent = true;
                        }
                    }
                    else
                    {
                        foundMatchingParent = true;
                    }
                }

                if(!foundMatchingParent)
                {
                    checkParent = checkParent.getParentBlock();
                }
            }

            int linkDistance = 1;

            if(foundMatchingParent)
            {
                linkDistance = checkParent.getNumOfDescendants() - (checkParent.getDescendantBlocks().indexOf(jumpBlock) + 1) + 2;

                ((NodeLinker)jumpNode).setLinkDistance(linkDistance);
            }
            else
            {
                //Unused break statement detected
            }

            //Add node to last nodes list to connect loop exit
            lastNodes.add(jumpNode);
        }
        else if(jumpNode.getComments().contains("continue"))
        {
            //Continue statement
            //Check for iteration parent block
            boolean foundMatchingParent = false;
            Block checkParent = jumpBlock.getParentBlock();

            while(!foundMatchingParent && checkParent != null)
            {
                if(checkParent.getClass() == IterationBlock.class)
                {
                    foundMatchingParent = true;
                }
                else
                {
                    checkParent = checkParent.getParentBlock();
                }
            }

            int linkDistance = 1;

            if(foundMatchingParent)
            {
                linkDistance = checkParent.getNumOfDescendants() - (checkParent.getDescendantBlocks().indexOf(jumpBlock) + 1) + 1;

                ((NodeLinker)jumpNode).setLinkDistance(linkDistance);
            }
            else
            {
                System.out.println("Unused continue statement detected!");
            }

            //Add node to last nodes list to connect to next iteration evaluation
            lastNodes.add(jumpNode);
        }
        else if(jumpNode.getComments().contains("goto"))
        {
            //Go to statement
            System.err.println("Goto statement block is not yet implemented!");
            System.err.println("Results will not be correct!");
        }

        return lastNodes;
    }

    private int getNextFreeId()
    {
        if(this.nodes.isEmpty())
        {
            return 0;
        }
        else
        {
            return this.nodes.lastElement().getId() + 1;
        }
    }

    public Vector<Node> getNodes()
    {
        return this.nodes;
    }

    public Vector<Link> getLinks()
    {
        return this.links;
    }

    public String getName()
    {
        return this.name;
    }

    public Node getEndNode()
    {
        return this.endNode;
    }

    private Vector<Node> createLinks(Vector<Node> sourceNodes, Node targetNode)
    {
        Vector<Node> remainingNodes = new Vector<Node>(sourceNodes);

        //Create links
        Iterator<Node> it = remainingNodes.iterator();

        while(it.hasNext())
        {
            Node sourceNode = it.next();

            if(((NodeLinker)sourceNode).tryLinking())
            {
                Link link = getEmptyTargetLink(sourceNode);

                //Set link target
                link.setTargetNode(targetNode);

                this.links.add(link);

                it.remove();
            }
        }

        return remainingNodes;
    }

    private void prettifyLayout()
    {
        int locX = 0;
        int locY = 0;

        for(Node node : nodes)
        {
            node.setLocation(locX, locY);

            locX = locX;
            locY = locY + 64;
        }

        this.endNode.setLocation(locX, locY);
    }
}
