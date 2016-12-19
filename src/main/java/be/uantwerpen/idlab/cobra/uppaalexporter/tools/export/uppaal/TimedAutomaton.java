package be.uantwerpen.idlab.cobra.uppaalexporter.tools.export.uppaal;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.*;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.iterationblocks.DoWhileBlock;
import be.uantwerpen.idlab.cobra.blockgen.tools.export.uppaal.models.Link;
import be.uantwerpen.idlab.cobra.blockgen.tools.export.uppaal.models.Node;

import java.nio.file.Files;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Thomas on 25/04/2016.
 */
public class TimedAutomaton {
	private String name;
	private Vector<Node> nodes;
	private Vector<Link> links;
	private Node endNode;
	private Node startNode;
	private String localVariables;
	String eol = System.getProperty("line.separator");

	public TimedAutomaton() {
		this.name = "AUTOMATON";
		this.nodes = new Vector<Node>();
		this.links = new Vector<Link>();
		this.startNode = new Node();
		this.startNode.setName("START");
		this.startNode.setComments("");
		this.startNode.setInitial(true);
		this.endNode = new Node();
		this.endNode.setName("END");
		this.endNode.setComments("");
		this.endNode.setCommitted(true);
		this.localVariables = "";

	}

	public int createAutomaton(Block model, int abstractionDepth) throws Exception {

		Vector<Node> endNodes = new Vector<Node>();
		Vector<Node> condNodes = new Vector<Node>();
		if (model.getClass() != MethodBlock.class) {
			throw new Exception("Top block needs to be from the class MethodBlock!" + model.getClass() +"XXXX " + MethodBlock.class);
		}
		// Set automaton name
		this.name = ((MethodBlock) model).getName().replace("*", "");

		// Set start/initial node
		this.startNode.setId(getNextFreeId());
		this.nodes.add(this.startNode);

		// Set committed start node in main file
		if (this.name.equals("main")) {
			this.startNode.setCommitted(true);
		}

		Node node = new Node();
		node.setId(getNextFreeId());
		node.setName(this.name);

		if (abstractionDepth != 0) {
			node.setComments(model.getCodeSegment().toString());
		} else {
			node.setComments(model.getCodeString());
		}

		this.nodes.add(node);

		Link linkStart = getEmptyTargetLink(this.startNode);

		// Set link from start node to target
		linkStart.setTargetNode(node);
		if (!this.name.equals("main")) {
			WaitChan(linkStart, this.name);
		}

		this.links.add(linkStart);

		if (abstractionDepth != 0) {
			Vector<Node> parentNodes = new Vector<Node>();

			parentNodes.add(node);

			endNodes = addNodesRecursive(model, parentNodes, abstractionDepth - 1);
		} else {
			endNodes.add(node);
		}

		// Set id of end node
		this.endNode.setId(getNextFreeId());

		// Link to end if no return node is available
		if (!endNodes.isEmpty()) {

			// Create links
			for (Node lastNode : endNodes) {
				// Set last node to end
				Link link = getEmptyTargetLink(lastNode);
				// Set link target
				link.setTargetNode(this.endNode);
				this.links.add(link);
			}
		}

		// Link end node to start node
		Link linkExit = getEmptyTargetLink(this.endNode);

		// Set link target
		linkExit.setTargetNode(this.startNode);

		// Link end node to start node except for main function
		if (!this.name.equals("main")) {
			SetChan(linkExit, this.name + "_out");
			this.links.add(linkExit);
		}

		// Set Update of links to localclock:=0 for timing analysis
		for (Link itlink : this.links) {
			itlink.setUpdate("localclock:=0");
		}

		// Declare local clock
		String lclock = "clock localclock;";

		// Model transformation for iteration blocks

		Vector<Node> forNodes = forNodesCollection(this.nodes);
		Vector<Node> whileNodes = whileNodesCollection(this.nodes);
		Vector<Node> dowhileNodes = dowhileNodesCollection(this.nodes);

		genericModelTransformation(this.nodes, this.name);

		forLoopModelTransformation(forNodes);

		whileLoopModelTransformation(whileNodes);

		dowhileLoopModelTransformation(dowhileNodes);

		// Write local clock
		setLocalVariables(lclock + eol);

		/*
		 * try { PrintWriter writer = new PrintWriter(
		 * "/Users/HLi/Documents/UA/Haoxuan/Benchmarks/TEST/" + this.name +
		 * ".txt", "UTF-8"); writer.println(whilevariables_post_list_export);
		 * writer.close(); } catch (IOException e) { // do something }
		 */
		/*
		 * BufferedReader whilevariables_post_list_buffer = new BufferedReader(
		 * new FileReader("/Users/HLi/Documents/UA/Haoxuan/Benchmarks/TEST/" +
		 * this.name + ".txt")); StringBuilder whilevariables_post_list_write =
		 * new StringBuilder(); try { String line =
		 * whilevariables_post_list_buffer.readLine();
		 * 
		 * while (line != null) { whilevariables_post_list_write.append(line);
		 * whilevariables_post_list_write.append("\n"); line =
		 * whilevariables_post_list_buffer.readLine(); }
		 * 
		 * } finally { whilevariables_post_list_buffer.close(); }
		 * setLocalVariables(whilevariables_post_list_write.toString());
		 * 
		 * try { PrintWriter writer = new PrintWriter(new FileOutputStream( new
		 * File("/Users/HLi/Documents/UA/Haoxuan/Benchmarks/TEST/" +
		 * "testprintf" + ".txt"), true));
		 * 
		 * writer.println("aaaa"); writer.close(); } catch (IOException e) { //
		 * do something }
		 */

		linkTransformation(this.links);

		// Adjust layout
		this.prettifyLayout();

		return 0;
	}

