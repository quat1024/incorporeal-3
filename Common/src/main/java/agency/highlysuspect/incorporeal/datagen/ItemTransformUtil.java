package agency.highlysuspect.incorporeal.datagen;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;

import java.util.Locale;

public class ItemTransformUtil {
	//Manually copied from minecraft:block/block i think
	private static final ItemTransform thirdperson_righthand = new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 2.5f, 0), new Vector3f(0.375f, 0.375f, 0.375f));
	private static final ItemTransform firstperson_lefthand = new ItemTransform(new Vector3f(0, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.4f, 0.4f, 0.4f));
	private static final ItemTransform firstperson_righthand = new ItemTransform(new Vector3f(0, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.4f, 0.4f, 0.4f));
	private static final ItemTransform head = ItemTransform.NO_TRANSFORM;
	private static final ItemTransform gui = new ItemTransform(new Vector3f(30, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.625f, 0.625f, 0.625f));
	private static final ItemTransform ground = new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 3, 0), new Vector3f(0.25f, 0.25f, 0.25f));
	private static final ItemTransform fixed = new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f));
	
	public static final ItemTransforms BLOCK_BLOCK = new ItemTransforms(
		thirdperson_righthand, //not a typo
		thirdperson_righthand,
		firstperson_lefthand,
		firstperson_righthand,
		head,
		gui,
		ground,
		fixed
	);
	
	//Apply a scaling transformation to all members of an item transforms.
	public static ItemTransforms scaleItemTransforms(ItemTransforms input, float scaleFactor) {
		return new ItemTransforms(
			scaleItemTransform(input.thirdPersonLeftHand, scaleFactor),
			scaleItemTransform(input.thirdPersonRightHand, scaleFactor),
			scaleItemTransform(input.firstPersonLeftHand, scaleFactor),
			scaleItemTransform(input.firstPersonRightHand, scaleFactor),
			scaleItemTransform(input.head, scaleFactor),
			scaleItemTransform(input.gui, scaleFactor),
			scaleItemTransform(input.ground, scaleFactor),
			scaleItemTransform(input.fixed, scaleFactor)
		);
	}
	
	//Apply a scaling transformation to one member of an item transform.
	public static ItemTransform scaleItemTransform(ItemTransform input, float scaleFactor) {
		//hardcoding some shit i need for taters lol, magic numbers, do not eat
		Vector3f translation = input.translation.copy();
		translation.add(0, (scaleFactor - 1) * 3.2f, 0);
		
		Vector3f scale = input.scale.copy();
		scale.mul(scaleFactor);
		return new ItemTransform(input.rotation, translation, scale);
	}
	
	public static JsonObject toJson(ItemTransforms ts) {
		JsonObject json = new JsonObject();
		
		json.add("thirdperson_righthand", toJson(ts.getTransform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)));
		json.add("thirdperson_lefthand", toJson(ts.getTransform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)));
		json.add("firstperson_righthand", toJson(ts.getTransform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)));
		json.add("firstperson_righthand", toJson(ts.getTransform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND)));
		json.add("head", toJson(ts.getTransform(ItemTransforms.TransformType.HEAD)));
		json.add("gui", toJson(ts.getTransform(ItemTransforms.TransformType.GUI)));
		json.add("ground", toJson(ts.getTransform(ItemTransforms.TransformType.GROUND)));
		json.add("fixed", toJson(ts.getTransform(ItemTransforms.TransformType.FIXED)));
		
		return json;
	}
	
	public static JsonObject toJson(ItemTransform t) {
		JsonObject json = new JsonObject();
		json.add("rotation", writeVector3f(t.rotation));
		json.add("translation", writeVector3f(t.translation));
		json.add("scale", writeVector3f(t.scale));
		return json;
	}
	
	public static JsonArray writeVector3f(Vector3f yes) {
		JsonArray wow = new JsonArray();
		wow.add(yes.x());
		wow.add(yes.y());
		wow.add(yes.z());
		return wow;
	}
}
