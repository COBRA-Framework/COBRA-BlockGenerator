package be.uantwerpen.cobra.blockgen.tools.export.uppaal;

import be.uantwerpen.cobra.blockgen.models.blocks.*;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.models.Link;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.models.Node;

import java.nio.file.Files;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.io.BufferedReader;
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
			throw new Exception("Top block needs to be from the class MethodBlock!");
		}

		// Set automaton name
		this.name = ((MethodBlock) model).getMethodName();

		// Set start/initial node
		this.startNode.setId(getNextFreeId());
		this.nodes.add(this.startNode);

		// Set committed start node in main file
		if (this.name.equals("main")) {
			this.startNode.setCommitted(true);
		}

		Node node = new Node();
		node.setId(getNextFreeId());
		node.setName(((MethodBlock) model).getMethodName());

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

		// Set Update of links to x:=0 for timing analysis
		for (Link itlink : this.links) {
			itlink.setUpdate("x:=0");
		}

		// Declare local clock
		String lclock = "clock x;";

		// Declare local WCET variables
		String localVariablesWCET = "";

		// Declare local iterator variables
		String localIterators = "";

		// Declare the variables in while_cond
		String localWhileConditionVariables = "";

		// Declare elements needed to write the update function of while_post
		// while_post conditional variable value lists
		String whilevariables_post = "";
		// while_post counters
		String while_post_counter = "";

		for (Node itnode : this.nodes) {
			// Commit function call nodes
			if (itnode.getName().equals(this.name)) {
				itnode.setCommitted(true);
			}

			// Set Guard and Invariant for normal nodes and links
			if (itnode.getName().matches("r" + "\\d+.*") && !itnode.getCommitted()) {

				// Set Guard of normal links
				Vector<Link> links = itnode.getLinks();

				for (Link link : links) {
					String guard_org = link.getGuard() != null ? link.getGuard() : "";
					link.setGuard(guard_org.replaceAll("x&gt;=" + itnode.getName() + "_WCET", "") + "x&gt;="
							+ itnode.getName() + "_WCET");
				}

				// Set Invariant of normal nodes and special nodes
				itnode.setInvariant("x&lt;=" + itnode.getName() + "_WCET");
			}

			// Set links for special nodes (_cond _init _post)
			if (itnode.getName().matches("r" + "\\d+.*")) {

				// Set Update of links of _init block of iteration statements
				if (itnode.getName().contains("_init")) {

					Vector<Link> initlinks = itnode.getLinks();

					for (Link link : initlinks) {
						String[] iterator = itnode.getComments().replaceAll(" ", "").replaceAll("\\d+.*", "")
								.replaceAll(";", "").split("=");

						for (int cnt = 0; cnt < iterator.length; cnt++) {
							// Set Update of link for iteration nodes
							link.setUpdate(link.getUpdate() + ", " + iterator[cnt] + ":=0");

							// Collect local iterator variables
							// Remove repeated variables
							localIterators = localIterators.replaceAll(iterator[cnt] + ", ", "");
							localIterators = localIterators + iterator[cnt] + ", ";

						}
					}
				}

				// Set Update and Guard of links of _post block of iteration
				// statements

				if (itnode.getName().contains("_cond")) {

					Node conditionNode = new Node();
					conditionNode = itnode;

					String[] variable = conditionNode.getComments().replaceAll("while", "").replaceAll("for", "")
							.replaceAll("[)(,;\n\\s]+", "").split("[!><=&|]+");

					String[] operator_temp = conditionNode.getComments().replaceAll("while", "").replaceAll("for", "")
							.replaceAll("[)(,;\n\\s]+", "").split("[a-zA-Z_0-9]+");

					String[] nonOperator_temp = conditionNode.getComments().replaceAll("while", "")
							.replaceAll("for", "").replaceAll("[)(,;\n\\s]+", "").split("[!><=&|]+");

					Vector<String> operator = new Vector<String>();
					for (int cnt = 0; cnt < operator_temp.length; cnt++) {
						if (!operator_temp[cnt].isEmpty()) {
							operator.add(operator_temp[cnt]);
						}
					}

					Vector<String> nonOperator = new Vector<String>();
					for (int cnt = 0; cnt < nonOperator_temp.length; cnt++) {
						if (!nonOperator_temp[cnt].isEmpty()) {
							nonOperator.add(nonOperator_temp[cnt]);
						}
					}

					// Set the guard to be added to true link (for loop)
					String guardCondition = conditionNode.getComments().replace(" ", "").replace("for", "")
							.replace("while", "").replace("(", "").replace(")", "").replace(";", "")
							.replace("&&", "&amp;&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("==", "=")
							.replace(",", "");

					// While loop
					String guardConditionWhile = "";
					for (int cnt = 0; cnt < nonOperator.size() - 1; cnt++) {
						if (nonOperator.get(cnt).replaceAll("[0-9]", "").isEmpty()) {
							guardConditionWhile = guardConditionWhile + nonOperator.get(cnt) + operator.get(cnt);

						} else {
							guardConditionWhile = guardConditionWhile + nonOperator.get(cnt) + "_"
									+ conditionNode.getName() + operator.get(cnt);
						}

					}

					if (!nonOperator.isEmpty()) {
						if (nonOperator.get(nonOperator.size() - 1).replaceAll("[0-9]", "").isEmpty()) {
							guardConditionWhile = guardConditionWhile + nonOperator.get(nonOperator.size() - 1);

						} else {
							guardConditionWhile = guardConditionWhile + nonOperator.get(nonOperator.size() - 1) + "_"
									+ conditionNode.getName();
						}
					}
					// Replace operators
					guardConditionWhile = guardConditionWhile.replace("&&", "&amp;&amp;").replace("<", "&lt;")
							.replace(">", "&gt;").replace("==", "=").replace(",", "");

					// Set Guard of the false link of _cond node
					Vector<Link> condlinks = conditionNode.getLinks();

					// While loop _cond node link set for loop _cond node set

					if (itnode.getComments().startsWith("while")) {

						for (Link link : condlinks) {

							// Set Guard of link of false link of _cond node
							if (link.getGuard().contains("false")) {
								String guardCondition_temp = link.getGuard() != null ? link.getGuard() + "&amp;&amp; !("
										: "";
								link.setGuard(
										guardCondition_temp.replace("&amp;&amp; !(" + guardConditionWhile + ")", "")
												+ guardConditionWhile + ")");

							}
							// Set Guard of link of true link of _cond node
							if (link.getGuard().contains("true")) {
								String guardCondition_temp = link.getGuard() != null ? link.getGuard() + "&amp;&amp; ("
										: "";
								link.setGuard(
										guardCondition_temp.replace("&amp;&amp; (" + guardConditionWhile + ")", "")
												+ guardConditionWhile + ")");
							}

						}

					} else {
						for (Link link : condlinks) {
							// Set Guard of link of false link of _cond node
							if (link.getGuard().contains("false")) {
								String guardCondition_temp = link.getGuard() != null ? link.getGuard() + "&amp;&amp; !("
										: "";
								link.setGuard(guardCondition_temp.replace("&amp;&amp; !(" + guardCondition + ")", "")
										+ guardCondition + ")");

							}

							// Set Guard of link of true link of _cond node
							if (link.getGuard().contains("true")) {
								String guardCondition_temp = link.getGuard() != null ? link.getGuard() + "&amp;&amp; ("
										: "";
								link.setGuard(guardCondition_temp.replace("&amp;&amp; (" + guardCondition + ")", "")
										+ guardCondition + ")");
							}

						}
					}

					// Get the condition variables in while loop
					if (itnode.getComments().startsWith("while")) {
						for (int cnt = 0; cnt < variable.length; cnt++) {
							// Collect local iterator variables
							// Remove repeated variables
							if ((!variable[cnt].isEmpty()) && (!variable[cnt].replaceAll("[0-9]", "").isEmpty())) {
								localWhileConditionVariables = localWhileConditionVariables
										.replaceAll(variable[cnt] + "_" + itnode.getName() + ", ", "");
								localWhileConditionVariables = localWhileConditionVariables + variable[cnt] + "_"
										+ itnode.getName() + ", ";
							}
						}
					}
					condNodes.add(conditionNode);
				}

				// For loop
				if (itnode.getName().contains("_post")) {
					// Find Update for link of _post node
					String iteratorUpdate = itnode.getComments().replaceAll(" ", "").replaceAll("=", ":=")
							.replaceAll(";", "");

					// Set Update for iteration nodes
					Vector<Link> postlinks = itnode.getLinks();
					for (Link link : postlinks) {
						// Set Update of link of _post node
						link.setUpdate("x:=0, " + iteratorUpdate);
					}
				}

				// While loop
				if (itnode.getComments().matches("while_post")) {

					// Find all the elements need to write the update function
					// of _post node

					// Find all the counter for the update function
					String while_post_counter_temp = itnode.getName() + "_counter = 0, ";

					while_post_counter = while_post_counter + while_post_counter_temp;

					// Find the variables needed for the update function
					String whilecondNode_name = itnode.getName().replaceAll("_post", "_cond");

					for (Node conditionNode_post : this.nodes) {
						if (conditionNode_post.getName().equals(whilecondNode_name)) {

							String[] whilevariables_post_list = conditionNode_post.getComments().replaceAll("while", "")
									.replaceAll("[)(,;\n\\s]+", "").split("[!><=&|]+");

							for (int cnt = 0; cnt < whilevariables_post_list.length; cnt++) {

								if ((!whilevariables_post_list[cnt].isEmpty())
										&& (!whilevariables_post_list[cnt].replaceAll("[0-9]", "").isEmpty())) {
									String whilevariables_post_temp = conditionNode_post.getName() + "_"
											+ whilevariables_post_list[cnt] + "_" + itnode.getName()
											+ "_post_appendix_pin";
									whilevariables_post = whilevariables_post.replaceAll(whilevariables_post_temp, "")
											+ whilevariables_post_temp;
								}
							}
						}
					}

					// localWhileConditionVariables
					// Define Update for link of _post node
					String iteratorUpdate = itnode.getName() + "_update()";

					// Set Update for iteration nodes
					Vector<Link> postlinks = itnode.getLinks();
					for (Link link : postlinks) {
						// Set Update of link of _post node
						link.setUpdate("x:=0, " + iteratorUpdate);
					}
				}

				// Collect local WCET variables
				localVariablesWCET = localVariablesWCET + itnode.getName() + "_WCET=1, ";

			}

		}

		// Write local clock
		setLocalVariables(lclock + eol);
		// Write local WCET variables
		localVariablesWCET = "int " + localVariablesWCET.substring(0, localVariablesWCET.length() - 2) + ";";
		setLocalVariables(localVariablesWCET + eol);
		// Write local iterator variables
		if (!localIterators.isEmpty()) {
			localIterators = "int " + localIterators.substring(0, localIterators.length() - 2) + ";";
			setLocalVariables(localIterators + eol);
		}

		if (!localWhileConditionVariables.isEmpty()) {
			localWhileConditionVariables = "int "
					+ localWhileConditionVariables.substring(0, localWhileConditionVariables.length() - 2) + ";";
			setLocalVariables(localWhileConditionVariables + eol);
		}
		
		// Write Update function for while loop
		// Write while_post counter to Declaration
		if (!while_post_counter.isEmpty()) {
			setLocalVariables("int " + while_post_counter.substring(0, while_post_counter.length() - 2) + ";");
		}

		// Write while_post conditional variable values to Declaration

		/*
		 * String whilevariables_post_list_export = ""; if
		 * (!whilevariables_post.isEmpty()) {
		 * 
		 * String[] whilevariables_post_list =
		 * whilevariables_post.split("_post_appendix_pin"); for (int cnt = 0;
		 * cnt < whilevariables_post_list.length; cnt++) {
		 * whilevariables_post_list_export = whilevariables_post_list_export +
		 * eol + "const int " + whilevariables_post_list[cnt] +
		 * "_list[10] = {1,1,2,3,4,5,6,7,8,9};"; setLocalVariables( eol +
		 * "const int " + whilevariables_post_list[cnt] +
		 * "_list[10] = {1,1,2,3,4,5,6,7,8,9};"); } }
		 * 
		 * 
		 * 
		 * try{ PrintWriter writer = new
		 * PrintWriter("/Users/HLi/Documents/UA/Haoxuan/Benchmarks/TEST/" +
		 * this.name + ".txt", "UTF-8");
		 * writer.println(whilevariables_post_list_export); writer.close(); }
		 * catch (IOException e) { // do something }
		 */
		
		BufferedReader whilevariables_post_list_buffer = new BufferedReader(
				new FileReader("/Users/HLi/Documents/UA/Haoxuan/Benchmarks/TEST/" + this.name + ".txt"));
		StringBuilder whilevariables_post_list_write = new StringBuilder();
		try {		
			String line = whilevariables_post_list_buffer.readLine();

			while (line != null) {
				whilevariables_post_list_write.append(line);
				whilevariables_post_list_write.append("\n");
				line = whilevariables_post_list_buffer.readLine();
			}
			
		} finally {
			whilevariables_post_list_buffer.close();
		}
		setLocalVariables(whilevariables_post_list_write.toString());
		
		// Write _post_update() function

		for (Node updateFucntionNode : condNodes) {
			if (updateFucntionNode.getComments().startsWith("while")) {

				String name_post = updateFucntionNode.getName().replaceAll("_cond", "_post");
				String whileupdatefunctionHead = "void " + name_post + "_update()";
				String whileupdatefunctionBody = "";
				String whileupdatefunctionTail = "	" + name_post + "_counter++;";
				String[] whileupdateVariables = updateFucntionNode.getComments().replaceAll("while", "")
						.replaceAll("[)(,;\n\\s]+", "").split("[!><=&|]+");
				String whileupdateVariables_temp = "";

				for (int cnt = 0; cnt < whileupdateVariables.length; cnt++) {

					if ((!whileupdateVariables[cnt].isEmpty())
							&& (!whileupdateVariables[cnt].replaceAll("[0-9]", "").isEmpty())) {
						whileupdateVariables_temp = whileupdateVariables_temp
								.replaceAll(whileupdateVariables[cnt] + "_whileupdateVariables_temp_appendix_pin", "")
								+ whileupdateVariables[cnt] + "_whileupdateVariables_temp_appendix_pin";
					}
				}

				String[] whileupdateVariable_list = whileupdateVariables_temp
						.split("_whileupdateVariables_temp_appendix_pin", -1);

				for (int cnt = 0; cnt < whileupdateVariable_list.length; cnt++) {
					if (!whileupdateVariable_list[cnt].matches("")) {
						String whileupdatefunctionBody_temp = eol + "	" + whileupdateVariable_list[cnt] + "_"
								+ updateFucntionNode.getName() + " = " + updateFucntionNode.getName() + "_"
								+ whileupdateVariable_list[cnt] + "_" + name_post + "_list[" + name_post + "_counter"
								+ "];";
						whileupdatefunctionBody = whileupdatefunctionBody.replaceAll(whileupdatefunctionBody_temp, "")
								+ whileupdatefunctionBody_temp;
					}
				}

				String whileupdatefunction = eol + whileupdatefunctionHead + eol + "{" + whileupdatefunctionBody + eol
						+ whileupdatefunctionTail + eol + "}";
				setLocalVariables(whileupdatefunction);

			}

		}

		// Remove all false and true Guard in links
		for (Link link : this.links) {
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

		// Adjust layout
		this.prettifyLayout();

		return 0;
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

		if (node.getComments().split("();").length == node.getComments().split("\n").length
				&& node.getComments().contains("();")) {
			String[] Chan = node.getComments().replaceAll("\n", "").replaceAll(" ", "").split("\\(" + "\\)" + ";");

			for (int cnt = 0; cnt < Chan.length; cnt++) {
				Node functionCallStartNode = new Node();
				functionCallStartNode.setId(getNextFreeId());
				functionCallStartNode.setName(Chan[cnt] + "_r" + (startRowNumber + cnt));
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

			if (!iterationBlock.isDoWhileStatement()) {
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

			if (iterationBlock.isDoWhileStatement()) {
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

					if (iterationBlock.isDoWhileStatement()) {
						// Set link guard (True statement)
						link.setGuard("true");
					}

					this.links.add(link);
				}
			}

			// Create iteration exit links
			// Get guard condition false
			if (!iterationBlock.isDoWhileStatement()) {
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
						if (((CaseBlock) caseBlock).getIfSelectionValue()) {
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