	private Vector<Node> forNodesCollection(Vector<Node> nodes) {
		// Temporary Node vector to collect iteration nodes
		Vector<Node> iterationNodes_temp = new Vector<Node>();
		// Collect for iteration nodes
		Vector<Node> forNodes = new Vector<Node>();
		for (Node itnode : nodes) {
			if (itnode.getName().matches("r" + "\\d+_init")) {
				iterationNodes_temp.add(itnode);
			}
		}

		for (Node fornode : iterationNodes_temp) {
			String fornodename = fornode.getName().replaceAll("_init", "");
			for (Node itnode : nodes) {
				if (itnode.getName().contains(fornodename)) {
					forNodes.add(itnode);
				}
			}
		}
		return forNodes;
	}

	private Vector<Node> whileNodesCollection(Vector<Node> nodes) {
		// Temporary Node vector to collect iteration nodes
		Vector<Node> iterationNodes_temp = new Vector<Node>();
		// Collect while iteration nodes and dowhile iteration nodes
		Vector<Node> whileNodes = new Vector<Node>();

		// Collect while nodes (including the while nodes in dowhile loop)
		for (Node itnode : nodes) {
			if (itnode.getName().matches("r" + "\\d+_cond") && itnode.getComments().startsWith("while")) {
				iterationNodes_temp.add(itnode);
			}
		}
		for (Node whilenode : iterationNodes_temp) {
			String whilenodename = whilenode.getName().replaceAll("_cond", "");
			for (Node itnode : nodes) {
				if (itnode.getName().contains(whilenodename)) {
					whileNodes.add(itnode);
				}
			}
		}

		iterationNodes_temp.clear();

		// Collect dowhile nodes
		Vector<Node> dowhileNodes = new Vector<Node>();

		for (Node itnode : nodes) {
			if (itnode.getName().matches("r" + "\\d+_do")) {
				iterationNodes_temp.add(itnode);
			}
		}
		for (Node dowhilenode : iterationNodes_temp) {
			String dowhilenodename = dowhilenode.getName().replaceAll("_do", "");
			for (Node itnode : nodes) {
				if (itnode.getName().contains(dowhilenodename)) {
					dowhileNodes.add(itnode);
					whileNodes.remove(itnode);
				}
			}
		}
		return whileNodes;

	}

	private Vector<Node> dowhileNodesCollection(Vector<Node> nodes) {
		// Temporary Node vector to collect iteration nodes
		Vector<Node> iterationNodes_temp = new Vector<Node>();
		// Collect while iteration nodes and dowhile iteration nodes
		Vector<Node> whileNodes = new Vector<Node>();

		// Collect while nodes (including the while nodes in dowhile loop)
		for (Node itnode : nodes) {
			if (itnode.getName().matches("r" + "\\d+_cond") && itnode.getComments().startsWith("while")) {
				iterationNodes_temp.add(itnode);
			}
		}
		for (Node whilenode : iterationNodes_temp) {
			String whilenodename = whilenode.getName().replaceAll("_cond", "");
			for (Node itnode : nodes) {
				if (itnode.getName().contains(whilenodename)) {
					whileNodes.add(itnode);
				}
			}
		}

		iterationNodes_temp.clear();

		// Collect dowhile nodes
		Vector<Node> dowhileNodes = new Vector<Node>();

		for (Node itnode : nodes) {
			if (itnode.getName().matches("r" + "\\d+_do")) {
				iterationNodes_temp.add(itnode);
			}
		}
		for (Node dowhilenode : iterationNodes_temp) {
			String dowhilenodename = dowhilenode.getName().replaceAll("_do", "");
			for (Node itnode : nodes) {
				if (itnode.getName().contains(dowhilenodename)) {
					dowhileNodes.add(itnode);
					whileNodes.remove(itnode);
				}
			}
		}
		return dowhileNodes;

	}

