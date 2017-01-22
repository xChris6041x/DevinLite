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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	private String label;
	
	private CommandTree parent;
	private List<CommandTree> children;
	
	private CommandExecutor executor;
	
	
	public CommandTree(String label, CommandExecutor executor) {
		this.label = label;
		this.executor = executor;
		
		this.children = new ArrayList<CommandTree>();
	}
	public CommandTree(String label) {
		this(label, null);
	}
	
	
	/**
	 * @return the label of this command.
	 */
	public String getLabel() { 
		return label; 
	}
	
	/**
	 * Set the label of this command tree.
	 * @param label - The new label.
	 * @return
	 */
	public void setLabel(String label) {
		this.label = label; 
	}
	
	/**
	 * @param label
	 * @return whether the {@code label} is a valid label for this command tree.
	 */
	public boolean isValidLabel(String label) {
		return label.equalsIgnoreCase(this.label);
	}
	
	/**
	 * @return the command executor that will run when this is executed.
	 */
	public CommandExecutor getExecutor() {
		return executor;
	}
	
	/**
	 * Set the command executor that will run when this is executed.
	 * @param executor - The new command executor.
	 */
	public void setExecutor(CommandExecutor executor) {
		this.executor = executor;
	}
	
	
	/**
	 * @return the parent of this CommandTree.
	 */
	public CommandTree getParent() {
		return parent;
	}
	
	/**
	 * Set the parent of this command tree and fix the old parent so it
	 * no longer contains this as a child.
	 * 
	 * @param parent - The new parent.
	 */
	public void setParent(CommandTree parent) {
		if(this.parent != null) {
			this.parent.children.remove(this);
		}
		this.parent = parent;
		
		parent.children.add(this);
	}
	
	
	/**
	 * @param label
	 * @return a child with a specific {@code label}.
	 */
	public CommandTree getChild(String label) {
		return getChild(label);
	}
	
	/**
	 * Note: This will consume the list, so it is not meant to be used outside the
	 * getChild(Labels...) command.
	 * 
	 * @param labels
	 * @return a child that follows the structure label[0] label[1] label[2]... label[n].
	 */
	private CommandTree getChild(List<String> labels) {
		for(CommandTree child : children) {
			if(child.isValidLabel(labels.get(0))) {
				if(labels.size() == 1) {
					return child;
				}
				else {
					labels.remove(0);
					return child.getChild(labels);
				}
			}
		}
		
		throw new IllegalArgumentException("There is no child inside " + label + " labeled " + labels.get(0));
	}
	
	/**
	 * @param labels
	 * @return a child that follows the structure label[0] label[1] label[2]... label[n].
	 */
	public CommandTree getChild(String... labels) {
		return getChild(Arrays.asList(labels));
	}
	
	
	// TODO: Finish executor!
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!isValidLabel(label)) return false;
		if(args.length > 0) {
			CommandTree child = this.getChild(args[0]);
			if(child == null) {
				return (executor == null) ? false : executor.onCommand(sender, cmd, label, args);
			}
			else {
				String[] newArgs = new String[args.length - 1];
				for(int i = 1; i < args.length; i++) {
					newArgs[i - 1] = args[i];
				}
				
				return child.onCommand(sender, cmd, args[0], newArgs);
			}
		}
		else {
			return (executor == null) ? false : executor.onCommand(sender, cmd, label, args);
		}
	}
	
}
