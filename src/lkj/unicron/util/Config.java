package lkj.unicron.util;

import lkj.unicron.cura.Profile;

import org.join.ogles.lib.GLColor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Config {

	
	public static float DEFAULT_RED = 0.29f;
	public static float DEFAULT_GREEN = 0.41f;
	public static float DEFAULT_BLUE = 1.0f;
	public static float DEFAULT_ALPHA = 0.77f;
	
	private Context mContext;
	public Config(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public GLColor getColor(){
		GLColor c = new GLColor();
		SharedPreferences config = mContext.getSharedPreferences("other_configs", Activity.MODE_PRIVATE);

		c.r = (int) (config.getFloat("red", DEFAULT_RED)  * 65535);
		c.g  =  (int) (config.getFloat("green", DEFAULT_GREEN) * 65535);
		c.b  =  (int) (config.getFloat("blue", DEFAULT_BLUE) * 65535);
		c.a =  (int) (config.getFloat("alpha", DEFAULT_ALPHA) * 65535);
	//	Log.d("getColor =" + c);
		return c;		
	}
	
	public static boolean getIsQuickSetting(Context context){
		SharedPreferences config = context.getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
		return config.getBoolean("isQuickSetting", true);		
	}
	
	public static void setIsQuickSetting(Context context, boolean val){
		SharedPreferences config = context.getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
		Editor editor = config.edit();
		editor.putBoolean("isQuickSetting", val);
		editor.commit();	
	}
	
	public static String getQuickSetting(Context context){
		SharedPreferences config = context.getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
		return config.getString("QuickSetting", "Hight quality print");		
	}
	
	public static void setQuickSetting(Context context, String val){
		SharedPreferences config = context.getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
		Editor editor = config.edit();
		editor.putString("QuickSetting", val);
		Log.d("setQuickSetting val =" + val);
		editor.commit();	
	}
	
	public static String getMaterialSetting(Context context){
		SharedPreferences config = context.getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
		return config.getString("MaterialSetting", "ABS");		
	}
	
	public static void setMaterialSetting(Context context, String val){
		SharedPreferences config = context.getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
		Editor editor = config.edit();
		editor.putString("MaterialSetting", val);
		//Log.d("setMaterialSetting val =" + val);
		editor.commit();	
	}
	
	public static Boolean getLineBoxVisble(Context context){
		SharedPreferences config = context.getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
		boolean displayBox = config.getBoolean("displayBox", false);
		//	Log.d("displayBox =" + displayBox);
		return displayBox;
	}
	
	public static void setLineBoxVisble(Context context, boolean val){
		SharedPreferences config = context.getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
		Editor editor = config.edit();
		editor.putBoolean("displayBox", val);
		editor.commit();	
	}
    
	// save advance print option
	/*  float nozzle_size =  Float.parseFloat( (String) mProfile.getItem("nozzle_size"));  
	 *  mProfile.setItem("wall_thickness", Float.toString(nozzle_size * 2.0f));
		mProfile.setItem("layer_height",Float.toString(0.06f));
		mProfile.setItem("fill_density", Float.toString(20));
		mProfile.setItem("print_speed", Float.toString(50));
		mProfile.setItem("cool_min_layer_time", Float.toString(5));
		mProfile.setItem("bottom_layer_speed", Float.toString(15));
	*/
	public static void save_advance_print_option_other(Context context, String str, String val){
		SharedPreferences config = context.getSharedPreferences("advance_print_other_config", Activity.MODE_PRIVATE);
		Editor editor = config.edit();
		editor.putString(str, val);
		editor.commit();
	}
	
	public static String get_advance_print_option_other(Context context, String str){
		SharedPreferences config = context.getSharedPreferences("advance_print_other_config", Activity.MODE_PRIVATE);
		return config.getString(str,"null");
	}
	
	public static void save_advance_print_option_other_first(Context context){
		SharedPreferences config = context.getSharedPreferences("advance_print_other_config", Activity.MODE_PRIVATE);
		
		if(!config.getString("wall_thickness", "not").equals("not")){
			return;
		}
		
		Editor editor = config.edit();

		editor.putString("wall_thickness", "0.8");
		editor.putString("layer_height", "0.1");
		editor.putString("fill_density", "20.0");
		editor.putString("print_speed", "50.0");
		editor.putString("cool_min_layer_time", "5.0");
		editor.putString("bottom_layer_speed", "20.0");
		editor.commit();	
	}
	
	//only advance option
	public static void save_full_config(Context context, Profile p){
		SharedPreferences config = context.getSharedPreferences("full_config", Activity.MODE_PRIVATE);
		Editor editor = config.edit();
		for(Profile.ItemConfig item : p.configMap.values()){
			editor.putString(item.getName(), item.getValue().toString());
			Log.d("item.getValue().toString()=" + item.getValue().toString() + ", item.getName()=" +  item.getName());
		}
		editor.commit();
	}
	
	//"full_config", full_config_backup
	public static Profile get_full_config(Context context, String preference_name){
		Profile p = null;
		SharedPreferences config = context.getSharedPreferences(preference_name, Activity.MODE_PRIVATE);
		for(String key : Profile.ItemName){
			String val = config.getString(key, "0");
			Log.d("get_full_config key=" + key + ", val=" +  val);
		}
		return p;
	}
	
	public static void save_basic_config_backup_first(Context context){
		SharedPreferences config = context.getSharedPreferences("full_config_backup", Activity.MODE_PRIVATE);
		
		if(!config.getString("print_temperature2", "not").equals("not")){
			return;
		}
		
		Editor editor = config.edit();

		editor.putString("print_temperature2", "0");
		editor.putString("print_temperature3", "0");
		editor.putString("print_temperature4", "0");
		editor.putString("fan_speed_max", "100");
		editor.putString("nozzle_size", "0.4");
		editor.putString("support_z_distance", "0.15");
		editor.putString("object_center_y", "-1");
		editor.putString("skirt_minimal_length", "150");
		editor.putString("overlap_dual", "0.15");
		editor.putString("cool_min_layer_time", "5");
		
		editor.putString("support_dual_extrusion", "Both");
		editor.putString("skirt_line_count", "1");
		editor.putString("retraction_amount", "4.5");
		editor.putString("travel_speed", "150");
		editor.putString("brim_line_count", "20");
		editor.putString("raft_line_spacing", "1.0");
		editor.putString("fan_full_height", "5.0");
		editor.putString("cool_head_lift", "0");
		editor.putString("raft_interface_thickness", "0.2");
		editor.putString("retraction_minimal_extrusion", "0.02");
		
		editor.putString("wall_thickness", "0.8");
		editor.putString("print_temperature", "220");
		editor.putString("skirt_gap", "3.0");
		editor.putString("support", "None");
		editor.putString("support_type", "Grid");
		editor.putString("fan_speed", "100");
		editor.putString("support_xy_distance", "0.7");
		editor.putString("inset0_speed", "0.0");
		editor.putString("raft_margin", "5");
		editor.putString("retraction_speed", "40.0");
		
		editor.putString("platform_adhesion", "None");
		editor.putString("bottom_layer_speed", "20");
		editor.putString("wipe_tower", "0");
		editor.putString("raft_base_linewidth", "0.7");
		editor.putString("support_angle", "60");
		editor.putString("solid_bottom", "1");
		editor.putString("raft_interface_linewidth", "0.2");
		editor.putString("fix_horrible_extensive_stitching", "0");
		editor.putString("retraction_combing", "1");
		editor.putString("layer_height", "0.1");
		editor.putString("ooze_shield", "0");
		
		editor.putString("object_center_x", "-1");
		editor.putString("bottom_thickness", "0.3");
		editor.putString("raft_base_thickness", "0.3");
		editor.putString("fill_density", "20");
		editor.putString("fix_horrible_union_all_type_a", "1");
		editor.putString("filament_diameter", "2.85");
		editor.putString("print_speed", "50");
		editor.putString("fill_overlap", "15");
		editor.putString("infill_speed", "0.0");
		editor.putString("support_fill_rate", "15");
		
		editor.putString("retraction_min_travel", "1.5");
		editor.putString("wipe_tower_volume", "15");
		editor.putString("solid_top", "1");
		editor.putString("retraction_dual_amount", "16.5");
		editor.putString("spiralize", "0");
		editor.putString("print_bed_temperature", "100");
		editor.putString("filament_flow", "100.0");
		editor.putString("fan_enabled", "1");
		editor.putString("retraction_enable", "1");
		editor.putString("insetx_speed", "0.0");
		
		editor.putString("fix_horrible_union_all_type_b", "0");
		editor.putString("object_sink", "0.0");
		editor.putString("solid_layer_thickness", "0.6");
		editor.putString("cool_min_feedrate", "10");
		editor.putString("retraction_hop", "0.0");
		editor.putString("filament_diameter2", "0");
		editor.putString("filament_diameter3", "0");
		editor.putString("plugin_config", "0");
		editor.putString("filament_diameter4", "0");
		editor.putString("fix_horrible_use_open_bits", "0");
		
		editor.commit();
	}
	
	public static void save_basic_config_first(Context context){
		SharedPreferences config = context.getSharedPreferences("full_config", Activity.MODE_PRIVATE);
		
		if(!config.getString("print_temperature2", "not").equals("not")){
			return;
		}
		
		Editor editor = config.edit();

		editor.putString("print_temperature2", "0");
		editor.putString("print_temperature3", "0");
		editor.putString("print_temperature4", "0");
		editor.putString("fan_speed_max", "100");
		editor.putString("nozzle_size", "0.4");
		editor.putString("support_z_distance", "0.15");
		editor.putString("object_center_y", "-1");
		editor.putString("skirt_minimal_length", "150");
		editor.putString("overlap_dual", "0.15");
		editor.putString("cool_min_layer_time", "5");
		
		editor.putString("support_dual_extrusion", "Both");
		editor.putString("skirt_line_count", "1");
		editor.putString("retraction_amount", "4.5");
		editor.putString("travel_speed", "150");
		editor.putString("brim_line_count", "20");
		editor.putString("raft_line_spacing", "1.0");
		editor.putString("fan_full_height", "5.0");
		editor.putString("cool_head_lift", "0");
		editor.putString("raft_interface_thickness", "0.2");
		editor.putString("retraction_minimal_extrusion", "0.02");
		
		editor.putString("wall_thickness", "0.8");
		editor.putString("print_temperature", "220");
		editor.putString("skirt_gap", "3.0");
		editor.putString("support", "None");
		editor.putString("support_type", "Grid");
		editor.putString("fan_speed", "100");
		editor.putString("support_xy_distance", "0.7");
		editor.putString("inset0_speed", "0.0");
		editor.putString("raft_margin", "5");
		editor.putString("retraction_speed", "40.0");
		
		editor.putString("platform_adhesion", "None");
		editor.putString("bottom_layer_speed", "20");
		editor.putString("wipe_tower", "0");
		editor.putString("raft_base_linewidth", "0.7");
		editor.putString("support_angle", "60");
		editor.putString("solid_bottom", "1");
		editor.putString("raft_interface_linewidth", "0.2");
		editor.putString("fix_horrible_extensive_stitching", "0");
		editor.putString("retraction_combing", "1");
		editor.putString("layer_height", "0.1");
		editor.putString("ooze_shield", "0");
		
		editor.putString("object_center_x", "-1");
		editor.putString("bottom_thickness", "0.3");
		editor.putString("raft_base_thickness", "0.3");
		editor.putString("fill_density", "20");
		editor.putString("fix_horrible_union_all_type_a", "1");
		editor.putString("filament_diameter", "2.85");
		editor.putString("print_speed", "50");
		editor.putString("fill_overlap", "15");
		editor.putString("infill_speed", "0.0");
		editor.putString("support_fill_rate", "15");
		
		editor.putString("retraction_min_travel", "1.5");
		editor.putString("wipe_tower_volume", "15");
		editor.putString("solid_top", "1");
		editor.putString("retraction_dual_amount", "16.5");
		editor.putString("spiralize", "0");
		editor.putString("print_bed_temperature", "100");
		editor.putString("filament_flow", "100.0");
		editor.putString("fan_enabled", "1");
		editor.putString("retraction_enable", "1");
		editor.putString("insetx_speed", "0.0");
		
		editor.putString("fix_horrible_union_all_type_b", "0");
		editor.putString("object_sink", "0.0");
		editor.putString("solid_layer_thickness", "0.6");
		editor.putString("cool_min_feedrate", "10");
		editor.putString("retraction_hop", "0.0");
		editor.putString("filament_diameter2", "0");
		editor.putString("filament_diameter3", "0");
		editor.putString("plugin_config", "0");
		editor.putString("filament_diameter4", "0");
		editor.putString("fix_horrible_use_open_bits", "0");
		
		editor.commit();
	}
	

	
	public static void save_machine_config_first(Context context){
		SharedPreferences config = context.getSharedPreferences("machine_config", Activity.MODE_PRIVATE);
		
		if(!config.getString("nozzle_size", "not").equals("not")){
			return;
		}
		
		Editor editor = config.edit();
		//  ~/.cura/14.03/preferences.ini
		editor.putString("nozzle_size", "0.4");
		editor.putString("machine_name", "trube_lkj_machine");
		editor.putString("machine_type", "ultimaker2");//Unicorn
		editor.putString("machine_width", "200");
		editor.putString("machine_depth", "200");
		editor.putString("machine_height", "200");
		editor.putString("machine_center_is_zero", "0");//?????????
		editor.putString("machine_shape", "Square"); //'Square','Circular'
		editor.putString("ultimaker_extruder_upgrade", "0");
		editor.putString("has_heated_bed", "1");
		editor.putString("gcode_flavor", "UltiGCode"); //'RepRap (Marlin/Sprinter)', 'RepRap (Volumetric)', 'UltiGCode', 'MakerBot', 'BFB', 'Mach3'
		editor.putString("extruder_amount", "1");//['1','2','3','4'
		editor.putString("extruder_offset_x1", "18.0");
		editor.putString("extruder_offset_y1", "0.0");
		editor.putString("extruder_offset_x2", "0.0");
		editor.putString("extruder_offset_y2", "0.0");
		editor.putString("extruder_offset_x3", "0.0");
		editor.putString("extruder_offset_y3", "0.0");
		editor.putString("steps_per_e", "0");
		editor.putString("serial_port", "AUTO");
		editor.putString("serial_port_auto", "0");
		editor.putString("serial_baud", "AUTO");
		editor.putString("serial_baud_auto", "0");
		editor.putString("extruder_head_size_min_x", "40.0");
		editor.putString("extruder_head_size_min_y", "10.0");
		editor.putString("extruder_head_size_max_x", "60.0");
		editor.putString("extruder_head_size_max_y", "30.0");
		editor.putString("extruder_head_size_height", "55.0");
		
		editor.commit();
	}
	public   static String indexToSupportType(int qualityIndex) {
	        switch (qualityIndex) {
	            case 0:  
	            	return "NONE";
	            case 1: 
	            	return "Touching buildplate";
	            case 2:
	            	return "Everywhere";
	            default:
	            	return "NONE";  
	        }   
	    } 
	    
	   public   static String indexToAdhesionType(int qualityIndex) {
	        switch (qualityIndex) {
	            case 0:  
	            	return "NONE";
	            case 1: 
	            	return "Brim";
	            case 2:
	            	return "Raft";
	            default:
	            	return "NONE";  
	        }   
	    } 
	   public   static String indexToPrintType(int qualityIndex) {
	        switch (qualityIndex) {
	            case 0:  
	            	return "Hight quality print";
	            case 1: 
	            	return "Normal quality print";
	            case 2:
	            	return "Fast low quality print";
	            default:
	            	return "NONE";  
	        }   
	    } 
	    public    static int TypeToSupportindex(String type) {
	    	Log.d("TypeToSupportindex type =" + type);
	    	if(type.equals("None") || type.equals("Hight quality print")){ 
	    		return 0;
	    	} else if(type.equals("Brim") || type.equals("Touching buildplate") || type.equals("Normal quality print")){
	    		return 1;
	    	} else if(type.equals("Raft") || type.equals("Everywhere") || type.equals("Fast low quality print")){
	    		return 2;
	    	}
	        return 0;
	    } 
	    
}