	private void genericModelTransformation(Vector<Node> nodes, String name) {
		String localVariablesWCET = "";

		for (Node itnode : nodes) {
			// Commit function call nodes
			if (itnode.getName().equals(name)) {
				itnode.setCommitted(true);
			}

			// Set Guard and Invariant for normal nodes and links
			if (itnode.getName().matches("r" + "\\d+.*") && !itnode.getCommitted()) {

				// Set Guard of normal links
				Vector<Link> links = itnode.getLinks();

				for (Link link : links) {
					String guard_org = link.getGuard() != null ? link.getGuard() : "";
					link.setGuard(guard_org.replaceAll("localclock&gt;=" + itnode.getName() + "_WCET", "")
							+ "localclock&gt;=" + itnode.getName() + "_WCET");
				}

				// Set Invariant of normal nodes and special nodes
				itnode.setInvariant("localclock&lt;=" + itnode.getName() + "_WCET");
			}

			// Set links for special nodes (_cond _init _post)
			if (itnode.getName().matches("r" + "\\d+.*")) {

				// Set Update and Guard of links of _post block of iteration
				// statements

				// Collect local WCET variables
				localVariablesWCET = localVariablesWCET + itnode.getName() + "_WCET=1, ";

			}

		}

		if (!localVariablesWCET.isEmpty()) {
			localVariablesWCET = "int " + localVariablesWCET.substring(0, localVariablesWCET.length() - 2) + ";";
			setLocalVariables(localVariablesWCET + eol);
		}

	}

	private void forLoopModelTransformation(Vector<Node> forNodes) {
		String Counters = "";
		String LoopBounds = "";

		for (Node itnode : forNodes) {
			// For loop _init node

			// For loop _cond node
			if (itnode.getName().contains("_cond")) {
				// For loop set the guard to be added to true link
				String Counter = itnode.getName().replaceAll("_cond", "") + "_Counter";
				String LoopBound = itnode.getName().replaceAll("_cond", "") + "_LoopBound";
				String annotation = "_Pragma( " + "loopbound min 1 max 100" + ")";
				
				String guardConditionFor = Counter + " &lt; " + LoopBound;

				// Set Guard of the false link of _cond node
				Vector<Link> condlinks = itnode.getLinks();

				// For loop _cond node link set for loop _cond node set
				for (Link link : condlinks) {
					// Set Guard of link of false link of _cond node
					if (link.getGuard().contains("false")) {
						String guardCondition_temp = link.getGuard() != null ? link.getGuard() + "&amp;&amp; !(" : "";
						link.setGuard(guardCondition_temp.replace("&amp;&amp; !(" + guardConditionFor + ")", "")
								+ guardConditionFor + ")");

					}
					// Set Guard of link of true link of _cond node
					if (link.getGuard().contains("true")) {
						String guardCondition_temp = link.getGuard() != null ? link.getGuard() + "&amp;&amp; (" : "";
						link.setGuard(guardCondition_temp.replace("&amp;&amp; (" + guardConditionFor + ")", "")
								+ guardConditionFor + ")");
					}
				}
				// Get the condition variables in while loop
				Counters = Counters + Counter + " = 0, ";
				LoopBounds = LoopBounds + "int " + LoopBound + " ="
						+ annotation.replaceAll(".*max", "").replaceAll("\\)", "") + ";" + eol;
			}

			// For loop _post node
			if (itnode.getName().contains("_post")) {
				// Define Update for link of _post node
				String iteratorUpdate = itnode.getName().replaceAll("_post", "") + "_Counter++";
				// Set Update for iteration nodes
				Vector<Link> postlinks = itnode.getLinks();
				for (Link link : postlinks) {
					// Set Update of link of _post node
					link.setUpdate("localclock:=0, " + iteratorUpdate);
				}
			}
		}
		if (!Counters.isEmpty())
			setLocalVariables("int " + Counters.substring(0, Counters.length() - 2) + ";" + eol);
		if (!LoopBounds.isEmpty())
			setLocalVariables(LoopBounds + eol);
	}

