/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to <http://unlicense.org>
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * A class that handles the command and the sub commands of that command,
 * then hands the proper information to the CommandExecutor.
 * 
 * @version 0.1.0
 * @author Christopher Bishop (xChris6041x)
 */
public class CommandTree implements CommandExecutor {
	
	private CommandExecutor executor;
	private Map<String[], CommandTree> children;
	
	
	public CommandTree(CommandExecutor executor) {
		this.executor = executor;
		this.children = new HashMap<String[], CommandTree>();
	}
	public CommandTree() {
		this(null);
	}
	
	
	/**
	 * @since 0.1.0
	 * @return the {@code CommandExecutor} that will be executed.
	 */
	public CommandExecutor getExecutor() {
		return executor;
	}
	/**
	 * Set the current executor to {@code executor}.
	 * 
	 * @since 0.1.0
	 * @param executor - The executor that is being used to set.
	 */
	public void setExecutor(CommandExecutor executor) {
		this.executor = executor;
	}
	
	
	/**
	 * @since 0.1.0
	 * @param label - The label to search for.
	 * @param deepSearch - Whether to search through all the children's children to find that label.
	 * @return the {@code CommandTree} child that was found or null if nothing was found.
	 */
	public CommandTree getChild(String label, boolean deepSearch) {
		for(Entry<String[], CommandTree> child : children.entrySet()) {
			for(String lbl : child.getKey()) {
				if(lbl.equalsIgnoreCase(label)) {
					return child.getValue();
				}
				else if(deepSearch) {
					CommandTree commandChild = child.getValue().getChild(label, deepSearch);
					if(commandChild != null) return commandChild;
				}
			}
		}
		return null;
	}
	
	/**
	 * @since 0.1.0
	 * @param labels - The labels that match the child labels in order.
	 * @return the {@code CommandTree} child that matches all the {@code labels}, or null if not matching.
	 */
	public CommandTree getChild(String... labels) {
		if(labels.length == 0) return null;
		for(Entry<String[], CommandTree> child : children.entrySet()) {
			for(String lbl : child.getKey()) {
				if(lbl.equalsIgnoreCase(labels[0])) {
					if(labels.length == 1) {
						return child.getValue();
					}
					else {
						return child.getValue().getChild(CommandTree.removeFirst(labels));
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * @since 0.1.0
	 * @param structure - The structure that the children will follow separated by spaces. Ex: "root sub1 sub2"
	 * @return the {@code CommandTree} child that follows the {@code structure}
	 */
	public CommandTree getChild(String structure) {
		return getChild(structure.split(" "));
	}
	
	/**
	 * Add a child to this {@code CommandTree}.
	 * 
	 * @since 0.1.0
	 * @param executor - The executor the last child will use.
	 * @param labels - All the labels and aliases for each child.
	 * @throws IllegalArgumentException if there are no labels available or there's a duplicate command.
	 */
	public void add(CommandExecutor executor, String[]... labels) throws IllegalArgumentException {
		if(labels.length == 0 || labels[0].length == 0) throw new IllegalArgumentException("Cannot add an executor to a child with no labels");
		
		// Find an existing child.
		CommandTree child = null;
		String lbl = "";
		for(String label : labels[0]) {
			CommandTree commandChild = getChild(label);
			if(commandChild != null) {
				child = commandChild;
				lbl = label;
				break;
			}
		}
		
		// Add the executor to the child or go deeper.
		if(child == null) child = new CommandTree();
		if(labels.length == 1) {
			if(child.getExecutor() == null) {
				child.setExecutor(executor);
			}
			else {
				throw new IllegalArgumentException("Cannot have two commands with the same label \"" + lbl + "\".");
			}
		}
		else {
			child.add(executor, CommandTree.removeFirst(labels));
		}
	}
	
	/**
	 * Add a child to this {@code CommandTree}.
	 * 
	 * @since 0.1.0
	 * @param executor - The executor the last child will use.
	 * @param structure - The structure of the children. It should be in <a href="https://github.com/xChris6041x/DevinLite/tree/master">this format</a>.
	 * @throws IllegalArgumentException if there are no labels available or there's a duplicate command.
	 */
	public void add(CommandExecutor executor, String structure) throws IllegalArgumentException {
		String[] splits = structure.split(" ");
		String[][] labels = new String[splits.length][];
		for(int i = 0; i < splits.length; i++) {
			labels[i] = splits[i].split("|");
		}
		
		add(executor, labels);
	}
	
	/* The Command */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 0) {
			CommandTree child = this.getChild(args[0]);
			if(child == null) {
				return (executor == null) ? false : executor.onCommand(sender, cmd, label, args);
			}
			else {
				String lbl = args[0];
				String[] newArgs = CommandTree.removeFirst(args);
				
				return child.onCommand(sender, cmd, lbl, newArgs);
			}
		}
		else {
			return (executor == null) ? false : executor.onCommand(sender, cmd, label, args);
		}
	}
	
	
	/**
	 * @since 0.1.0
	 * @param arr - The array which needs the first element removed.
	 * @return An array which is a duplicate of {@code arr} except the first element is removed.
	 */
	public static String[] removeFirst(String[] arr) {
		String[] newArr = new String[arr.length - 1];
		for(int i = 1; i < arr.length; i++) {
			newArr[i - 1] = arr[i];
		}
		
		return newArr;
	}
	
	/**
	 * @since 0.1.0
	 * @param arr - The array of arrays which needs the first element removed.
	 * @return An array which is a duplicate of {@code arr} except the first element is removed.
	 */
	public static String[][] removeFirst(String[][] arr) {
		String[][] newArr = new String[arr.length - 1][];
		for(int i = 1; i < arr.length; i++) {
			newArr[i - 1] = arr[i];
		}
		
		return newArr;
	}
	
}
