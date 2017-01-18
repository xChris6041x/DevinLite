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
import java.util.List;

import org.bukkit.command.CommandExecutor;

/**
 * A class that handles the command and the sub commands of that command,
 * then hands the proper information to the CommandExecutor.
 * 
 * @version 0.1.0
 * @author Christopher Bishop (xChris6041x)
 */
public class CommandTree {

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
		for(CommandTree child : children) {
			if(child.label.equalsIgnoreCase(label)) return child;
		}
		return null;
	}
	
}