	private void whileLoopModelTransformation(Vector<Node> whileNodes) {
		String Counters = "";
		String LoopBounds = "";
		for (Node itnode : whileNodes) {

			if (itnode.getName().contains("_cond")) {

				Node conditionNode = new Node();
				conditionNode = itnode;
				String Counter = itnode.getName().replaceAll("_cond", "") + "_Counter";
				String LoopBound = itnode.getName().replaceAll("_cond", "") + "_LoopBound";
				String annotation = "_Pragma( " + "loopbound min 1 max 100" + ")";
				// While loop set the guard to be added to true link
				String guardConditionWhile = Counter + " &lt; " + LoopBound;

				// Set Guard of the false link of _cond node
				Vector<Link> condlinks = conditionNode.getLinks();

				// While loop _cond node link set for loop _cond node set
				for (Link link : condlinks) {
					// Set Guard of link of false link of _cond node
					if (link.getGuard().contains("false")) {
						String guardCondition_temp = link.getGuard() != null ? link.getGuard() + "&amp;&amp; !(" : "";
						link.setGuard(guardCondition_temp.replace("&amp;&amp; !(" + guardConditionWhile + ")", "")
								+ guardConditionWhile + ")");
					}
					// Set Guard of link of true link of _cond node
					if (link.getGuard().contains("true")) {
						String guardCondition_temp = link.getGuard() != null ? link.getGuard() + "&amp;&amp; (" : "";
						link.setGuard(guardCondition_temp.replace("&amp;&amp; (" + guardConditionWhile + ")", "")
								+ guardConditionWhile + ")");
					}
				}
				// Get the condition variables in while loop
				Counters = Counters + Counter + " = 0, ";
				LoopBounds = LoopBounds + "int " + LoopBound + " ="
						+ annotation.replaceAll(".*max", "").replaceAll("\\)", "") + ";" + eol;
			}

			// While loop
			if (itnode.getName().contains("_post")) {
				// Define Update for link of _post node
				String iteratorUpdate = itnode.getName().replaceAll("_post", "") + "_Counter++";

				// Set Update for iteration nodes
				Vector<Link> postlinks = itnode.getLinks();
				for (Link link : postlinks) {
					// Set Update of link of _post node
					link.setUpdate("localclock:=0, " + iteratorUpdate);
				}
			}
		}
		if (!Counters.isEmpty())
			setLocalVariables("int " + Counters.substring(0, Counters.length() - 2) + ";" + eol);
		if (!LoopBounds.isEmpty())
			setLocalVariables(LoopBounds + eol);
	}

	private void dowhileLoopModelTransformation(Vector<Node> dowhileNodes) {
		String Counters = "";
		String LoopBounds = "";
		for (Node itnode : dowhileNodes) {
			if (itnode.getName().contains("_cond")) {

				String Counter = itnode.getName().replaceAll("_cond", "") + "_Counter";
				String LoopBound = itnode.getName().replaceAll("_cond", "") + "_LoopBound";
				String annotation = "_Pragma( " + "loopbound min 1 max 100" + ")";
				// While loop set the guard to be added to true link
				String guardConditiondoWhile = Counter + " &lt; " + LoopBound;
				// Set Guard of the false link of _cond node
				Vector<Link> condlinks = itnode.getLinks();

				// While loop _cond node link set for loop _cond node set
				for (Link link : condlinks) {
					// Set Guard of link of false link of _cond node
					if (link.getGuard().contains("false")) {
						String guardCondition_temp = link.getGuard() != null ? link.getGuard() + "&amp;&amp; !(" : "";
						link.setGuard(guardCondition_temp.replace("&amp;&amp; !(" + guardConditiondoWhile + ")", "")
								+ guardConditiondoWhile + ")");
					}
					// Set Guard of link of true link of _cond node
					if (link.getGuard().contains("true")) {
						String guardCondition_temp = link.getGuard() != null ? link.getGuard() + "&amp;&amp; (" : "";
						link.setGuard(guardCondition_temp.replace("&amp;&amp; (" + guardConditiondoWhile + ")", "")
								+ guardConditiondoWhile + ")");
					}
				}
				// Get the condition variables in while loop
				Counters = Counters + Counter + " = 0, ";
				LoopBounds = LoopBounds + "int " + LoopBound + " ="
						+ annotation.replaceAll(".*max", "").replaceAll("\\)", "") + ";" + eol;

			}

			// While loop
			if (itnode.getName().contains("_post")) {
				// Define Update for link of _post node
				String iteratorUpdate = itnode.getName().replaceAll("_post", "") + "_Counter++";

				// Set Update for iteration nodes
				Vector<Link> postlinks = itnode.getLinks();
				for (Link link : postlinks) {
					// Set Update of link of _post node
					link.setUpdate("localclock:=0, " + iteratorUpdate);
				}
			}
		}
		if (!Counters.isEmpty())
			setLocalVariables("int " + Counters.substring(0, Counters.length() - 2) + ";" + eol);
		if (!LoopBounds.isEmpty())
			setLocalVariables(LoopBounds + eol);
	}

	private void linkTransformation(Vector<Link> links) {
		// Remove all false and true Guard in links
		for (Link link : links) {
			// Remove "true"
			if (link.getGuard() != null) {
				if (link.getGuard().contains("true")) {
					String guardCondition_temp = link.getGuard();
					link.setGuard(guardCondition_temp.replaceAll("true", ""));
				}
				// Remove "false"
				if (link.getGuard().contains("false")) {
					String guardCondition_temp = link.getGuard();
					link.setGuard(guardCondition_temp.replaceAll("false", ""));
				}
			}
		}
	}

	private void SetChan(Link link, String ChanName) {
		link.setSync("_" + ChanName + "!");
	}

