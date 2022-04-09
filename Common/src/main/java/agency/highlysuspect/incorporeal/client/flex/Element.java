package agency.highlysuspect.incorporeal.client.flex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class Element {
	public Element(String name) {
		this.name = name;
	}
	
	//for debugging mainly lol
	public final String name;
	
	public int relativeX, relativeY; //position (set from parent)
	public int width, height; //size (set by self)
	
	//properties relevant to being stuck into a FlexElement (Wow! It's Janky)
	public int flexGrow;
	public @Nullable Alignment alignSelf = null;
	
	/**
	 * Recursively visit child elements
	 */
	public void visitChildren(Consumer<Element> visitor) {
		//no-op by default
	}
	
	/**
	 * Walk the tree of child elements.
	 * On the way up, set each child's position relative to myself, and set my own width and height.
	 * 
	 * Elements do not directly set the width and height of their own children, but that behavior
	 * can be requested by refining BoxConstraints on the way down the tree traversal.
	 * 
	 * Elements never know their absolute position, and do not set their own relative position.
	 */
	public abstract void solve(Constraints constraints);
	
	public void drawRecursively(PoseStack pose, float partialTicks) {
		//Draw myself before drawing children, because backgrounds are usually parents of things-that-go-on-the-background
		draw(pose, partialTicks);
		visitChildren(child -> {
			pose.translate(child.relativeX, child.relativeY, 0);
			child.drawRecursively(pose, partialTicks);
			pose.translate(-child.relativeX, -child.relativeY, 0);
		});
	}
	
	public void draw(PoseStack pose, float partialTicks) {
		
	}
	
	public void debugDrawRecursively(PoseStack pose, float partialTicks, int wow) {
		int realColor = (0x00FFFFFF & Mth.hsvToRgb(wow / 30f, 1f, 0.5f)) | 0x77000000;
		//blah blah blah
	}
	
	//println debug it lolol
	public void dump() {
		dump(0);
	}
	
	public void dump(int depth) {
		System.out.printf("%s%s - x: %s, y: %s, width: %s, height: %s%n", " ".repeat(depth), name, relativeX, relativeY, width, height);
		visitChildren(child -> child.dump(depth + 1));
	}
}
