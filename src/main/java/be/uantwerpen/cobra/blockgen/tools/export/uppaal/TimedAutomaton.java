package be.uantwerpen.cobra.blockgen.tools.export.uppaal;

import be.uantwerpen.cobra.blockgen.models.blocks.*;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.models.Link;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.models.Node;

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
    private Node startNode;
    private ChanSet ChanSet;

    
    public TimedAutomaton()
    {
        this.name = "AUTOMATON";
        this.nodes = new Vector<Node>();
        this.links = new Vector<Link>();
        this.ChanSet = new ChanSet();
        this.startNode = new Node();
        this.startNode.setName("START");
        this.startNode.setComments("");
        this.startNode.setInitial(true);
        this.endNode = new Node();
        this.endNode.setName("END");
        this.endNode.setComments("");
        this.endNode.setCommitted(true);

    }

    public int createAutomaton(Block model, int abstractionDepth) throws Exception
    {
        Vector<Node> endNodes = new Vector<Node>();

        if(model.getClass() != MethodBlock.class)
        {
            throw new Exception("Top block needs to be from the class MethodBlock!");
        }

        //Set automaton name
        this.name = ((MethodBlock)model).getMethodName();
        
        //Set start/initial node
        this.startNode.setId(getNextFreeId());
        this.nodes.add(this.startNode);
        
        //Set committed start node in main file
        if (this.name.equals("main")) 
        {
        	this.startNode.setCommitted(true); 
        }
        
        Node node = new Node();
        node.setId(getNextFreeId());
        node.setName(((MethodBlock) model).getMethodName());
       
        
        if(abstractionDepth != 0)
        {
            node.setComments(model.getCodeSegment().toString());
        }
        else
        {
            node.setComments(model.getCodeString());
        }
        
        this.nodes.add(node);        
        
        Link linkStart = getEmptyTargetLink(this.startNode);

        //Set link from start node to target
        linkStart.setTargetNode(node);
        if(!this.name.equals("main"))
        {
        	WaitChan(linkStart, this.name);
        }
        
        this.links.add(linkStart);

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
        
        //this.nodes.add(this.endNode);
        
        
        
        //Link to end and end to exit if no return node is available 
        if(!endNodes.isEmpty())
        {
        	
            //Create links
            for(Node lastNode : endNodes)
            {
            	//Set last node to end
                Link link = getEmptyTargetLink(lastNode);

                //Set link target
                link.setTargetNode(this.endNode);
          
                this.links.add(link);
                
                //Set end node to exit node
                Link linkExit = getEmptyTargetLink(this.endNode);

                //Set link target
                linkExit.setTargetNode(this.startNode);
               
                if(!this.name.equals("main"))
                {
                	SetChan(linkExit, this.name + "_out");
                }
          
                this.links.add(linkExit);         
            }      
        }
        
        for(Node itnode : this.nodes)
        {
        	//Commit function call nodes
        	if(itnode.getName().equals(this.name))
        	{
        		itnode.setCommitted(true);
        	}
        	
        	if(itnode.getName().matches("r"+ ".*"))
        	{
        		itnode.setInvariant( "x&lt;=" + itnode.getName() + "_WCET" );
        		Vector<Link> links = itnode.getLinks();
        		
        		for(Link link : links)
        		{
        			link.setGuard("x&gt;=" + itnode.getName() + "_WCET" );
        		}
        		
        	}     
        	
        }
        
        for(Link itlink : this.links)
        {
        	itlink.setUpdate("x:=0");
        	
        	
        }
        
        
        
        //Set the beginning node of the Chain
        //if(this.name.equals("main"))
  
       // if(this.name.equals("quicksort_init"))
       this.ChanSet.addChanSet("222");
    
       System.out.println("aaaaaaaaaaaaaaaaaaaaa" + ChanSet.getChanSet(0));
        
        this.prettifyLayout();

        return 0;
    }

    private void SetChan (Link link, String ChanName)
    {
    	link.setSync("_" + ChanName + "!");
    }
    
    private void WaitChan (Link link, String ChanName)
    {
    	link.setSync("_" + ChanName + "?");
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
                Node nodeCheck = createBasicNode(block, leftAbstractionLevel);

                //this.nodes.add(node);
                
                //this.nodes.add(testNode);
                Vector<Node> nodes = functionCallCheck(nodeCheck);
                
                
                //Create link
                for(Node lastNode : lastNodes)
                {
                    Link link = getEmptyTargetLink(lastNode);

                    //Set link target
                    link.setTargetNode(nodes.get(0));

                    this.links.add(link);
                }

                //Set new last node
                lastNodes.clear();
                lastNodes.add(nodes.get(nodes.size()-1));      

                if(block.getNumOfChildren() > 0 && leftAbstractionLevel != 0)
                {
                	lastNodes = addNodesRecursive(block, lastNodes, nextAbstractionLevel);
                }
            }
        }

        return lastNodes;
    }

    private Vector<Node> functionCallCheck(Node node)
    {
    	Vector<Node> returnNodes = new Vector<Node>();
    	
    	if(node.getComments().split("();").length == node.getComments().split("\n").length && node.getComments().contains("();") )
    	{	     		
    		String[] Chan = node.getComments().replaceAll("\n", "").replaceAll(" ", "").split("\\(" + "\\)" + ";");
 
    		for(int cnt=0; cnt< Chan.length ; cnt++)
    		{
				Node functionCallStartNode = new Node(); 
    			functionCallStartNode.setId(getNextFreeId());		
    			functionCallStartNode.setName(Chan[cnt]);
    			functionCallStartNode.setComments(Chan[cnt] + "();");
    			functionCallStartNode.setCommitted(true);
    			
    			
    			this.nodes.add(functionCallStartNode);
    			
    			Node functionCallEndNode = new Node();    			
    			functionCallEndNode.setId(getNextFreeId());
    			functionCallEndNode.setName(Chan[cnt] + "_out");
    			functionCallEndNode.setComments("");    		
    			
    			this.nodes.add(functionCallEndNode);
    		
    			returnNodes.add(functionCallStartNode);
    			returnNodes.add(functionCallEndNode);
    			
  
    			Link linkCallStart = getEmptyTargetLink(functionCallStartNode);
    			SetChan(linkCallStart, Chan[cnt]);  
           			
           		Link linkCallEnd = getEmptyTargetLink(functionCallEndNode);           			
           		WaitChan(linkCallEnd,Chan[cnt] + "_out");      		
           		
           		if(cnt < Chan.length - 1)
           		{
           			this.links.add(linkCallStart);           			
               		this.links.add(linkCallEnd);
           		}
           		else
           		{
           			this.links.add(linkCallStart);     
           		}
    		}
    		
    		
    	    for(int cnt = 0; cnt < returnNodes.size()-1; cnt++)
    	    {
    	    	Link Link = returnNodes.get(cnt).getLinks().get(0);
    	    	Link.setTargetNode(returnNodes.get(cnt+1));
    			
    	    }	
    	   
 	     	   		
    	} 
    	else
    	{
    		this.nodes.add(node);
    		
    		returnNodes.add(node);
    		
    	}
    	
    	
    	return returnNodes;
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
        node = new Node();
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
            for(Node lastNode : lastNodes)
            {
                Link link = getEmptyTargetLink(lastNode);

                //Set link target
                link.setTargetNode(node);

                this.links.add(link);
            }

            //Set new last node
            lastNodes.clear();
            lastNodes.add(node);
        }
        else
        {
            if(iterationBlock.getCodeSegment().toString().startsWith("for"))
            {
                String initString = iterationBlock.getCodeSegment().toString();
                initString = initString.split(";")[0];
                initString = initString.split("\\(")[1];

                //Create initialisation node
                if(!initString.isEmpty())
                {
                    Node node = new Node();
                    node.setId(getNextFreeId());
                    node.setName("r" + iterationBlock.getStartRowNumber() + "_init");
                    node.setComments(initString + ";");

                    this.nodes.add(node);

                    //Create link with last nodes
                    for(Node lastNode : lastNodes)
                    {
                        Link link = getEmptyTargetLink(lastNode);

                        //Set link target
                        link.setTargetNode(node);

                        this.links.add(link);
                    }

                    //Set new last node
                    lastNodes.clear();
                    lastNodes.add(node);
                }
            }

            //Create iteration condition node
            Node conditionNode = new Node();
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
            for(Node lastNode : lastNodes)
            {
                Link link = getEmptyTargetLink(lastNode);

                //Set link target
                link.setTargetNode(conditionNode);

                this.links.add(link);
            }

            //Set new last node
            lastNodes.clear();
            lastNodes.add(conditionNode);

            Vector<Node> iterationInNodes = new Vector<Node>(lastNodes);

            //Add iteration body
            lastNodes = addNodesRecursive(iterationBlock, lastNodes, nextAbstractionLevel);

            if(iterationBlock.getCodeSegment().toString().startsWith("for"))
            {
                String postString = iterationBlock.getCodeSegment().toString();
                postString = postString.split(";")[2];
                postString = postString.substring(0, postString.lastIndexOf(")"));

                //Create post statement node
                if(!postString.isEmpty())
                {
                    Node node = new Node();
                    node.setId(getNextFreeId());
                    node.setName("r" + iterationBlock.getStartRowNumber() + "_post");
                    node.setComments(postString + ";");

                    this.nodes.add(node);

                    //Create link with last nodes
                    for(Node lastNode : lastNodes)
                    {
                        Link link = getEmptyTargetLink(lastNode);

                        //Set link target
                        link.setTargetNode(node);

                        this.links.add(link);
                    }

                    //Set new last node
                    lastNodes.clear();
                    lastNodes.add(node);
                }
            }

            if(iterationBlock.isDoWhileStatement())
            {
                //Create iteration condition node at the end
                Node conditionDoWhileNode = new Node();

                conditionDoWhileNode.setName("r" + iterationBlock.getStartRowNumber());
                conditionDoWhileNode.setId(getNextFreeId());
                conditionDoWhileNode.setComments(conditionString);

                this.nodes.add(conditionDoWhileNode);

                //Create link with last nodes
                for(Node lastNode : lastNodes)
                {
                    Link link = getEmptyTargetLink(lastNode);

                    //Set link target
                    link.setTargetNode(conditionDoWhileNode);

                    this.links.add(link);
                }

                //Set new last node
                lastNodes.clear();
                lastNodes.add(conditionDoWhileNode);
            }

            //Create iteration loop links
            for(Node lastNode : lastNodes)
            {
                //Get iteration start nodes
                for(Node iterationInNode : iterationInNodes)
                {
                    Link link = getEmptyTargetLink(lastNode);

                    //Set link target
                    link.setTargetNode(iterationInNode);

                    //Add iteration loop link to last nodes of loop
                    lastNode.addLink(link);

                    if(iterationBlock.isDoWhileStatement())
                    {
                        //Set link guard (True statement)
                        link.setGuard("true");
                    }

                    this.links.add(link);
                }
            }

            //Create iteration exit links
            //Get guard condition false
            if(!iterationBlock.isDoWhileStatement())
            {
                for(Node iterationInNode : iterationInNodes)
                {
                    Link link = getEmptyTargetLink(iterationInNode);

                    //Set link guard (False statement)
                    link.setGuard("false");

                    //Add node to last node list
                    lastNodes.clear();
                    lastNodes.add(iterationInNode);
                }
            }
            else
            {
                for(Node lastNode : lastNodes)
                {
                    Link link = getEmptyTargetLink(lastNode);

                    //Set link guard (False statement)
                    link.setGuard("false");
                }
            }
        }

        return lastNodes;
    }

    private Vector<Node> createSelectionNode(SelectionBlock selectionBlock, Vector<Node> parentNodes, int leftAbstractionLevel)
    {
        int nextAbstractionLevel = leftAbstractionLevel - 1;
        Vector<Node> lastNodes = new Vector<Node>();

        if(leftAbstractionLevel == 0)
        {
            Node node = createBasicNode(selectionBlock, leftAbstractionLevel);

            this.nodes.add(node);

            //Create link
            for(Node parentNode : parentNodes)
            {
                Link link = getEmptyTargetLink(parentNode);

                //Set link target
                link.setTargetNode(node);

                this.links.add(link);
            }

            //Set new last nodes
            lastNodes.add(node);
        }
        else
        {
            //Create selection condition node
            Node selectionNode = new Node();
            String selectionString = selectionBlock.getCodeSegment().toString();

            selectionNode.setName("r" + selectionBlock.getStartRowNumber());
            selectionNode.setId(getNextFreeId());
            selectionNode.setComments(selectionString);

            this.nodes.add(selectionNode);

            //Create link with parent nodes
            for(Node parentNode : parentNodes)
            {
                Link link = getEmptyTargetLink(parentNode);

                //Set link target
                link.setTargetNode(selectionNode);

                this.links.add(link);
            }

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
                        lastNodes.addAll(addNodesRecursive(caseBlock, new Vector<Node>(Collections.singleton(selectionNode)), nextAbstractionLevel));
                    }
                }

                if(!trueStatement && falseStatement)                    //Close true case if not existing
                {
                    //Create link with selection statement node
                    Link link = getEmptyTargetLink(selectionNode);

                    //Set link guard
                    link.setGuard("true");

                    lastNodes.add(selectionNode);
                }
                else if(!falseStatement && trueStatement)               //Close false case if not existing
                {
                    //Create link with selection statement node
                    Link link = getEmptyTargetLink(selectionNode);

                    //Set link guard
                    link.setGuard("false");

                    lastNodes.add(selectionNode);
                }
                else if(!(trueStatement && falseStatement))            //None completed if-else statement or non-functional selection statement if();
                {
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
                    Node caseNode = new Node();
                    String caseString = caseBlock.getCodeSegment().toString();

                    caseNode.setName("r" + caseBlock.getStartRowNumber());
                    caseNode.setId(getNextFreeId());
                    caseNode.setComments(caseString);

                    this.nodes.add(caseNode);

                    //Create link with previous case nodes
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

                //To Do: Build check for cases without break statement!!
            }
        }

        return lastNodes;
    }

    private Vector<Node> createJumpNode(JumpBlock jumpBlock, Vector<Node> parentNodes, int leftAbstractionLevel)
    {
        int nextAbstractionLevel = leftAbstractionLevel - 1;
        Vector<Node> lastNodes = new Vector<Node>();

        //Create jump node
        Node jumpNode = new Node();
        String jumpString = jumpBlock.getCodeSegment().toString();

        jumpNode.setName("r" + jumpBlock.getStartRowNumber());
        jumpNode.setId(getNextFreeId());
        jumpNode.setComments(jumpString);

        this.nodes.add(jumpNode);

        //Create link with parent nodes
        for(Node parentNode : parentNodes)
        {
            Link link = getEmptyTargetLink(parentNode);

            //Set link target
            link.setTargetNode(jumpNode);

            this.links.add(link);
        }

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
            //Add node to last nodes list to connect loop exit
            //lastNodes.add(jumpNode);
            System.err.println("Break statement block is not yet implemented!");
            System.err.println("Results will not be correct!");
        }
        else if(jumpNode.getComments().contains("continue"))
        {
            //Continue statement
            //Add node to last nodes list to connect to next iteration evaluation
            //lastNodes.add(jumpNode);
            System.err.println("Continue statement block is not yet implemented!");
            System.err.println("Results will not be correct!");
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