	private void WaitChan(Link link, String ChanName) {
		link.setSync("_" + ChanName + "?");
	}

	private Vector<Node> addNodesRecursive(Block currentBlock, Vector<Node> parentNodes, int leftAbstractionLevel) {
		int startRow = 0;
		int endRow = 0;
		int nextAbstractionLevel = leftAbstractionLevel - 1;
		Vector<Node> lastNodes = new Vector<Node>(parentNodes);

		for (Block block : currentBlock.getChildBlocks()) {
			if (block instanceof IterationBlock) {
				lastNodes = createIterationNode((IterationBlock) block, lastNodes, leftAbstractionLevel);
			} else if (block instanceof SelectionBlock) {
				lastNodes = createSelectionNode((SelectionBlock) block, lastNodes, leftAbstractionLevel);
			} else if (block instanceof JumpBlock) {
				lastNodes = createJumpNode((JumpBlock) block, lastNodes, leftAbstractionLevel);
			} else {
				Node nodeCheck = createBasicNode(block, leftAbstractionLevel);

				startRow = block.getStartRowNumber();

				Vector<Node> nodes = functionCallCheck(nodeCheck, startRow);

				// Create link
				for (Node lastNode : lastNodes) {
					Link link = getEmptyTargetLink(lastNode);

					// Set link target
					link.setTargetNode(nodes.get(0));

					this.links.add(link);
				}

				// Set new last node
				lastNodes.clear();
				lastNodes.add(nodes.get(nodes.size() - 1));

				if (block.getNumOfChildren() > 0 && leftAbstractionLevel != 0) {
					lastNodes = addNodesRecursive(block, lastNodes, nextAbstractionLevel);
				}
			}
		}

		return lastNodes;
	}

	private Vector<Node> functionCallCheck(Node node, int startRowNumber) {
		Vector<Node> returnNodes = new Vector<Node>();

		// System.out.println((node.getComments().length() -
		// node.getComments().replaceAll("[\\s]", "").replace(");",
		// "").length())/2 + " " + (node.getComments().length() -
		// node.getComments().replace(";", "").length()));
		if ((node.getComments().length() - node.getComments().replace(");", "").length())
				/ 2 == (node.getComments().length() - node.getComments().replace(";", "").length())
				&& node.getComments().replaceAll("[\\s]", "").contains(");") && !node.getComments().contains("=")) {
			String[] Chan = node.getComments().replaceAll("[\n\\s]", "").split("\\)" + ";");
			for (int cnt = 0; cnt < Chan.length; cnt++) {
				Node functionCallStartNode = new Node();
				Chan[cnt] = Chan[cnt].replaceAll("\\(.*", "");
				functionCallStartNode.setId(getNextFreeId());
				functionCallStartNode.setName(Chan[cnt] + "_r" + (startRowNumber + cnt));
				// System.out.println(Chan[cnt]);
				functionCallStartNode.setComments(Chan[cnt] + "();");
				functionCallStartNode.setCommitted(true);

				this.nodes.add(functionCallStartNode);

				Node functionCallEndNode = new Node();
				functionCallEndNode.setId(getNextFreeId());
				functionCallEndNode.setName(Chan[cnt] + "_out_r" + (startRowNumber + cnt));
				functionCallEndNode.setComments("");

				this.nodes.add(functionCallEndNode);

				returnNodes.add(functionCallStartNode);
				returnNodes.add(functionCallEndNode);

				Link linkCallStart = getEmptyTargetLink(functionCallStartNode);
				SetChan(linkCallStart, Chan[cnt]);

				Link linkCallEnd = getEmptyTargetLink(functionCallEndNode);
				WaitChan(linkCallEnd, Chan[cnt] + "_out");

				if (cnt < Chan.length - 1) {
					this.links.add(linkCallStart);
					this.links.add(linkCallEnd);
				} else {
					this.links.add(linkCallStart);
				}
			}

			for (int cnt = 0; cnt < returnNodes.size() - 1; cnt++) {
				Link Link = returnNodes.get(cnt).getLinks().get(0);
				Link.setTargetNode(returnNodes.get(cnt + 1));

			}

		} else {
			this.nodes.add(node);

			returnNodes.add(node);

		}

		return returnNodes;
	}

	private Link getEmptyTargetLink(Node node) {
		Link link = null;
		boolean found = false;
		Iterator<Link> it = node.getLinks().iterator();

		while (it.hasNext() && !found) {
			link = it.next();

			if (link.getTargetNode() == null) {
				found = true;
			}
		}

		if (found) {
			return link;
		} else {
			link = new Link();
			node.addLink(link);

			return link;
		}
	}

