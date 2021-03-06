package vazkii.ambience.Util.Handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.ambience.Ambience;
import vazkii.ambience.Reference;
import vazkii.ambience.SongPicker;

public class SoundHandler {
//public class SoundHandler extends MovingSound {

	//public static SoundEvent secret;

	public static final List<String> SOUNDS = new ArrayList<String>();

	public SoundHandler() {
		// super(SoundEvents.ENTITY_MINECART_RIDING, SoundCategory.NEUTRAL);
	}

	public static void registerSounds() {
		

		/*
		 * ResourceLocation location = new ResourceLocation(Reference.MOD_ID, "secret");
		 * SoundEvent soundEvent = new SoundEvent(location);
		 * soundEvent.setRegistryName("secret");
		 * ForgeRegistries.SOUND_EVENTS.register(soundEvent);
		 */

		// TESTE
		// SOUNDS.add(soundEvent);

		/*try {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceLocation.class.getClassLoader().getResourceAsStream("assets/ambience/sounds.json"),"UTF-8"));
		JsonParser parser = new JsonParser();
		JsonElement obj = parser.parse(reader);

		if (obj != null) {
			String[] arrOfStr = obj.toString().split("\"name\":\"");
			List<String> result = new ArrayList<String>();

			for (int i = 1; i < arrOfStr.length; i++) {
				String SoundName = arrOfStr[i].split("\",\"")[0].substring(9);

				ResourceLocation location = new ResourceLocation(Reference.MOD_ID, SoundName);
				SoundEvent soundEvent = new SoundEvent(location);
				soundEvent.setRegistryName(SoundName);
				ForgeRegistries.SOUND_EVENTS.register(soundEvent);
				
				SOUNDS.add(SoundName);
			}

			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
			try {	            
				List<String> result = new ArrayList<String>();

				File config = new File(Ambience.ambienceDir, "ambience.properties");

				Properties props = new Properties();
				props.load(new FileReader(config));

				String[] songs = props.getProperty("speaker").split(",");
				for (int i = 0; i < songs.length; i++) {
					result.add(songs[i]);
				}

				for (int i = 0; i < result.size(); i++) {

					ResourceLocation location = new ResourceLocation(Reference.MOD_ID, result.get(i));
					SoundEvent soundEvent = new SoundEvent(location);
					soundEvent.setRegistryName(result.get(i));
					ForgeRegistries.SOUND_EVENTS.register(soundEvent);

					SOUNDS.add(result.get(i));
				}	            
	            
	        } catch (IOException e) {
	            System.out.println("###### Error: "+e.getMessage());
	            e.printStackTrace();
	        }			
	}
}