	private Node createBasicNode(Block block, int leftAbstractionLevel) {
		Node node;
		int startRow;
		int endRow;

		// Create node
		node = new Node();
		node.setId(getNextFreeId());

		startRow = block.getStartRowNumber();

		if (leftAbstractionLevel != 0) {
			endRow = block.getEndRowNumber();
			node.setComments(block.getCodeSegment().toString());
		} else {
			endRow = block.getLeafs().lastElement().getEndRowNumber();
			node.setComments(block.getCodeString());
		}

		if (startRow == endRow) {
			node.setName("r" + startRow);
		} else {
			node.setName("r" + startRow + "_r" + endRow);
		}

		return node;
	}

	private Vector<Node> createIterationNode(IterationBlock iterationBlock, Vector<Node> parentNodes,
			int leftAbstractionLevel) {
		int nextAbstractionLevel = leftAbstractionLevel - 1;
		Vector<Node> lastNodes = new Vector<Node>(parentNodes);

		if (leftAbstractionLevel == 0) {
			Node node = createBasicNode(iterationBlock, leftAbstractionLevel);

			this.nodes.add(node);

			// Create link
			for (Node lastNode : lastNodes) {
				Link link = getEmptyTargetLink(lastNode);

				// Set link target
				link.setTargetNode(node);

				this.links.add(link);
			}

			// Set new last node
			lastNodes.clear();
			lastNodes.add(node);
		} else {
			if (iterationBlock.getCodeSegment().toString().startsWith("for")) {
				String initString = iterationBlock.getCodeSegment().toString();
				initString = initString.split(";")[0];
				initString = initString.split("\\(")[1];

				// Create initialization node
				if (!initString.isEmpty()) {
					Node node = new Node();
					node.setId(getNextFreeId());
					node.setName("r" + iterationBlock.getStartRowNumber() + "_init");
					node.setComments(initString + ";");

					this.nodes.add(node);

					// Create link with last nodes
					for (Node lastNode : lastNodes) {
						Link link = getEmptyTargetLink(lastNode);

						// Set link target
						link.setTargetNode(node);

						this.links.add(link);
					}

					// Set new last node
					lastNodes.clear();
					lastNodes.add(node);
				}
			}

			// Create iteration condition node
			Node conditionNode = new Node();
			String conditionString = iterationBlock.getCodeSegment().toString();

			if (iterationBlock.getClass()!=DoWhileBlock.class) {
				if (iterationBlock.getCodeSegment().toString().startsWith("for")) {
					conditionNode.setName("r" + iterationBlock.getStartRowNumber() + "_cond");
					conditionString = conditionString.split(";")[1];
					conditionString = "for( ; " + conditionString + " ; )";
				} else if (iterationBlock.getCodeSegment().toString().startsWith("while")) {
					conditionNode.setName("r" + iterationBlock.getStartRowNumber() + "_cond");
					conditionString = iterationBlock.getCodeSegment().toString().replaceAll(" ", "");
				} else {
					conditionNode.setName("r" + iterationBlock.getStartRowNumber());
				}

				conditionNode.setComments(conditionString);

				// Create true statement links
				Link trueStatementLink = new Link();
				trueStatementLink.setGuard("true");

				conditionNode.addLink(trueStatementLink);
			} else {
				conditionNode.setName("r" + iterationBlock.getStartRowNumber() + "_do");
				conditionNode.setComments("do");
			}

			conditionNode.setId(getNextFreeId());

			this.nodes.add(conditionNode);

			// Create link with last nodes
			for (Node lastNode : lastNodes) {
				Link link = getEmptyTargetLink(lastNode);

				// Set link target
				link.setTargetNode(conditionNode);

				this.links.add(link);
			}

			// Set new last node
			lastNodes.clear();
			lastNodes.add(conditionNode);

			Vector<Node> iterationInNodes = new Vector<Node>(lastNodes);

			// Add iteration body
			lastNodes = addNodesRecursive(iterationBlock, lastNodes, nextAbstractionLevel);

			if (iterationBlock.getCodeSegment().toString().startsWith("for")) {
				String postString = iterationBlock.getCodeSegment().toString();
				postString = postString.split(";")[2];
				postString = postString.substring(0, postString.lastIndexOf(")"));

				// Create post statement node
				if (!postString.isEmpty()) {
					Node node = new Node();
					node.setId(getNextFreeId());
					node.setName("r" + iterationBlock.getStartRowNumber() + "_post");
					node.setComments(postString + ";");

					this.nodes.add(node);

					// Create link with last nodes
					for (Node lastNode : lastNodes) {
						Link link = getEmptyTargetLink(lastNode);

						// Set link target
						link.setTargetNode(node);

						this.links.add(link);
					}

					// Set new last node
					lastNodes.clear();
					lastNodes.add(node);
				}
			}

			if (iterationBlock.getCodeSegment().toString().startsWith("while")) {

				// Create post statement node for while loop
				Node node = new Node();
				node.setId(getNextFreeId());
				node.setName("r" + iterationBlock.getStartRowNumber() + "_post");
				node.setComments("while_post");
				node.setCommitted(true);
				;

				this.nodes.add(node);

				// Create link with last nodes
				for (Node lastNode : lastNodes) {
					Link link = getEmptyTargetLink(lastNode);

					// Set link target
					link.setTargetNode(node);

					this.links.add(link);
				}

				// Set new last node
				lastNodes.clear();
				lastNodes.add(node);

			}

			if (iterationBlock.getClass()==DoWhileBlock.class) {
				// Create iteration condition node at the end
				Node conditionDoWhileNode = new Node();

				conditionDoWhileNode.setName("r" + iterationBlock.getStartRowNumber() + "_cond");
				conditionDoWhileNode.setId(getNextFreeId());
				conditionDoWhileNode.setComments(conditionString);

				this.nodes.add(conditionDoWhileNode);

				// Create link with last nodes
				for (Node lastNode : lastNodes) {
					Link link = getEmptyTargetLink(lastNode);

					// Set link target
					link.setTargetNode(conditionDoWhileNode);

					this.links.add(link);
				}

				// Set new last node
				lastNodes.clear();
				lastNodes.add(conditionDoWhileNode);
			}

			// Create iteration loop links
			for (Node lastNode : lastNodes) {
				// Get iteration start nodes
				for (Node iterationInNode : iterationInNodes) {
					Link link = getEmptyTargetLink(lastNode);

					// Set link target
					link.setTargetNode(iterationInNode);

					// Add iteration loop link to last nodes of loop
					lastNode.addLink(link);

					if (iterationBlock.getClass()==DoWhileBlock.class) {
						// Set link guard (True statement)
						link.setGuard("true");
					}

					this.links.add(link);
				}
			}

			// Create iteration exit links
			// Get guard condition false
			if (iterationBlock.getClass()!=DoWhileBlock.class) {
				for (Node iterationInNode : iterationInNodes) {
					Link link = getEmptyTargetLink(iterationInNode);

					// Set link guard (False statement)
					link.setGuard("false");

					// Add node to last node list
					lastNodes.clear();
					lastNodes.add(iterationInNode);
				}
			} else {
				for (Node lastNode : lastNodes) {
					Link link = getEmptyTargetLink(lastNode);

					// Set link guard (False statement)
					link.setGuard("false");
				}
			}
		}

		return lastNodes;
	}

	private Vector<Node> createSelectionNode(SelectionBlock selectionBlock, Vector<Node> parentNodes,
			int leftAbstractionLevel) {
		int nextAbstractionLevel = leftAbstractionLevel - 1;
		Vector<Node> lastNodes = new Vector<Node>();

		if (leftAbstractionLevel == 0) {
			Node node = createBasicNode(selectionBlock, leftAbstractionLevel);

			this.nodes.add(node);

			// Create link
			for (Node parentNode : parentNodes) {
				Link link = getEmptyTargetLink(parentNode);

				// Set link target
				link.setTargetNode(node);

				this.links.add(link);
			}

			// Set new last nodes
			lastNodes.add(node);
		} else {
			// Create selection condition node
			Node selectionNode = new Node();
			String selectionString = selectionBlock.getCodeSegment().toString();

			selectionNode.setName("r" + selectionBlock.getStartRowNumber());
			selectionNode.setId(getNextFreeId());
			selectionNode.setComments(selectionString);

			this.nodes.add(selectionNode);

			// Create link with parent nodes
			for (Node parentNode : parentNodes) {
				Link link = getEmptyTargetLink(parentNode);

				// Set link target
				link.setTargetNode(selectionNode);

				this.links.add(link);
			}

			if (selectionBlock.getCodeSegment().toString().startsWith("if")) {
				// If-Else statement
				boolean trueStatement = false;
				boolean falseStatement = false;

				for (Block caseBlock : selectionBlock.getChildBlocks()) {
					String guardString;

					if (caseBlock.getNumOfChildren() != 0) {
						if (((CaseBlock) caseBlock).getClass().getName().contains("DoWhileBlock")) {
							// True case
							trueStatement = true;
							guardString = "true";
						} else {
							// False case
							falseStatement = true;
							guardString = "false";
						}

						// Create link with selection statement node
						Link link = getEmptyTargetLink(selectionNode);

						// Set link guard
						link.setGuard(guardString);

						// Add selection branch body
						lastNodes.addAll(addNodesRecursive(caseBlock,
								new Vector<Node>(Collections.singleton(selectionNode)), nextAbstractionLevel));
					}
				}

				if (!trueStatement && falseStatement) // Close true case if not
														// existing
				{
					// Create link with selection statement node
					Link link = getEmptyTargetLink(selectionNode);

					// Set link guard
					link.setGuard("true");

					lastNodes.add(selectionNode);
				} else if (!falseStatement && trueStatement) // Close false case
																// if not
																// existing
				{
					// Create link with selection statement node
					Link link = getEmptyTargetLink(selectionNode);

					// Set link guard
					link.setGuard("false");

					lastNodes.add(selectionNode);
				} else if (!(trueStatement && falseStatement)) // None completed
																// if-else
																// statement or
																// non-functional
																// selection
																// statement
																// if();
				{
					lastNodes.add(selectionNode);
				}
			} else if (selectionBlock.getCodeSegment().toString().startsWith("switch")) {
				Vector<Node> previousCaseNodes = new Vector<Node>(Collections.singleton(selectionNode));
				boolean firstCase = true;

				// Switch-case statement
				for (Block caseBlock : selectionBlock.getChildBlocks()) {
					Node caseNode = new Node();
					String caseString = caseBlock.getCodeSegment().toString();

					caseNode.setName("r" + caseBlock.getStartRowNumber());
					caseNode.setId(getNextFreeId());
					caseNode.setComments(caseString);

					this.nodes.add(caseNode);

					// Create link with previous case nodes
					for (Node previousCaseNode : previousCaseNodes) {
						Link link = getEmptyTargetLink(previousCaseNode);

						// Set link target
						link.setTargetNode(caseNode);

						if (!firstCase) {
							// Set guard (False)
							link.setGuard("false");
						} else {
							firstCase = false;
						}

						this.links.add(link);
					}

					// Create link for case body
					Link caseBodyLink = getEmptyTargetLink(caseNode);

					if (!caseString.startsWith("default:")) {
						// Set guard (True)
						caseBodyLink.setGuard("true");
					}

					lastNodes.addAll(addNodesRecursive(caseBlock, new Vector<Node>(Collections.singleton(caseNode)),
							nextAbstractionLevel));

					previousCaseNodes.clear();
					previousCaseNodes.add(caseNode);
				}

				// Check for empty switch (Pass through)
				if (selectionBlock.getChildBlocks().isEmpty()) {
					lastNodes.add(selectionNode);
				}

				// To Do: Build check for cases without break statement!!
			}
		}

		return lastNodes;
	}

	private Vector<Node> createJumpNode(JumpBlock jumpBlock, Vector<Node> parentNodes, int leftAbstractionLevel) {
		int nextAbstractionLevel = leftAbstractionLevel - 1;
		Vector<Node> lastNodes = new Vector<Node>();

		// Create jump node
		Node jumpNode = new Node();
		String jumpString = jumpBlock.getCodeSegment().toString();

		jumpNode.setName("r" + jumpBlock.getStartRowNumber());
		jumpNode.setId(getNextFreeId());
		jumpNode.setComments(jumpString);

		this.nodes.add(jumpNode);

		// Create link with parent nodes
		for (Node parentNode : parentNodes) {
			Link link = getEmptyTargetLink(parentNode);

			// Set link target
			link.setTargetNode(jumpNode);

			this.links.add(link);
		}

		if (jumpNode.getComments().contains("return")) {
			// Return statement
			// Create link with exit node
			Link link = getEmptyTargetLink(jumpNode);

			// Set link target
			link.setTargetNode(this.endNode);

			this.links.add(link);

		} else if (jumpNode.getComments().contains("break")) {
			// Break statement
			// Add node to last nodes list to connect loop exit
			// lastNodes.add(jumpNode);
			System.err.println("Break statement block is not yet implemented!");
			System.err.println("Results will not be correct!");
		} else if (jumpNode.getComments().contains("continue")) {
			// Continue statement
			// Add node to last nodes list to connect to next iteration
			// evaluation
			// lastNodes.add(jumpNode);
			System.err.println("Continue statement block is not yet implemented!");
			System.err.println("Results will not be correct!");
		} else if (jumpNode.getComments().contains("goto")) {
			// Go to statement
			System.err.println("Goto statement block is not yet implemented!");
			System.err.println("Results will not be correct!");
		}

		return lastNodes;
	}

	private int getNextFreeId() {
		if (this.nodes.isEmpty()) {
			return 0;
		} else {
			return this.nodes.lastElement().getId() + 1;
		}
	}

	public Vector<Node> getNodes() {
		return this.nodes;
	}

	public Vector<Link> getLinks() {
		return this.links;
	}

	public String getName() {
		return this.name;
	}

	public Node getEndNode() {
		return this.endNode;
	}

	public void setLocalVariables(String localVariable) {
		this.localVariables = this.localVariables + localVariable;

	}

	public String getLocalVariables() {
		return this.localVariables;
	}

	private void prettifyLayout() {
		int locX = 0;
		int locY = 0;

		for (Node node : nodes) {
			node.setLocation(locX, locY);

			locX = locX;
			locY = locY + 64;
		}

		this.endNode.setLocation(locX, locY);
	}
}
